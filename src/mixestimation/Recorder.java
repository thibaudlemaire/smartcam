package mixestimation;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.TargetDataLine;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;

import interfaces.MixEstimationListenerInterface;
import tools.Log;

public class Recorder {
	
	private boolean stopped = false;
	private boolean selectTest = true;
	private boolean noStop = false;
	
	private final int nbFrame = 512;
	
	/** nombre de valeur sur lesquelles on effectue la moyenne
	 * avec laquelle on comparera la valeur actuelle du poids
	 * de la piste */
	private final int intervalleMoyenne = 100;
	
	/** nombre de valeurs sur lesquelles on effectue la moyenne 
	 * pour un point de la courbe (que l'on comparera avec une 
	 * moyenne plus large, sur intervalleMoyenne points)
	 */
	private final int moyennePonctuelle = 20;
	
	/** seuil pour la cohérence de l'estimation du mix */
	private final double epsilon = 0.5;
	private final double seuilInf = 0.5;
	
	private Tore tore;
	private Tore toreMoyenne;
	
	private MixEstimation mix;
	private int[] listInstru;
	private int globalIndex;
	
	public Recorder(MixEstimation mix, int[] listInstru, int globalIndex) { 
		this.mix = mix; 
		this.listInstru = listInstru;
		this.globalIndex = globalIndex;
		this.tore = new Tore(listInstru.length, 1000);
		this.toreMoyenne = new Tore(listInstru.length, 1000);
	}
	
	public void record() {
		Mixer.Info[] infos = AudioSystem.getMixerInfo();
		boolean firestudioFound = false;
		for (Mixer.Info elt : infos) {
			
			if (elt.toString().equals("Project [plughw:0,0], version 4.9.0-2-amd64")
					|| elt.toString().equals("Project [plughw:1,0], version 4.9.0-2-amd64")) {
				
				Log.logger.info("mixestimation : Firestudio found");
				firestudioFound = true;
				
				TargetDataLine source = null;
				AudioFormat format = new AudioFormat(44100, 8, 8, true, false);
				try {
					source = AudioSystem.getTargetDataLine(format, elt);
					source.open();
				} catch (LineUnavailableException e) {
					Log.logger.severe("mixestimation : LineUnavailableException (Recorder) \n" + e.toString());
				}
				
				int numBytesRead;
				byte[] data = new byte[(source.getBufferSize()/40)*8];

				source.start();

				double[][] pistes = new double[listInstru.length][nbFrame];
				double[] moyennes = new double[listInstru.length];
				
				byte[] globalRecord = new byte[data.length/8];
				
				double[] global = new double[nbFrame];
				
				
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				int iteratorStop = 0;
				
				int k = 0;
				int m = 0;
				
				int idInstru;
				int previousIdInstru = -1;
				int lastIdInstru = -1;
				
				while (!stopped) {

				   numBytesRead =  source.read(data, 0, data.length);
				   
				   for (int i=0 ; i < globalRecord.length ; i++) {
					   globalRecord[i] = data[8*i + globalIndex];
					   if (i < nbFrame) global[i] = (new Byte(globalRecord[i])).doubleValue();
				   }
				   
				   for (int j=0 ; j < listInstru.length ; j++) {
					   moyennes[j] = 0;
					   for (int i=0 ; i < nbFrame ; i++) {
						   pistes[j][i] = (new Byte(data[8*i + listInstru[j]])).doubleValue();
						   moyennes[j] += pistes[j][i]*pistes[j][i];
					   }
					   moyennes[j] = ((double) moyennes[j])/((double) nbFrame);
				   }
				   
				   tore.addValues(estimationDuMix(pistes, global));
				   toreMoyenne.addValues(moyennes);
				   
				   idInstru = EDMMoyenneGlissante(k, m, lastIdInstru);
				   k ++;
				   if (idInstru == -1 || previousIdInstru != idInstru) {
					   m = k;
					   previousIdInstru = idInstru;
					   if (idInstru != -1) lastIdInstru = idInstru;
				   }
				   
				   if (selectTest) out.write(globalRecord, 0, globalRecord.length);
				   
				   if (noStop) {
					   iteratorStop ++;
					   if (iteratorStop>3000) break;
				   }
				   
				   
				}
				
				Log.logger.info("mixestimation : fin de l'acquisition audio");				
				source.stop();
				
				if (selectTest) recordGlobal(out) ;
				
			}
		}
		if (firestudioFound == false) Log.logger.severe("mixestimation : Firestudio not found");
	}
	
	
	private double[] estimationDuMix(double[][] pistes, double[] global) {
		
		//Do the TF
		double[] globalTF = Signal.transform(global, true, true);
		double[][] pistesTF = new double[pistes.length][pistes[0].length];
		for (int i = 0 ; i < pistes.length ; i++) {
			pistesTF[i] = Signal.transform(pistes[i], true, true);
		}
		
		//Do the NMF
		NMFTransformer nmf = new NMFTransformer(2, 0.00001, pistes.length);
		Array2DRowRealMatrix W = (Array2DRowRealMatrix) (new Array2DRowRealMatrix(pistesTF)).transpose();
		double[] res = nmf.NMF(globalTF, W);
		
		return res;
		
	}
	
	
	private int EDMMoyenneGlissante(int k, int m, int lastIdInstru) {
		int n = listInstru.length;
		double[] estimations = new double[n];
		double[] moyennes = new double[n];
		double[] ecartTypes = new double[n];
		
		if (tore.use < moyennePonctuelle) return -1;
		
		for (int j = 0 ; j < n ; j++) {
			
			double x = 0;
			for (int i = m ; i > m - moyennePonctuelle ; i--) x += tore.getValue(j, i) + toreMoyenne.getValue(j, i);
			x = x / moyennePonctuelle;
			
			for (int i = k-1 ; i>=0 && i >= k - intervalleMoyenne ; i--) {
				moyennes[j] += tore.getValue(j, i) + toreMoyenne.getValue(j, i);
			}
			moyennes[j] = moyennes[j]/(Math.min(k, intervalleMoyenne));
			
			
			for (int i = k-1 ; i>=0 && i >= k - intervalleMoyenne ; i--) {
				ecartTypes[j] += Math.pow((tore.getValue(j, i) + toreMoyenne.getValue(j, i) - moyennes[j]), 2);
			}
			ecartTypes[j] = Math.sqrt(ecartTypes[j] / Math.min(k, intervalleMoyenne));
			
			
			estimations[j] = (x - moyennes[j]) / ecartTypes[j];
		}
		
		double max = estimations[0];
		int indice = 0;
		for (int j = 0 ; j < n ; j++) {
			if (max < estimations[j]) {
				max = estimations[j];
				indice = j;
			}
		}
		
		estimations[0] = 0;
		double second = estimations[0];
		for (int j = 0 ; j < n ; j++) {
			if (second < estimations[j]) {
				second = estimations[j];
			}
		}
		
		if (max < seuilInf) return -1;
		
		double coherence = ( (max - second) / max ) * ((1 - 1 / (m - k - 1)) * (1 - epsilon) + epsilon);
		int relevance = (int) coherence * 100;
		
		if (indice != lastIdInstru) {
			for (MixEstimationListenerInterface listener : mix.getListeners().getListeners(MixEstimationListenerInterface.class)) {
				listener.MixReceive(mix.getZones().get(indice), relevance);
				System.out.println(relevance);
			}
		}
		
		return indice;
	}
	
	
	private void recordGlobal(ByteArrayOutputStream out) {
		byte[] buffer = out.toByteArray();
		
		try {
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		ByteArrayInputStream in = new ByteArrayInputStream(buffer);
		
		AudioFormat newFormat = new AudioFormat(44100, 8, 1, true, false);
		
		AudioInputStream writingStream = new AudioInputStream(in, newFormat, buffer.length/2);
		
		try {
			AudioSystem.write(writingStream, AudioFileFormat.Type.WAVE, new File("data/enregistrement.wav"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void stop() {
		stopped = true;
	}

}

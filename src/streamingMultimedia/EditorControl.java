package streamingMultimedia;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;

import tools.Point;

/**
 * EditorControl est une classe utilitaire qui permet de selectionner
 * la camera a enregistrer et de bouger la fenetre de crop
 * Elle doit etre initialisee a l'ouverture du stream
 * 
 * @author nathan
 *
 */


public class EditorControl {
	private final String cropPath;
	private final String streamSelectPath;
	private int recordedStream = 0;
	
	public EditorControl(String cropPath, String streamSelectPath){
		this.cropPath = cropPath;
		this.streamSelectPath = streamSelectPath;
		this.selectCamera(0);
	}
	
	/**
	 * Cette fonction permet de bouger le crop en x et y donnes
	 * Attention, le point passe en parametre est le coin en haut a gauche de la fenetre de crop
	 * @param coinHautGaucheCrop
	 */
	public final void moveCrop(Point coinHautGaucheCrop){
		int x = coinHautGaucheCrop.getX();
		int y = coinHautGaucheCrop.getY();
		PrintWriter pw = null;
		String newCoordinates = Integer.toString(x) + " " + Integer.toString(y);
		try{
			FileWriter fw = new FileWriter(cropPath, false);  	//On oblige le fichier à être réécrit en mettant false ici, si on met true, on ajoute juste de nouvelle modification si le fichier existe déjà
			BufferedWriter bw = new BufferedWriter(fw);
			pw = new PrintWriter(bw);
			pw.print(newCoordinates);
		}catch(Exception e){
			e.printStackTrace();
			System.out.println("Error" + e);
		} finally{
			try{
				pw.close();
			} catch(Exception e){
				e.printStackTrace();
				System.out.println("Error" + e);
			}
		}
	}
	
	/**
	 * Cette fonction permet d'ecrire le fichier Selectsream.txt
	 * et donc choisir la camera qui doit etre enregistree
	 * les cameras pouvant etre selectionnee sont :
	 * 		- 0 = 4K
	 * 		- 1 = crop
	 * 		- 2 = HD
	 * @param camera
	 */
	public final void selectCamera(int camera){
		PrintWriter pw = null;
		String cameraNumber = Integer.toString(camera);
		try{
			FileWriter fw = new FileWriter(streamSelectPath, false);  	//On oblige le fichier à être réécrit en mettant false ici, si on met true, on ajoute juste de nouvelle modification si le fichier existe déjà
			BufferedWriter bw = new BufferedWriter(fw);
			pw = new PrintWriter(bw);
			pw.print(cameraNumber);
		}catch(Exception e){
			e.printStackTrace();
			System.out.println("Error" + e);
		} finally{
			try{
				pw.close();
				recordedStream = camera;
			} catch(Exception e){
				e.printStackTrace();
				System.out.println("Error" + e);
			}
		}
	}
	
	/**
	 * Methode renvoyant le numero de la camera qui est recordee
	 * @return
	 */
	public final int getRecordedStream(){
		return recordedStream;
	}
}

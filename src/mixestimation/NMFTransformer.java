package mixestimation;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;

import tools.Log;

/** Implemente la NMF qui trouve H 
 * tel que V=WH pour V et W donne
 */
public class NMFTransformer {
	
	/** beta de la beta-divergence */
	private double beta;
	
	/** marge d'erreur acceptee */
	private double epsilon;
	
	/** valeur de H de depart
	 * sous forme d'un tableau de flottants*/
	private double[] start;
	
	/** taille de H */
	@SuppressWarnings("unused")
	private final int n;
	
	/** limite de boucle
	 * la methode peut ne pas converger */
	private final int lim = 1000;
	
	
	
	/** Constructeur sans H de depart (valeur par defaut : que des 2)
	 * @param beta
	 * @param epsilon
	 * @param n
	 */
	public NMFTransformer(double beta, double epsilon, int n) {
		this.beta = beta;
		this.epsilon = epsilon;
		this.n = n;
		double[] start = new double[n];
		for (int i = 0; i < n; i ++) {
			start[i] = 2;
		}
		this.start = start;
	}
	
	
	/** Constructeur avec start donne 
	 * @param beta
	 * @param epsilon
	 * @param start
	 */
	public NMFTransformer(double beta, double epsilon, double[] start) {
		this.beta = beta;
		this.epsilon = epsilon;
		this.start = start;
		this.n = start.length;
	}
	
	
	/** Calcule H
	 * @param V
	 * @param W
	 * @return
	 */
	public double[] NMF(double[] V, Array2DRowRealMatrix W) {
		double[] H = start;
		double[] Hprim = H;
		int compteur = 0;
		do {
			Hprim = H;
			double[] Vprim = W.operate(H);
			Array2DRowRealMatrix tW = (Array2DRowRealMatrix) W.transpose();
			double[] A = Signal.ebePower(Vprim, beta-2);
			double[] B = Signal.ebePower(Vprim, beta-1);
			double[] num = tW.operate(Signal.ebeMultiply(A, V));
			double[] denom = tW.operate(B);
			H = Signal.ebeMultiply(H, Signal.ebeDivide(num, denom));
			if (compteur >= lim) {
				break; //TODO trouver mieux...
			}
			compteur ++;
		} while (Signal.L1Norm(Signal.subtract(H, Hprim)) > epsilon);
		return H;
	}
	
	public double[] NMFRecurs(double[] V, Array2DRowRealMatrix W) {
		double[] H = start;
		double[] Hprim = new double[H.length];
		for (int i = 0; i < H.length; i++)  {
			Hprim[i] = H[i] + 2 * epsilon;
		}
		int compteur = 0;
		return NMFRecurs2(V, W, H, Hprim, compteur);
	}
	
	private double[] NMFRecurs2(double[] V, Array2DRowRealMatrix W, double[] H, double[] Hprim, int compteur) {
		if (Signal.L1Norm(Signal.subtract(H,Hprim)) <= epsilon) {
			return H;
		}
		else if (compteur > 1000) {
			Log.logger.warning("mixestimation : NMF : limite de boucle atteinte");
			return H; //TODO trouver mieux...
		}
		else {
			Hprim = H;
			double[] Vprim = W.operate(H);
			Array2DRowRealMatrix tW = (Array2DRowRealMatrix) W.transpose();
			double[] A = Signal.ebePower(Vprim, beta-2);
			double[] B = Signal.ebePower(Vprim, beta-1);
			double[] num = tW.operate(Signal.ebeMultiply(A, V));
			double[] denom = tW.operate(B);
			H = Signal.ebeMultiply(H, Signal.ebeDivide(num, denom));
			compteur ++;
			return NMFRecurs2(V, W, H, Hprim, compteur);
		}
		
	}
}

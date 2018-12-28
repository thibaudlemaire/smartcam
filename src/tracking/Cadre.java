package tracking;
import org.opencv.core.Mat;
import org.opencv.core.Rect;

public class Cadre {
		private Mat imageCroppe;
		private int xCoin;
		private int yCoin; 
		public Cadre(Mat imageOrigine,int xCoin,int yCoin, int largeur, int longueur)
		{
			this.xCoin=Math.max(0,Math.min(xCoin,imageOrigine.cols()-largeur));
			this.yCoin=Math.max(0,Math.min(yCoin,imageOrigine.rows()-longueur));
			this.imageCroppe=new Mat(imageOrigine, new Rect(Math.max(0,Math.min(xCoin,imageOrigine.cols()-largeur)),Math.max(0,Math.min(yCoin,imageOrigine.rows()-longueur)),largeur,longueur));
		}
		public final Mat getImageCroppe()
		{
			return imageCroppe;
		}
		public final int getXCoin()
		{
			return xCoin;
		}
		public final int getYCoin()
		{
			return yCoin;
		}
}

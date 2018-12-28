package materiel;

import javax.media.jai.InterpolationBilinear;

import tools.Orientation;
import tools.Point;

/**
 * Cette classe permet de convertir des points en orientation pour la caméra mobil
 * Utilise une approximation linéaire, valable si la caméra mobile est située loin de la scène
 * @author thibaud
 *
 */
public class PanTiltConverter {
	
	private InterpolationBilinear interpolator;
	private int pans[][];
	private int tilts[][];
	private final int subsamplebits = 10;
	/**
	 * Constructeur du convertisseur
	 * @param points
	 * @param orientations
	 */
	public PanTiltConverter(Orientation topLeft, Orientation topRight, Orientation bottomLeft, Orientation bottomRight)
	{
		interpolator = new InterpolationBilinear(subsamplebits);
		pans = new int[2][2];
		tilts = new int[2][2];
		pans[0][0] = topLeft.getPan();
		tilts[0][0] = topLeft.getTilt();
		pans[0][1] = topRight.getPan();
		tilts[0][1] = topRight.getTilt();
		pans[1][0] = bottomLeft.getPan();
		tilts[1][0] = bottomLeft.getTilt();
		pans[1][1] = bottomRight.getPan();
		tilts[1][1] = bottomRight.getTilt();
	}
	
	/**
	 * Cette fonction transforme un point en une orientation de la caméra mobile
	 * @param point
	 * @return
	 */
	public Orientation getPanTilt(Point point)
	{
		int pan, tilt;
		int x = (int) (point.getX()*(Math.pow(2,  subsamplebits))/Point.maxX);
		int y = (int) (point.getY()*(Math.pow(2, subsamplebits))/Point.maxY);
		pan = interpolator.interpolate(pans, x, y);
		tilt = interpolator.interpolate(tilts, x, y);
		return new Orientation(pan, tilt);
	}
}

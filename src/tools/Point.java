package tools;

import java.io.Serializable;

import exceptions.OutofFrameException;

/**
 * Point dans le référentiel de la caméra 4K
 * Teste l'appartenance au champs de la caméra et lève une exception si nécessaire
 * + Outils
 * @author thibaud
 *
 */
public class Point implements Serializable {

	
	private static final long serialVersionUID = 1L;
	public static final int maxX = Constants.FIXED_CAM_WIDTH;
	public static final int maxY = Constants.FIXED_CAM_HEIGHT;
	private final int x;
	private final int y;
	
	public Point(int x, int y) throws OutofFrameException
	{
		if(x <= maxX && y <= maxY)
		{
			this.x = x;
			this.y = y;
		}
		else 
			throw new OutofFrameException(x, y, maxX, maxY);
	}
	
	public int getX()
	{
		return x;
	}
	
	public int getY()
	{
		return y;
	}
	
	/**
	 * Cette foncton retourne le barycentre des points a et b
	 * @param a
	 * @param b
	 * @return
	 */
	public static Point barycentre(Point a, Point b)
	{
		Point barycentre = null;
		try 
		{
			barycentre = new Point( (a.getX() + b.getX()) / 2, (a.getY() + b.getY()) / 2);
		} 
		catch (OutofFrameException e)
		{ }
		return barycentre;
	}
}

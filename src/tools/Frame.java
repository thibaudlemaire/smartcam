package tools;

import java.io.Serializable;

import exceptions.OutofFrameException;

/** Outil de cadrage pour la caméra 4K ou pour le tracking
 * @author felixgaschi
 *
 */
public class Frame implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private Point A;
	private Point B;
	
	
	public Frame(int x1, int y1, int width, int height) throws OutofFrameException {
		this.A = new Point(x1, y1);
		this.B = new Point(x1 + width, y1 + height);
	}

	public Frame(Point point, int width, int height) {
		this.A = point;
		try {
			this.B = new Point(point.getX() + width, point.getY() + height);
		} catch (OutofFrameException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public Frame(Point topLeft, Point bottomRight)
	{
		this.A = topLeft;
		this.B = bottomRight;
	}
	
	public Point getPointA() {
		return A;
	}
	
	public Point getPointB() {
		return B;
	}
	
	public int getHeight() {
		return B.getY() - A.getY();
	}
	
	public int getWidth() {
		return B.getX() - A.getX();
	}
}

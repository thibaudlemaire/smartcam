package exceptions;

import tools.Log;

public class OutofFrameException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Exception levée quand un objet de type Point sort du cadre définit par la caméra 4K
	 * @param desiredX
	 * @param maxX
	 */
	public OutofFrameException(int desiredX, int desiredY, int maxX, int maxY)
	{
		Log.logger.warning("Attention ! "
				+ "(" + desiredX + "," + desiredY + ") est en dehors"
				+ "du cadre défini par (0,0) (" + maxX + "," + maxY +")");
	}
}

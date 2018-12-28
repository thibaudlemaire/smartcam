package interfaces;

import tools.Point;
import tools.Orientation;

/**
 * Interface de controle du pied robotisé de la camera mobile
 * @author thibaud
 *
 */
public interface MotorizedCameraInterface {

	/**
	 * Ces fonction seront utlisées quand le module n'est pas encore initialisé
	 * pour rechercher les points de calibration
	 */
	public void goUp();
	public void goDown();
	public void goLeft();
	public void goRight();
	
	/**
	 * Cette fonction permet de viser un point particulier le plus vite possible
	 * Cette fonctione est bloquante tant que la position n'est pas atteinte
	 * @param pointToAim, dans le référentiel de la caméra 4K
	 */
	public void setAim(Point pointToAim);
	/**
	 * Idem que précédement mais avec un réglage de vitesse en pourcentage
	 * @param pointToAim
	 * @param speed, en %
	 */
	public void setAim(Point pointToAim, int speed);
	/**
	 * Fonction qui permet de récupérer le point actuellement visé
	 * @return point vidé
	 */
	public Point getAim();
	
	/**
	 * Fonction qui permet de modifier le zoom de la caméra mobile
	 * @param newZoom, en %
	 */
	public void setZoom(int newZoom);
	/**
	 * Idem mais avec réglage de la vitesse en %
	 * @param newZoom, en %
	 * @param speed, en %
	 */
	public void setZoom(int newZoom, int speed);
	/**
	 * Permet de récupérer la valeur du zoom actuel en %
	 * @return, zoom en %
	 */
	public int getZoom();

	/** 
	 * Permet de modifier la mise au point, format à définir
	 * @param newFocus, format à définir
	 */
	public void setFocus(int newFocus);
	/**
	 * Permet de récupérer la valeur actuelle du focus
	 * @return [format à définir]
	 */
	public int getFocus();
	
	/**
	 * Permet de récupérer l'orentation, pan et tilt, de la caméra
	 * Cette fonction sera principalement utilisée pour initialiser le module
	 * @return
	 */
	public Orientation getOrientation();
	
	
	/**
	 * Cette fonction est utilisée pour initialiser le module
	 * @param points, points dans le repère de la caméra 4K
	 * @param orientations, pans et tilts correspondants
	 */
	public void initLocator(Orientation topLeft, Orientation topRight, Orientation bottomLeft, Orientation bottomRight);
	
	/**
	 * Fonction utilisée pour initialiser le zoom de la camera
	 */
	public void initCamera();
	
	/**
	 * Fonction pour arreter le module
	 */
	public void stop();
	
}

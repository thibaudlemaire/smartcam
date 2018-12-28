package materiel;

import exceptions.OutofFrameException;
import interfaces.MotorizedCameraInterface;
import tools.Constants;
import tools.Orientation;
import tools.Point;

/**
 * Classe de commande de la caméra mobile en orientation et en zoom
 * @author thibaud
 *
 */
public class MotorizedCamera implements MotorizedCameraInterface{

	private static final int panDmxChannel = 1; 	// Choix du canal DMX du mouvement Panoramique
	private static final int tiltDmxChannel = 3;	// idem pour le Tilt
	private Dmx dmx;								// Interface DMX	
	private PanTiltConverter converter;				// Interpolateur pour convertir un position sur l'image en orientation de la caméra
	private CameraController camera;				// Controlleur de la caméra (carte arduino) pour piloter le zoom
	private Point currentAim = null;				// Mémoire du point visé
	
	/**
	 * Constructeur de l'objet.
	 * Initialise le controleur et l'interface DMX
	 */
	public MotorizedCamera()
	{
		if (Constants.ZOOM_CONTROL_ON) camera = new CameraController();
		dmx = new Dmx(Dmx.DMX_OUT);
		dmx.open("tty.usbserial-EN198559");
	}
	
	@Override
	public void initCamera() {
		if (Constants.ZOOM_CONTROL_ON) camera.initCamera();
		try {
			setAim(new Point(Constants.FIXED_CAM_WIDTH/2, Constants.FIXED_CAM_HEIGHT/2));
		} catch (OutofFrameException e) {
			e.printStackTrace();
		}
	}
	
	// Fonctions d'orientation de la caméra "à l'aveugle"
	// Utilisé lors de l'initialisation 
	
	/**
	 * Fonction pour élevé la caméra
	 */
	@Override
	public void goUp() {
		sendDmx(getOrientation().addToTilt(1));
	}
	
	/**
	 * Fonction pour abaisser la caméra
	 */
	@Override
	public void goDown() {
		sendDmx(getOrientation().addToTilt(-1));		
	}
	
	/**
	 * Fonction pour orienter la caméra à gauche
	 */
	@Override
	public void goLeft() {
		sendDmx(getOrientation().addToPan(-1));		
	}
	
	/**
	 * Fonction pour orienter la caméra à droite
	 */
	@Override
	public void goRight() {
		sendDmx(getOrientation().addToPan(1));				
	}
	
	/**
	 * Fonction pour indiquer un point de visée
	 */
	@Override
	public void setAim(Point pointToAim) 
	{
		currentAim = pointToAim;
		sendDmx(converter.getPanTilt(pointToAim));
	}

	/**
	 * Fonction pour orienter la caméra avec une vitesse déterminée
	 */
	@Override
	public void setAim(Point pointToAim, int speed) {
		// TODO 		
	}

	/**
	 * Fonction qui renvoie le point actuellement visé
	 */
	@Override
	public Point getAim() {
		return currentAim;
	}

	/**
	 * Fonction pour modifier le zoom de la caméra
	 */
	@Override
	public void setZoom(int zoom)
	{
		if(zoom < 0 & zoom > camera.maxZoom)
			return;
		int delta = zoom - camera.getZoom();
		if(delta > 0)
			for(int i=0; i<delta; i++)
				camera.zoomUp();
		else
			for(int i=0; i<-delta; i++)
				camera.zoomDown();
	}

	/**
	 * Fonction pour modifier le zoom de la caméra avec une vitesse fixée
	 */
	@Override
	public void setZoom(int newZoom, int speed) {
		// TODO
	}

	/**
	 * Fonction qui renvoie le zoom actuel de la caméra
	 */
	@Override
	public int getZoom() {
		if (Constants.ZOOM_CONTROL_ON) return camera.getZoom();
		return -1;
	}

	/**
	 * Fonction qui permet de modifier le focus de la caméra
	 */
	@Override
	public void setFocus(int focus)
	{
		if(focus < 0 & focus > camera.maxFocus)
			return;
		int delta = focus - camera.getFocus();
		if(delta > 0)
			for(int i=0; i<delta; i++)
				camera.focusUp();
		else
			for(int i=0; i<-delta; i++)
				camera.focusDown();
	}

	/**
	 * Fonction qui renvoie le focus actuel de la caméra
	 */
	@Override
	public int getFocus() {
		if (Constants.ZOOM_CONTROL_ON) return camera.getFocus();
		return -1;
	}

	/**
	 * Fonction qui renvoie l'orientation actuelle de la caméra
	 */
	@Override
	public Orientation getOrientation() 
	{
		return new Orientation(dmx.getDMXOutput(panDmxChannel), dmx.getDMXOutput(tiltDmxChannel));
	}

	/**
	 * Fonction qui permet d'initialiser l'interpolateur
	 */
	@Override
	public void initLocator(Orientation topLeft, Orientation topRight, Orientation bottomLeft, Orientation bottomRight) 
	{
		converter = new PanTiltConverter(topLeft, topRight, bottomLeft, bottomRight);
	}

	/**
	 * Fonction qui envoie la nouvelle position de la caméra à l'interface DMX
	 * @param orientation
	 */
	private void sendDmx(Orientation orientation)
	{
		dmx.setDMXOutput(panDmxChannel, orientation.getPan(), false);
		dmx.setDMXOutput(tiltDmxChannel, orientation.getTilt(), true);
	}

	@Override
	public void stop() 
	{
		dmx.close();
		if (Constants.ZOOM_CONTROL_ON) camera.close();
	}
	
}

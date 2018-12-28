package interfaces;

import tools.Frame;

public interface TrackerInterface extends Runnable {

	/** 
	 * Initialise le tracker
	 * @param pointsFile
	 * @param framesFolder
	 * @param format
	 */
	public void trackerInit(String folderPath);
	
	/**
	 * Lance le tracking
	 * @param topLeftPoint
	 * @param width
	 * @param height
	 */
	public boolean startTracking(Frame frame);
	
	/**
	 * Stoppe le tracking
	 */
	public void stopTracking();
	
	/** 
	 * Fonction qui ajoute un listener
	 * Appelée par le coeur décisionnel
	 * @return
	 */
	public void setListener(TrackerListenerInterface l);
	
	/**
	 * Fonction qui supprime un listener
	 * @param l
	 */
	public void unsetListener(TrackerListenerInterface l);
	
	/**
	 * Fonction pour arreter le module
	 */
	public void stop();
}

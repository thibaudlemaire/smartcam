package interfaces;

import tools.Zone;

/** 
 * Interface de l'estimateur d'éclairage
 * @author thibaud
 *
 */
public interface LightEstimationInterface extends Runnable {

	/**
	 * Fonction d'initialisation du module
	 */
	public void initLightEstimationInterface();
	
	/** Fonction qui ajoute un listener
	 * @return
	 */
	public void setListener(LightEstimationListenerInterface l);

	/**
	 * Fonction qui supprime un listener
	 * @param l
	 */
	public void unsetListener(LightEstimationListenerInterface l);
	
	/**
	 * Cette fonction ajoute une zone à l'estimateur de lumière
	 * L'objet zone encapsule le canal DMX à écouter 
	 * @param zone
	 */
	public void addZone(Zone zone);
	
	/**
	 * Cette fonction supprime une zone  
	 * @param id
	 */
	public void delZone(int id);
	
	
	/**
	 * Fonction pour arreter le module
	 */
	public void stop();

}

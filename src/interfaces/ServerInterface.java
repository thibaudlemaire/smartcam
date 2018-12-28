package interfaces;

public interface ServerInterface extends Runnable {

	/**
	 * Cette fonction initialise le module serveur
	 * @param port
	 * @param ihm
	 */
	void initServer(int pcPort, int androidPort, IhmInterface ihm);

	/**
	 * Fonction pour arreter le module
	 */
	public void stop();
	
	/**
	 * Fonction pour envoyer à l'appli un changement de zone
	 * @param idZone
	 */
	void sendZoneChange(int idZone);
	
	/**
	 * Fonction pour envoyer à l'appli un changement de flux
	 * @param id
	 */
	void sendStreamChange(int id);
}

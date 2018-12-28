package interfaces;

import tools.Frame;

/**
 * Interface du monteur video
 * @author thibaud
 *
 */
public interface EditorInterface extends Runnable {

	/**
	 * Cette fonction permet d'initialiser le module
	 */
	public void initEditorModule();
	
	/*
	 * Cette fonction permet d'utiliser la caméra mobile en sortie video
	 */
	public void useMobileCam();
	
	/**
	 * Cette fonction permet d'utiliser une cadrage prédéfini de la caméra 4K en 
	 * sortie vidéo
	 * @param frame
	 */
	public void useFixedCam();
	
	/**
	 * Cette fonction permet de choisir le caméra mobile
	 */
	public void usePanCam();
	
	/**
	 * Cette fonction est utilisée pour définir la zone de crop
	 * @param frame
	 */
	public void setCrop(Frame frame);
	
	/**
	 * Cette fonction commence l'enregistrement
	 * @param outputFile
	 * @return
	 */
	public boolean startRecording(String outputFile);
	
	/**
	 * Stoppe l'enregistrement
	 */
	public void stopRecording();
	
	/**
	 * Fonction pour arreter le module
	 */
	public void stop();
	
	/** 
	 * Fonction qui renvoie un entier correspondant
	 * au flux enregistre
	 */
	public int getRecordedStream();
}

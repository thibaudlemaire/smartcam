package interfaces;

import java.util.EventListener;

import tools.Zone;

/**
 * Interface du listener de l'estimation d'éclairage
 * Implémantée par le coeur décisionnel
 * @author thibaud
 *
 */
public interface LightEstimationListenerInterface extends EventListener {
	
	/**
	 * Méthode appelé quand la lumière sur le plateau change pour mettre en avant une zone
	 * @param highlightedZone
	 * @param relevance, en %, plus ce nombre est élevé, plus la zone est mise en valeur
	 */
	public void LightEstimationChanged(Zone highlightedZone, int relevance);
}

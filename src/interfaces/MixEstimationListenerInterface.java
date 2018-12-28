package interfaces;

import java.util.EventListener;

import tools.Zone;

/** Listener de l'estimation du mix, implémenté
 * par le coeur décisionnel
 * @author felixgaschi
 *
 */
public interface MixEstimationListenerInterface extends EventListener {
	
	/**
	 * Fonction appelée quand l'instrument mis en avant change
	 * @param highlightedZone
	 * @param relevance, en %, plus le nombre est élevé, plus l'instrument est mis 
	 * en valeur
	 */
	public void MixReceive(Zone highlightedZone, int relevance);

}

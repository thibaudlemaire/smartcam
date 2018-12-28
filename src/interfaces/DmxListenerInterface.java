package interfaces;

import java.util.EventListener;

public interface DmxListenerInterface extends EventListener
{
	/**
	 * Méthode appelé quand la lumière sur le plateau change pour mettre en avant une zone
	 * @param highlightedZone
	 * @param relevance, en %, plus ce nombre est élevé, plus la zone est mise en valeur
	 */
	public void dmxInputChanged();
}


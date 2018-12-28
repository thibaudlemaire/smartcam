package interfaces;

import java.util.EventListener;

import tools.Point;

public interface TrackerListenerInterface extends EventListener{
	/**
	 * Fonction appelée quand le tracking détecte un mouvement
	 */
	public void newTrackingPosition(Point centerPoint);

}

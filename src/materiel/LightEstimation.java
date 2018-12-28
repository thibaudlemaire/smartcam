package materiel;

import java.util.Hashtable;
import java.util.Map;

import javax.swing.event.EventListenerList;

import interfaces.DmxListenerInterface;
import interfaces.LightEstimationInterface;
import interfaces.LightEstimationListenerInterface;
import tools.Log;
import tools.Zone;

/**
 * Classe d'estimation de l'éclairage
 * 
 * @author thibaud
 *
 */
public class LightEstimation implements LightEstimationInterface, DmxListenerInterface
{

	private Hashtable<Integer, Zone> zonesToTrack = new Hashtable<Integer, Zone>();
	private final EventListenerList listeners = new EventListenerList();	// Fonction où propager les évènements
	private Zone currentHighlightedZone = null;								// Mémoire de la zone mise en valeur
	private Dmx dmx;														// Interface DMX
	private boolean loop = true;											// Booléen d'arret du Thread
	private boolean dmxChanged = false;										// Drapeau de changement sur les entrées DMX
	private final static int RELEVANCE_COEF = 2;
	
	/**
	 * Constructeur de l'estimateur
	 * @param dmx
	 */
	public LightEstimation()
	{
		dmx = new Dmx(Dmx.DMX_IN);
		dmx.open("tty.usbserial-EN198886");
	}
	
	/**
	 * Fonction appelée pour l'initialisation du module 
	 */
	@Override
	public void initLightEstimationInterface() {
		int[] channelToMonitor = new int[zonesToTrack.size()];
		int i = 0;
		for(Map.Entry<Integer, Zone> entry : zonesToTrack.entrySet())
		{
			channelToMonitor[i] = entry.getValue().getDmxChannel();
			i++;
		}
		Log.logger.info("Mise à jour des cannaux DMX à analyser");
		dmx.setListener(this, channelToMonitor);
	}

	/**
	 * Fonction permettant d'ajouter un listener 
	 */
	@Override
	public void setListener(LightEstimationListenerInterface l) {
		listeners.add(LightEstimationListenerInterface.class, l);
	}

	/**
	 * Fonction permettant de supprimer un listener
	 */
	@Override
	public void unsetListener(LightEstimationListenerInterface l) {
		listeners.remove(LightEstimationListenerInterface.class, l);		
	}

	/**
	 * Fonction permettant d'ajouter une zone à analyser par l'estimateur
	 * La canal DMX à monitorer est encapsulé dans l'objet Zone
	 */
	@Override
	public void addZone(Zone zone) 
	{
		Log.logger.info("Ajout de la zone "+zone.getId()+" à l'estimateur d'éclairage");
		zonesToTrack.put(zone.getId(), zone);
		initLightEstimationInterface();
	}
	

	@Override
	public void delZone(int id) {
		zonesToTrack.remove(id);
	}

	/**
	 * Cette fonction est appelé lorsque les entrées DMX monitorées détèctent un changement
	 */
	@Override
	public void dmxInputChanged()
	{
		dmxChanged = true;
		synchronized(this) {
			this.notify();
		}
	}

	/**
	 * Thread d'analyse des modifications de niveaux DMX
	 * Ce thread attend une modification des entrées DMX et la traite
	 */
	@Override
	public void run() {
		
		while(loop)
		{
			if (dmxChanged)
			{
				Zone highlightedZone = null; 
				int highlightedValue = 0;
				for(Map.Entry<Integer, Zone> entry : zonesToTrack.entrySet())
				{
					int value = dmx.getDMXInput(entry.getValue().getDmxChannel());
					if(value > highlightedValue)
					{
						highlightedZone = entry.getValue();
						highlightedValue = value;
					}
				}
				currentHighlightedZone = highlightedZone;
				for(LightEstimationListenerInterface listener : listeners.getListeners(LightEstimationListenerInterface.class))
				{
		            listener.LightEstimationChanged(currentHighlightedZone, 100);
				}
				dmxChanged = false;
			} else
				try {
					synchronized(this){
						this.wait(1000);
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
		}
	}
	
	/**
	 * Fonction permettant de stopper le Thread
	 */
	@Override
	public void stop()
	{
		loop = false;
		dmx.close();
	}

}

package mixestimation;

import java.util.Hashtable;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.event.EventListenerList;

import org.apache.commons.math3.util.MultidimensionalCounter.Iterator;
import org.math.plot.*;

import interfaces.MixEstimationInterface;
import interfaces.MixEstimationListenerInterface;
import tools.Log;
import tools.Zone;

public class MixEstimation implements MixEstimationInterface {

	
	private EventListenerList listeners = new EventListenerList();
	private Hashtable<Integer, Zone> zones = new Hashtable<Integer, Zone>();
	//private final boolean debug = false;
	//private RealTimeFileReader fileReader;
	private RealTimeEstimation RTE;
	
	public void run() {
		int[] listInstru = new int[zones.size()];
		int i = 0;
		for(Map.Entry<Integer, Zone> entry : zones.entrySet())
		{
			listInstru[i] = entry.getValue().getInstrument().getMixerChannel() - 1;
			i++;
		}
		
		RealTimeEstimation RTE = new RealTimeEstimation(this, listInstru, 7);
		this.RTE = RTE;
		
		System.out.println("lancement du recorder");
		RTE.record();
		
	}

	public void initMixEstimationModule() {
	}

	public void setListener(MixEstimationListenerInterface l) {
		listeners.add(MixEstimationListenerInterface.class, l);
		
	}

	public void unsetListener(MixEstimationListenerInterface l) {
		listeners.remove(MixEstimationListenerInterface.class, l);
		
	}

	public void addZone(Zone zone) {
		zones.put(zone.getId(), zone);
		
	}
	
	public EventListenerList getListeners() {
		return listeners;
	}

	public Hashtable<Integer, Zone> getZones() {
		return zones;
	}

	public void stop() {
		RTE.stop();
		//fileReader.stop();
	}

	@Override
	public void delZone(int id) {
		// TODO Auto-generated method stub
		
	}
	
}

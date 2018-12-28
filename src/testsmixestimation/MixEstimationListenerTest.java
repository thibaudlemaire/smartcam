package testsmixestimation;

import interfaces.MixEstimationListenerInterface;
import mixestimation.MixEstimation;
import tools.Log;
import tools.Zone;

public class MixEstimationListenerTest implements MixEstimationListenerInterface {
	
	public MixEstimation mix;
	
	
	public MixEstimationListenerTest() {
		MixEstimation mix = new MixEstimation();
		this.mix = mix;
		mix.setListener(this);
	}
	
	public void run() throws InterruptedException {
		
		Thread thread = new Thread(mix);
		thread.start();
		
		Thread.sleep(40000);
		mix.stop();
		
	}
	
	public void MixReceive(Zone highlightedZone, int relevance) {
		System.out.println("mixestimation : new estimation du mix : " + ((Integer) highlightedZone.getInstrument().getMixerChannel()).toString());
	}

}

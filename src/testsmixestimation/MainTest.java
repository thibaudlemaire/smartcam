package testsmixestimation;

import mixestimation.Instrument;
import tools.Zone;

public class MainTest {

	public static void main(String[] args) throws InterruptedException {
		Zone zone1 = new Zone(0);
		Zone zone2 = new Zone(1);
		Zone zone3 = new Zone(2);
		zone1.setInstrument(new Instrument(4, "piste audio"));
		zone2.setInstrument(new Instrument(5, "micro"));
		zone3.setInstrument(new Instrument(6, "boite a rythme"));
		
		MixEstimationListenerTest mixTest = new MixEstimationListenerTest();
		mixTest.mix.addZone(zone1);
		mixTest.mix.addZone(zone2);
		mixTest.mix.addZone(zone3);
		
		System.out.println("listener créé"); 
		
		mixTest.run();
	}

}

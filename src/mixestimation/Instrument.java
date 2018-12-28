package mixestimation;

import interfaces.InstrumentInterface;

public class Instrument implements InstrumentInterface {

	private int mixerChannel;
	
	
	public Instrument(int mixerChannel) {
		this.mixerChannel = mixerChannel;
	}
	
	public void setChannel(int mixerChannel) {
		this.mixerChannel = mixerChannel;
	}
	
	public int getMixerChannel() {
		return mixerChannel;
	}

}

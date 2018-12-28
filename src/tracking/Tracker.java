package tracking;

import java.io.IOException;

import interfaces.*;
import tools.*;

public class Tracker implements TrackerInterface {
	private RepeatAction action;
	private String folderPath;
	private boolean isTracking = false;
	private boolean loop = true;

	public Tracker() {

	}

	/**
	 * 
	 * @param pointsFile
	 * @param framesFolder
	 * @param format
	 * @throws IOException
	 */

	public void trackerInit(String folderPath) {
		this.folderPath = folderPath;
		action = new RepeatAction(folderPath);
	}

	/**
	 * 
	 * @param topLeftPoint
	 * @param width
	 * @param height
	 * @throws Exception
	 */
	public boolean startTracking(Frame startingFrame) {
		action.initFrame(startingFrame);
		isTracking = true;
		synchronized (this) {
			this.notify();
		}
		return true;
	}

	/**
	 * 
	 */
	public void stopTracking() {
		action.stopTracking();
		isTracking = false;
		if(loop){
			action = new RepeatAction(folderPath);
		}
	}

	/**
	 * 
	 * @param l
	 */
	public void setListener(TrackerListenerInterface l) {
action.setListener(l);	}

	/**
	 * 
	 * @param l
	 */
	public void unsetListener(TrackerListenerInterface l) {
action.unsetListener(l);	}

	@Override
	public void run() {
		while (loop) {
			try {
				synchronized (this) {
					this.wait();
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (isTracking) {
				action.start();
				try {
					action.join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		try {
			action.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void stop() {
		loop = false;
		this.stopTracking();
		synchronized (this) {
			this.notify();
		}
	}

}



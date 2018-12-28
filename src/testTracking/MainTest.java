package testTracking;

import interfaces.TrackerInterface;

import java.io.IOException;

import tools.Frame;
import tools.Point;
import tracking.*;

import org.opencv.core.Core;

public class MainTest extends Thread {
	static{System.loadLibrary(Core.NATIVE_LIBRARY_NAME);}
	
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		

		try {

			TrackerInterface tracker = new Tracker();
			tracker.trackerInit("folder");
			Thread trackerThread = new Thread(tracker);
			trackerThread.start();
			tracker.startTracking(new Frame(new Point(200, 200), 200, 200));

			sleep(1000000);


			tracker.stopTracking();
			tracker.stop();
			trackerThread.join();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
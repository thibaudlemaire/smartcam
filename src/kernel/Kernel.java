package kernel;

import java.util.Hashtable;
import java.util.Map;

import exceptions.OutofFrameException;
import interfaces.EditorInterface;
import interfaces.IhmInterface;
import interfaces.LightEstimationInterface;
import interfaces.LightEstimationListenerInterface;
import interfaces.MixEstimationInterface;
import interfaces.MixEstimationListenerInterface;
import interfaces.MotorizedCameraInterface;
import interfaces.ServerInterface;
import interfaces.TrackerInterface;
import interfaces.TrackerListenerInterface;
import materiel.LightEstimation;
import materiel.MotorizedCamera;
import mixestimation.Instrument;
import mixestimation.MixEstimation;
import server.Server;
import streamingMultimedia.Editor;
import tools.Constants;
import tools.Frame;
import tools.Log;
import tools.Orientation;
import tools.Point;
import tools.Zone;
import tracking.Tracker;

public class Kernel implements 	IhmInterface, 
								LightEstimationListenerInterface, 
								MixEstimationListenerInterface, 
								TrackerListenerInterface { 
	
	// Hashtable of zones
	private Hashtable<Integer, Zone> zones;
	private String outputFile;
	
	/**
	 * Modules construction
	 */
	private EditorInterface editor;
	private LightEstimationInterface lightEstimator;
	private MixEstimationInterface mixEstimator;
	private MotorizedCameraInterface camera; 
	private ServerInterface server;
	private TrackerInterface tracker;
	
	private Thread editorThread;
	private Thread lightEstimatorThread;
	private Thread mixEstimatorThread;
	private Thread serverThread;
	private Thread trackerThread;
	
	private Zone currentZone;
	private long time;
	private long minTime = 5000;	
	
	public Kernel(String outputFile)
	{
		this.outputFile = outputFile;
		if (Constants.VIDEO_ON) editor = new Editor();
		if (Constants.LIGHT_ESTIM_ON) lightEstimator = new LightEstimation();
		if (Constants.MIX_ESTIM_ON) mixEstimator = new MixEstimation();
		if (Constants.MOBILE_CAM_CONTROL_ON) camera = new MotorizedCamera();
		if (Constants.SERVER_ON) server = new Server();
		if (Constants.TRACKING_ON) tracker = new Tracker();
		
		zones = new Hashtable<Integer, Zone>();
		
		time = System.currentTimeMillis();
	}
	
	// #########################################
	// ####### KERNEL AND MODULES SETUP ########
	// #########################################
	
	/**
	 * Cette fonction initialise le logger et les modules
	 */
	public void initModules()
	{
		if (Constants.VIDEO_ON) editor.initEditorModule();
		if (Constants.LIGHT_ESTIM_ON) lightEstimator.initLightEstimationInterface();
		if (Constants.MIX_ESTIM_ON) mixEstimator.initMixEstimationModule();
		if (Constants.SERVER_ON) server.initServer(1234, 3000, this); // Server accessible on port 1234
		if (Constants.TRACKING_ON) tracker.trackerInit("folder"); // TODO : change foler
		if (Constants.MOBILE_CAM_CONTROL_ON) {
			Orientation topLeft = new Orientation(105,80);
			Orientation topRight = new Orientation(55,60);
			Orientation bottomLeft = new Orientation(105,0);
			Orientation bottomRight = new Orientation(65,0);		
			camera.initLocator(topLeft, topRight, bottomLeft, bottomRight);
			camera.initCamera();
		}
		setListeners();
	}
	
	/**
	 * Cette fonction enregistre le kernel comme listener des modules
	 */
	private void setListeners()
	{
		if (Constants.LIGHT_ESTIM_ON) lightEstimator.setListener(this);
		if (Constants.MIX_ESTIM_ON) mixEstimator.setListener(this);
		if (Constants.TRACKING_ON) tracker.setListener(this);
	}
	
	/**
	 * Cette fonction lance les threads associés aux modules
	 */
	public void startThreads()
	{
		if (Constants.VIDEO_ON) editorThread = new Thread(editor);
		if (Constants.LIGHT_ESTIM_ON) lightEstimatorThread = new Thread(lightEstimator);
		if (Constants.MIX_ESTIM_ON) mixEstimatorThread = new Thread(mixEstimator);
		if (Constants.SERVER_ON) serverThread = new Thread(server);
		if (Constants.TRACKING_ON) trackerThread = new Thread(tracker);
		
		// Name thread :
		if (Constants.VIDEO_ON) editorThread.setName("Editor");
		if (Constants.LIGHT_ESTIM_ON) lightEstimatorThread.setName("LightEstimator");
		if (Constants.MIX_ESTIM_ON) mixEstimatorThread.setName("Mix Estimator");
		if (Constants.SERVER_ON) serverThread.setName("Server");
		if (Constants.TRACKING_ON) trackerThread.setName("Tracker");
		
		// Start :
		if (Constants.VIDEO_ON) editorThread.start();
		if (Constants.SERVER_ON) serverThread.start();
	}
	
	public void startTreatment()
	{
		if (Constants.MIX_ESTIM_ON) mixEstimatorThread.start();
		if (Constants.LIGHT_ESTIM_ON) lightEstimatorThread.start();
		if (Constants.TRACKING_ON) trackerThread.start();
	}
	
	public void stopMix()
	{
		if (Constants.MIX_ESTIM_ON) {
			Log.logger.info("Arret de l'estimateur de mix");
			mixEstimator.stop();
			try {
				mixEstimatorThread.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void stopAll() throws InterruptedException
	{
		if (Constants.VIDEO_ON) {
			Log.logger.info("Arret de l'editeur");
			editor.stop();
			editorThread.join();
		}
		
		if (Constants.LIGHT_ESTIM_ON) {
			Log.logger.info("Arret de l'estimateur d'éclairage");
			lightEstimator.stop();
			lightEstimatorThread.join();
		}
		
		if (Constants.MIX_ESTIM_ON) {
			Log.logger.info("Arret de l'estimateur de mix");
			mixEstimator.stop();
			mixEstimatorThread.join();
		}
		
		if (Constants.SERVER_ON) {
			Log.logger.info("Arret du serveur");
			server.stop();
			serverThread.join();
		}
		
		if (Constants.TRACKING_ON) {
			Log.logger.info("Arret du tracker");
			tracker.stop();
			trackerThread.join();
		}
		
		if (Constants.MOBILE_CAM_CONTROL_ON) {
			Log.logger.info("Arret du module de controle de la caméra");
			camera.stop();
		}
		
		Log.logger.info("Tout est stoppé");
	}
	
	// ################################
	// ####### ZONES MANAGEMENT #######
	// ################################
	
	/**
	 * Cette fonction ajoute une Zone au kernel
	 * @param zone
	 */
	public void addZone(Zone zone)
	{
		zones.put(zone.getId(), zone);
		if (Constants.LIGHT_ESTIM_ON) lightEstimator.addZone(zone);
		if (Constants.MIX_ESTIM_ON) mixEstimator.addZone(zone);
	}
	
	/**
	 * Cette fonction supprime une Zone
	 * @param zone
	 */
	public void delZone(int id)
	{
		zones.remove(id);
		if (Constants.LIGHT_ESTIM_ON) lightEstimator.delZone(id);
		if (Constants.MIX_ESTIM_ON) mixEstimator.delZone(id);
	}
	
	/**
	 * Cette fonction propage les Zones gérées par le noyaux à tous les modules
	 */
	public void registerZones()
	{
		for(Map.Entry<Integer, Zone> entry : zones.entrySet())
		{
			if (Constants.LIGHT_ESTIM_ON) lightEstimator.addZone(entry.getValue());
			if (Constants.MIX_ESTIM_ON) mixEstimator.addZone(entry.getValue());
		}
	}
	
	// ##########################
	// ####### LISTENERS ########
	// ##########################
	
	/**
	 * Tracking Listener
	 * @param centerPoint
	 */
	@Override
	public void newTrackingPosition(Point centerPoint)
	{
		Log.logger.info("Nouvelle position tracking");
		camera.setAim(centerPoint);
	}
	
	/**
	 * MixEstimation Listener
	 * @param highlightedZone
	 * @param relevance
	 */
	@Override
	public void MixReceive(Zone highlightedZone, int relevance) {
		Log.logger.info("Nouvelle estimation du mix reçue : " + highlightedZone.getInstrument().getMixerChannel());
		estimationReceive(highlightedZone, relevance);
		
	}

	/**
	 * LightEstimation Listener
	 * @param highlightedZone
	 * @param relevance
	 */
	@Override
	public void LightEstimationChanged(Zone highlightedZone, int relevance) {
		Log.logger.info("Nouvelle estimation de l'éclairage reçue, canal " + highlightedZone.getDmxChannel() + 
				", zone " + highlightedZone.getId() + ", pertinence " + relevance);
		estimationReceive(highlightedZone, relevance);
	}
	
	
	public void estimationReceive(Zone highlightedZone, int relevance) {
		long currentTime = System.currentTimeMillis();
		if (currentTime - time > minTime) {
			currentZone = highlightedZone;
			this.time = currentTime;
			
			Log.logger.info("Changement de zone " + ((Integer) currentZone.getId()).toString());
			if (Constants.MOBILE_CAM_CONTROL_ON) camera.setAim(currentZone.getCenter());
			else Log.logger.warning("La caméra mobile n'est pas disponible");
			if (Constants.SERVER_ON) server.sendZoneChange(currentZone.getId());

			Log.logger.info("Changement de caméra : Caméra mobile");
			if (Constants.VIDEO_ON) editor.useMobileCam();
			else Log.logger.warning("L'editeur video n'est pas disponible");
			if (Constants.SERVER_ON) server.sendStreamChange(2);

			//int recordedStream = editor.getRecordedStream();
			/*if (recordedStream != 2) {
				Log.logger.info("using mobile cam");
				currentZone.setCenterAuto();
				camera.setAim(currentZone.getCenter());
				editor.useMobileCam();
			}
			else {
				Log.logger.info("using crop cam");
				editor.setCrop(currentZone.getFrame());
				editor.useFixedCam();	
			}*/
		}
	}

	// ##########################
	// ######### I H M ##########
	// ##########################
	
	/**
	 * Camera control throw IHM
	 */
	@Override
	public void cameraUp() {
		Log.logger.info("IHM : Camera Up");
		if (Constants.MOBILE_CAM_CONTROL_ON) camera.goUp();
	}

	@Override
	public void cameraDown() {
		Log.logger.info("IHM : Camera Down");
		if (Constants.MOBILE_CAM_CONTROL_ON) camera.goDown();
	}

	@Override
	public void cameraRight() {
		Log.logger.info("IHM : Camera Right");
		if (Constants.MOBILE_CAM_CONTROL_ON) camera.goRight();
	}

	@Override
	public void cameraLeft() {
		Log.logger.info("IHM : Camera Left");
		if (Constants.MOBILE_CAM_CONTROL_ON) camera.goLeft();
	}

	@Override
	public void zoom(int zoom) {
		Log.logger.info("IHM : Zoom to "+ zoom);
		if (Constants.ZOOM_CONTROL_ON) camera.setZoom(zoom);
	}

	@Override
	public void chooseZoneToAim(int idZone) {
		Log.logger.info("IMH : Select zone " + idZone);		
		if (Constants.MOBILE_CAM_CONTROL_ON) camera.setAim(zones.get(idZone).getCenter());
	}
	
	/**
	 * Editor control throw IHM
	 */
	@Override
	public void showMobileCam() {
		Log.logger.info("IHM : Show Mobile Cam");
		if (Constants.VIDEO_ON) editor.useMobileCam();
	}

	@Override
	public void showFixedCam() {
		Log.logger.info("IHM : Show Fixed Cam");
		if (Constants.VIDEO_ON) editor.useFixedCam();
	}

	@Override
	public void showPanoramicCam() {
		Log.logger.info("IHM : Show Panoramic Cam");
		if (Constants.VIDEO_ON) editor.usePanCam();
	}

	@Override
	public void startRecording() {
		Log.logger.info("IHM : Start Recording");
		if (Constants.VIDEO_ON) editor.startRecording(outputFile);
	}

	@Override
	public void stopRecording() {
		Log.logger.info("IHM : Stop Recording");
		if (Constants.VIDEO_ON) editor.stopRecording();
	}

	@Override
	public void startTracking(int x, int y, int width, int height) {
		Log.logger.info("IHM : Start Tracking");
		try {
			if (Constants.TRACKING_ON) tracker.startTracking(new Frame(new Point(x, y), width, height));
		} catch (OutofFrameException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void stopTracking() {
		Log.logger.info("IHM : Stop Tracking");
		if (Constants.TRACKING_ON) tracker.stopTracking();
	}

	@Override
	public void setManualMode() {
		Log.logger.info("IHM : Set manual mode");
	}

	@Override
	public void setAutomaticMode() {
		Log.logger.info("IHM : Set automatic mode");
	}

	@Override
	public void addMusician(int id, int DMXchannel, int soundCardChannel, int topLeftX, int topLeftY) {
		Frame frame = null; 
		try {
			frame = new Frame(topLeftX, topLeftY, Constants.CROP_WIDTH, Constants.CROP_HEIGHT);
		} catch (OutofFrameException e) {
			e.printStackTrace();
		}
		Zone zone = new Zone(id);
		zone.setFrame(frame);
		if (camera != null) 
		{
			zone.setCenter(camera.getAim());
			zone.setZoom(camera.getZoom());
		}
		zone.setInstrument(new Instrument(soundCardChannel));
		zone.setDmxChannel(DMXchannel);
		addZone(zone);
		Log.logger.info("IHM : Nouvelle zone #" + zone.getId());
	}

	@Override
	public void delMusician(int id) {
		delZone(id);
		Log.logger.info("IHM : Suppression de la zone #" + id);
	}

	@Override
	public void selectFrame(int topLeftX, int topLeftY) {
		Log.logger.info("IHM : Définition d'une zone à filmer");
	}

}

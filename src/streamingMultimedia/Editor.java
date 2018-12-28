package streamingMultimedia;
import interfaces.EditorInterface;
import tools.Frame;

/**
 * 
 * @author nathan
 *
 *la classe Editor execute la commande ffmpeg et crée un EditorControl pour controler le stream
 */

public class Editor implements EditorInterface{
	private String pathToStreamInit = "./src/streamingMultimedia/streamInit";
	private StreamFfmpeg stream;
	private EditorControl control;
	
	public Editor(){
		
	}
	
	/**
	 * lance la commande de stream et edition en dynamique ffmpeg
	 */
	
	
	public void run() {
		stream.start();
		try {
			stream.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	public void initEditorModule() {
		this.stream = new StreamFfmpeg(pathToStreamInit + "/mainStream");
		this.control = new EditorControl(pathToStreamInit + "/crop.txt", pathToStreamInit + "/SelectStream.txt");
	}
	/**
	 * les cameras suivent l'ordre suivant :
	 * -4K = 0
	 * -crop = 1
	 * -HD = 2
	 * 
	 * les méthodes useMobileCam() permettent de changer la caméra enregistrée dans le fichier final
	 */
	
	public void useMobileCam() {
		control.selectCamera(2);
		
	}

	
	public void useFixedCam() {
		control.selectCamera(1);
		
	}

	
	public void usePanCam() {
		control.selectCamera(0);
		
	}


	public void setCrop(Frame frame) {
		control.moveCrop(frame.getPointA());
		
	}

	
	public boolean startRecording(String outputFile) {
		return false;
	}


	public void stopRecording() {
	}

	
	public void stop() {
		stream.close();
	}
	
	public int getRecordedStream(){
		return control.getRecordedStream();
	}
}

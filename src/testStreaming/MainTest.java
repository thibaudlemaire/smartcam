package testStreaming;

import interfaces.EditorInterface;
import streamingMultimedia.Editor;

public class MainTest extends Thread{

	private static EditorInterface editor;
	private static Thread editorThread;
	/**
	 * @param args
	 */
	public static void main(String[] args){
		// TODO Auto-generated method stub
		editor = new Editor();
		editor.initEditorModule();
		editorThread = new Thread(editor);
		editorThread.start();
		//editor.startRecording("file");
		
		try {
			sleep(60000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		editor.stop();
		try {
			editorThread.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}

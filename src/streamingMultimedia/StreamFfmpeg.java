package streamingMultimedia;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import tools.Log;

public class StreamFfmpeg extends Thread{
	private String recordingPath;
	private String ffmpegPath;
	private String openStream;
	private String encoder;
	private String profileStream;
	private String filter;
	private String container;
	private String ipAdressClient;
	private String portClient;
	private String internetProtocol;
	private String streamUrl;
	private String streamCommand;
	
	private Process p;

	//Premier constructeur avec les arguments entrés à la main
	
	public StreamFfmpeg(){
		super("streamFfmpeg");
		//recordingPath="C:\\ffmpeg\\bin";				//windows version
		recordingPath="/home/nathan/bin";				//Linux version
		
		//ffmpegPath = "C:\\ffmpeg\\bin\\ffmpeg ";			//windows version
		ffmpegPath = "../../../exec/ffmpeg ";			//Linux version
		
		//openStream = "-y -f dshow -s 1280x720 -r 30 -i video=\"USB2.0 HD UVC WebCam\" ";	//windows version
		openStream = "-y -f v4l2 -s 1280x720 -r 30 -i /dev/video0 ";						//Linux version
		
		encoder = "-c:v libx264 ";
		profileStream = "-vf format=yuv420p -profile:v baseline -level 3.0 -tune zerolatency -preset ultrafast  -x264opts keyint=100:min-keyint=20 ";
		filter = "";
		container = "-f mpegts ";
		ipAdressClient = "192.168.0.37";
		portClient = "1234";
		internetProtocol = "udp";
		streamUrl= internetProtocol + "://"+ ipAdressClient + ":" + portClient;
		streamCommand = ffmpegPath + openStream + encoder + profileStream + filter + container + streamUrl;
	}
	
	//Second constructeur avec les arguments lus dans un fichier texte
	
	public StreamFfmpeg(String CommandFilePath){
		super("streamFfmpeg");
		streamCommand = readCommandLineFile(CommandFilePath);
	}
	
	//Méthode pour lancer le stream
	
	public void run() {
		Log.logger.info("Lancement du module de video");
		//System.out.println("Inside the thread!");
		//String[] args = { "cmd.exe", "/C", streamCommand };					//windows version
		String[] args = { "/bin/bash", "-c", streamCommand };
	    try {
	        //System.out.println("Stream Log >> Executing Runtime for FFMPEG: " + streamCommand);
	        p = Runtime.getRuntime().exec(args);
	        InputStream in = p.getErrorStream();
	        int c;
	        while ((c = in.read()) != -1)
	        {
	            //System.out.print((char)c);
	        }
	        in.close();
	        //System.out.println("Stream managed to close");
	        interrupt();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	   
	}

	//Méthode pour fermer proprement le stream
	
	public void close(){
		OutputStream ostream = p.getOutputStream(); 	//récupère le output stream du process qui correspond à ce que l'utilisateur écrirait dans le shell
		try {
			ostream.write("q\n".getBytes());			//on écrit q suivi de \n pour "entrée" dans la commande
		} catch (IOException e1) {
			e1.printStackTrace();
		}       
		try {
			ostream.flush();							//vide le buffer
		} catch (IOException e1) {
			e1.printStackTrace();
		}                          
	}
	
	//Méthode pour lire la ligne de comande depuis un fichier texte
	
	public String readCommandLineFile(String filePath){
		BufferedReader br = null;
		streamCommand = "";
		try{
            FileReader fr = new FileReader(filePath);
            br = new BufferedReader(fr);
            String currentLine = br.readLine();
            
            if (currentLine == null || currentLine.length() == 0){ 		//assure que le fichier n'est pas vide
            	throw new Exception("Command line file for FFMPEG empty !");
            	}
            
            while(currentLine != null){
            	streamCommand += currentLine + "\n";
            	currentLine = br.readLine();
            	}
            
            }catch (Exception e) {
                e.printStackTrace();
                System.out.println("Error" + e);
            } finally{
                try{
                    br.close();
                } catch(Exception e) {
                    e.printStackTrace();
                    System.out.println("Error" + e);
                }
		}
		return streamCommand;
	}
	
}
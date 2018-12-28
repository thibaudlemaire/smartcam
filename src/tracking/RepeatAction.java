package tracking;
import java.awt.Panel;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.BufferedReader;
import javax.swing.JFrame;
import javax.swing.event.EventListenerList;

import org.opencv.highgui.Highgui;        
import org.opencv.highgui.VideoCapture;

import exceptions.OutofFrameException;
import interfaces.TrackerListenerInterface;
import tools.Frame;
import tools.Point;

//import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import org.opencv.core.Mat;

public class RepeatAction extends Thread{
	private final boolean debug=false;
	private final boolean window=true;
	
	private final int epsilon = 50;
	private final int margeRechercheCrop = 50;
	
	private boolean repetition=true;
	private ArrayList<Integer> vectorMouvement;	//Vecteur mouvement, initialis�e � 0 0
	ArrayList<Integer> vect;
	private int x, y, w, h; //position, lageur hauteur cadre de recherche initial
	
	private Mat imageArriveeNonCroppe=new Mat();
	private Mat imageDepartNonCroppe;

	
	private JFrame jFrame;
	private JPanelOpenCV panel;
	private EventListenerList listeners = new EventListenerList();

	
	//private EditorControl control;
	//private File fichierImageDepart;
	//private File fichierImageArrivee;
	
	private Surf surf;
	private VideoCapture camera;
	
	private final float nndrRatio = (float) 0.7; 		//Param�tre changeable: plus il est �lev� plus il y a de points d'int�rets mais plus il y a de matching en th�orie moins pr�cis
	 
	public void setListener(TrackerListenerInterface l) {
		listeners.add(TrackerListenerInterface.class, l);
	}


	public void unsetListener(TrackerListenerInterface l) {
		listeners.remove(TrackerListenerInterface.class, l);
	}
	
	
	public RepeatAction(String folderPath){
		super("tracking");
		//Initialisation des fichiers
		this.camera = new VideoCapture(1);
		
		//Autres initialisations
		vectorMouvement = new ArrayList<Integer>(2);				//Initialisation de vectorMouvement
		vectorMouvement.add(new Integer(0));
		vectorMouvement.add(new Integer(0));
		
	    
		camera.read(imageArriveeNonCroppe);
	    
	    //File depart=new File(adresseDossier+Integer.toString(0)+format);
	  
	    
	    surf = new Surf(debug, margeRechercheCrop, nndrRatio, epsilon); 						//le 50 est la marge de recherche de zone de crop
	    

	    
	   // control = new EditorControl("/home/nathan/pact54/Computer/src/streamingMultimedia/streamInit/crop.txt","/home/nathan/pact54/Computer/src/streamingMultimedia/streamInit/SelectStream.txt" );
		}
	
	   //on initialise la position du crop en rçupérant les coordonnées de la frame de départ
	public void initFrame(Frame cadreCrop){
		x = cadreCrop.getPointA().getX();
	    y = cadreCrop.getPointA().getY();
	    w = cadreCrop.getWidth();
	    h = cadreCrop.getHeight();
	    
	    if (window){
	    jFrame=new JFrame();
	    jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.setSize(640, 500);
        jFrame.setLocation(500, 100);
        
        this.panel = new JPanelOpenCV();  
	    jFrame.setContentPane(panel);       
	    jFrame.setVisible(true);
	    	}
	    }
	
	public void run(){
		
		while (repetition){
			
			vectorMouvement = Surf(vectorMouvement);
			
		}
	}
	
	public final ArrayList<Integer> Surf(ArrayList<Integer> vectIfNotWorking){ 
	  	
		//Initialization 
		
		if (debug){
			long debut=System.currentTimeMillis();					//Sert a mesurer le temps d'execution
			System.out.println(System.currentTimeMillis()-debut); 	//renvoie le temps ecoule depuis le debut
			System.out.println("Started....");
			System.out.println("Loading images...");
			}
		
		vect = new ArrayList<Integer>();
		vect.add(0);
		vect.add(0);
		//x positif=mouvement vers la droite; y positif=mouvement vers le bas , vect contient le nouveau vecteur mouvement
		//Production des Mat pour analyse
		
		
		imageDepartNonCroppe = imageArriveeNonCroppe.clone();
		camera.read(imageArriveeNonCroppe);
		while (imageArriveeNonCroppe.empty())
			{camera.release();
			try {
				sleep(10);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			camera.open(1);
			camera.read(imageArriveeNonCroppe);
			System.out.println("Empty");
			try {
				sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}}
	    /*camera.release();
	    camera.open(1);*/
		
	    vect=surf.surf(imageDepartNonCroppe, imageArriveeNonCroppe, x, y, w, h);
	    
	    if (window && !(imageArriveeNonCroppe.empty())&& !(imageDepartNonCroppe.empty()) ){
	    surf.printImages(imageArriveeNonCroppe, w, h , this.panel);}
	    x=x+vect.get(0);
	    
	    y=y+vect.get(1);
	    
	    for(TrackerListenerInterface l : listeners.getListeners(TrackerListenerInterface.class))
			try {
				l.newTrackingPosition(new Point(x,y));
			} catch (OutofFrameException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	
	    
	    //********************demo pan 3*********************
	    /*try {
			Point p = new Point(x,y);
			control.moveCrop(p);
		} catch (OutofFrameException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	    
	    //*****************************************************
	      
	     if (debug){System.out.println("Ended....");
	     System.out.println(vect.get(0));
	     System.out.println(vect.get(1));
	     }
	   
	      return vect;
	}

	public final void stopTracking()
	{
		repetition=false;
		camera.release();
		try {
			sleep(25);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(jFrame != null)
			jFrame.dispose();
	}
 	
}

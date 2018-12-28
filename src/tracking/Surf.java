package tracking;
import java.awt.Panel;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.swing.*;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.features2d.DMatch;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FeatureDetector;
import org.opencv.features2d.KeyPoint;

public class Surf {
	
	private Boolean debug;
	float nndrRatio;
	private int margeRechercheCrop;
	private int epsilon;
	private BufferedImage retourBuffered; 
	private Mat imageDepartCroppe;
	private Mat imageArriveeCroppe;
	private Cadre cadreDepart;
	private Cadre cadreArrivee;
	private MatOfKeyPoint departKeyPoints;
	private FeatureDetector featureDetector;
	private MatOfKeyPoint departDescriptors;
	private DescriptorExtractor descriptorExtractor;
	private MatOfKeyPoint arriveeKeyPoints;
	private MatOfKeyPoint arriveeDescriptors;
	private List<MatOfDMatch> matches;
	private DescriptorMatcher descriptorMatcher;
	private LinkedList<DMatch> goodMatchesListBeforeTri;
	private Mat copyOfImageArrivee;
	private ArrayList<Integer> vectorOfMove;
	private int comptVFiltre;
	private ArrayList<Integer> listX,listY,listXFinal,listYFinal;
	private double vx,vy,lx,ly;
	

	
	public Surf(Boolean debug, int margeRechercheCrop, float nndrRatio, int epsilon){
		this.debug = debug;
		this.margeRechercheCrop = margeRechercheCrop;
		this.nndrRatio = nndrRatio;
		this.epsilon = epsilon;
		this.vectorOfMove=new ArrayList<Integer>(2);
		vectorOfMove.add(0);
		vectorOfMove.add(0);
		
	}
	
	public ArrayList<Integer> surf(Mat imageDepartNonCroppe, Mat imageArriveeNonCroppe, int x, int y, int w, int h){
		
	      if (imageDepartNonCroppe.dims()==0 || imageArriveeNonCroppe.dims()==0)
	    	  return vectorOfMove;
		  cadreDepart = new Cadre(imageDepartNonCroppe,x,y,w,h);
	      imageDepartCroppe = cadreDepart.getImageCroppe();										// "Croppage" de l'image originale-L'appel � la classe cadre permet de controler les coordonn�es du cadre et donc d'�viter un out of range
	        
	      cadreArrivee=new Cadre(imageArriveeNonCroppe,x - margeRechercheCrop ,y - margeRechercheCrop ,w + 2*margeRechercheCrop,h + 2*margeRechercheCrop);
	      imageArriveeCroppe = cadreArrivee.getImageCroppe();
	
	     
	      //Creation des descripteurs sur l'image de depart
	      departKeyPoints = new MatOfKeyPoint();
	      featureDetector = FeatureDetector.create(FeatureDetector.SURF);
	      
	      if (debug){System.out.println("Detecting key points...");}
	      
	      featureDetector.detect(imageDepartCroppe, departKeyPoints);		//Detection des points cl�s qui sont mis dans departKeyPoints
	      departDescriptors = new MatOfKeyPoint();
	      descriptorExtractor = DescriptorExtractor.create(DescriptorExtractor.SURF);
	      
	      if (debug){System.out.println("Computing descriptors...");}
	      
	      descriptorExtractor.compute(imageDepartCroppe, departKeyPoints, departDescriptors);//Associe un point cl� et un descripteur
	     
	      if (debug){System.out.println("Drawing key points on depart image...");}
	      
	      
	      // Match depart image with the arrivee image
	      arriveeKeyPoints = new MatOfKeyPoint();
	      arriveeDescriptors = new MatOfKeyPoint();
	      
	      if (debug){System.out.println("Detecting key points in background image...");}
	      
	      featureDetector.detect(imageArriveeCroppe, arriveeKeyPoints);
	      
	      if (debug){System.out.println("Computing descriptors in background image...");}
	      
	      descriptorExtractor.compute(imageArriveeCroppe, arriveeKeyPoints, arriveeDescriptors);
	      matches = new LinkedList<MatOfDMatch>();
	      descriptorMatcher = DescriptorMatcher.create(DescriptorMatcher.FLANNBASED);
	      
	      if (debug){System.out.println("Matching depart and arrivee images...");}
	      
	      if(!arriveeDescriptors.empty() && !departDescriptors.empty()){
	    	  
	    	  descriptorMatcher.knnMatch(departDescriptors, arriveeDescriptors, matches,2);
	      
	    	  if (debug){
	    		  System.out.println("Calculating good match list...");
	    	  }
	      
	    	  goodMatchesListBeforeTri = new LinkedList<DMatch>();	      
	      
	      
	    	  for (int i = 0; i < matches.size(); i++) {
	    		  MatOfDMatch matofDMatch = matches.get(i);
	    		  DMatch[] dmatcharray = matofDMatch.toArray();
	    		  DMatch m1 = dmatcharray[0];
	    		  DMatch m2 = dmatcharray[1];
	    		  if (m1.distance <= m2.distance * nndrRatio) {
	    			  goodMatchesListBeforeTri.addLast(m1);			//Ajout de tous les bons matching
	    		  }
	    	  }
	    	  if (goodMatchesListBeforeTri.size() >= 1) {
	    		  vectorOfMove = filtreVector();
	    	  
	    	  }
	      }
	      
	      
	      	return vectorOfMove;
	}
	
	
	public ArrayList<Integer> filtreVector(){
		
		vx=0;vy=0;
		comptVFiltre=0;
		listYFinal=new ArrayList<Integer>();
		listXFinal=new ArrayList<Integer>();
		listY=new ArrayList<Integer>();
		listX=new ArrayList<Integer>();

		if (debug){System.out.println("depart Found!!!");}
	          
	    List<KeyPoint> objKeypointlist = departKeyPoints.toList();
	    List<KeyPoint> scnKeypointlist = arriveeKeyPoints.toList();
	          
	    //Estimation du vecteur d�placement moyen
	   for (int i = 0; i < goodMatchesListBeforeTri.size(); i++) {
	    	Point ptdepart=objKeypointlist.get(goodMatchesListBeforeTri.get(i).queryIdx).pt;
	        Point ptarrivee=scnKeypointlist.get(goodMatchesListBeforeTri.get(i).trainIdx).pt;
	        /*vx+=(cadreArrivee.getXCoin() + ptarrivee.x - (cadreDepart.getXCoin() + ptdepart.x));
	        vy+=(cadreArrivee.getYCoin()+ ptarrivee.y - (cadreDepart.getYCoin() + ptdepart.y));*/
	        listX.add((int)(cadreArrivee.getXCoin() + ptarrivee.x - (cadreDepart.getXCoin() + ptdepart.x)));
	        listY.add((int) (cadreArrivee.getYCoin()+ ptarrivee.y - (cadreDepart.getYCoin() + ptdepart.y)));
	        //ajout méthode médiane ici 
	        
	        //comptVNonFiltre++;
	        }
	   listX.sort(null);
	   listY.sort(null);
	   vx=listX.get(listX.size()/2);
	   vy=listY.get(listY.size()/2);

	    
	    
	      //Tri et estimation du vecteur mouvement en retirant les matching aberrants
	   /*for (int i = 0; i < goodMatchesListBeforeTri.size(); i++) {
	    	 Point ptdepart=objKeypointlist.get(goodMatchesListBeforeTri.get(i).queryIdx).pt;
	    	 Point ptarrivee=scnKeypointlist.get(goodMatchesListBeforeTri.get(i).trainIdx).pt;
          
	    	 vx+=(cadreArrivee.getXCoin() + ptarrivee.x - (cadreDepart.getXCoin() + ptdepart.x));
		     vy+=(cadreArrivee.getYCoin()+ ptarrivee.y - (cadreDepart.getYCoin() + ptdepart.y));
	    	 if (Math.abs(lx-vx)<epsilon && Math.abs(ly-vy)<epsilon ){
         		vxfinal+=(cadreArrivee.getXCoin() + ptarrivee.x - (cadreDepart.getXCoin() + ptdepart.x));
         		vyfinal+=(cadreArrivee.getYCoin()+ ptarrivee.y - (cadreDepart.getYCoin() + ptdepart.y));
         		comptVFiltre++;
         		//goodMatchesList.add(goodMatchesListBeforeTri.get(i));//Ici, on ne garde que les matching renvoyant des vecteurs non aberrants (demeurant assez proche du cadre moyen -x�[xmoyen-e,xmoyen+e] et de lm�me pour y           	 
	    	 }
	    }
	          
	   if (comptVFiltre!=0){
		   vectorOfMove.set(0,new Integer((int) (vxfinal/comptVFiltre)));
		   vectorOfMove.set(1,new Integer((int) (vyfinal/comptVFiltre)));
		   //System.out.println(vxfinal/comptVFiltre);
		  //System.out.print(vectorOfMove);
		   }*/
	   
	   for (int i = 0; i < goodMatchesListBeforeTri.size(); i++) {
	    	 Point ptdepart=objKeypointlist.get(goodMatchesListBeforeTri.get(i).queryIdx).pt;
	    	 Point ptarrivee=scnKeypointlist.get(goodMatchesListBeforeTri.get(i).trainIdx).pt;
       
	    	 lx=(cadreArrivee.getXCoin() + ptarrivee.x - (cadreDepart.getXCoin() + ptdepart.x));
		     ly=(cadreArrivee.getYCoin()+ ptarrivee.y - (cadreDepart.getYCoin() + ptdepart.y));
	    	 
			if (Math.abs(lx-vx)<epsilon && Math.abs(ly-vy)<epsilon ){
				listXFinal.add((int) (cadreArrivee.getXCoin() + ptarrivee.x - (cadreDepart.getXCoin() + ptdepart.x)));
				listYFinal.add((int) (cadreArrivee.getYCoin()+ ptarrivee.y - (cadreDepart.getYCoin() + ptdepart.y)));
				comptVFiltre++;
      		}
	   }
	   if (comptVFiltre!=0){
	    	 
			 listXFinal.sort(null);
			 listYFinal.sort(null);
			 vectorOfMove.set(0,listXFinal.get(listXFinal.size()/2));
			 vectorOfMove.set(1,listYFinal.get(listYFinal.size()/2));
			   
	    }

        //Si au moins un bon matching filtr� est d�tect� alors on rentre les nouvelles coordonn�es du mouvement rep�r�
		
		return vectorOfMove;
	}
	
	public final void printImages(Mat imageArrivee, int w, int h, JPanelOpenCV panel){
		{
			copyOfImageArrivee=imageArrivee;
			
			Core.rectangle(copyOfImageArrivee, new Point(cadreDepart.getXCoin()+vectorOfMove.get(0),cadreDepart.getYCoin()+vectorOfMove.get(1)), new Point(cadreDepart.getXCoin()+vectorOfMove.get(0)+w,cadreDepart.getYCoin()+vectorOfMove.get(1)+h), new Scalar(255,0,255),0);//Nouvelle emplacement de la zone de d�part dans l'image d'arriv�e, en violet   
	    	Core.rectangle(copyOfImageArrivee, new Point(cadreDepart.getXCoin(),cadreDepart.getYCoin()), new Point(cadreDepart.getXCoin()+w,cadreDepart.getYCoin()+h), new Scalar(0,255,0),0);//La position originelle de l'objet "cibl�", en vert
	    	retourBuffered=MatToBufferedImage(copyOfImageArrivee);
	    	//jFrame.getContentPane().add(new JPanelOpenCV(retourBuffered));
	    	//jFrame.setVisible(true);
	    	//jFrame.repaint();
	    	if (!copyOfImageArrivee.empty()){
	    		panel.setimage(retourBuffered);  
	    		panel.repaint();
	    		}else{
	           System.out.println(" --(!) No captured frame -- Break!");  
	         }  
		}
	}
	
	public BufferedImage MatToBufferedImage(Mat frame) {
        //Mat() to BufferedImage
        int type = 0;
        if (frame.channels() == 1) {
            type = BufferedImage.TYPE_BYTE_GRAY;
        } else if (frame.channels() == 3) {
            type = BufferedImage.TYPE_3BYTE_BGR;
        }
        BufferedImage image = new BufferedImage(frame.width(), frame.height(), type);
        WritableRaster raster = image.getRaster();
        DataBufferByte dataBuffer = (DataBufferByte) raster.getDataBuffer();
        byte[] data = dataBuffer.getData();
        frame.get(0, 0, data);


        return image;
    }

	
	
}

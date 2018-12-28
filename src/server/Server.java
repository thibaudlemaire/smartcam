package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.BindException;
import java.net.ConnectException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Scanner;

import interfaces.IhmInterface;
import interfaces.ServerInterface;
import tools.Constants;
import tools.Log;

public class Server implements ServerInterface {
	private int port = 1234;
	private IhmInterface ihm = null;
	private boolean isWorking = true;
	private ServerSending serverSending = null;
	private Thread serverSendingThread = null;
	
	public Server()
	{
		serverSending = new ServerSending();
		serverSendingThread = new Thread(serverSending);
		serverSendingThread.setName("Server_Sending");
	}
	/**
	 * Cette fonction est la fonction appelee lors de la création du thread
	 */
	@Override
	public void run() {
		ServerSocket srvr = null;
		serverSendingThread.start();
		Log.logger.info("Lancement du serveur sur le port " + port);
		while (isWorking) {
			   try {
				   srvr = new ServerSocket(port);
				   srvr.setSoTimeout(1000);
				   Socket skt = srvr.accept();
				   BufferedReader in = new BufferedReader(new
						   InputStreamReader(skt.getInputStream()));
				   String data = in.readLine();
				   in.close();
				   
				   if (data.startsWith(".") & data.endsWith(".")) {
					   analysis(data.substring(2, data.length()-2));
				   } else {
					   Log.logger.warning("Serveur : pas le bon format de string");
				   }
				   srvr.close();
			   }
			   catch(SocketTimeoutException e)
			   {
				   try {
					srvr.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				   continue;
			   }
			   catch(ConnectException e) {
				   Log.logger.severe("Impossible de se connecter");
				   e.printStackTrace();
			   }
			   catch(BindException e)
			   {
				   Log.logger.warning("Impossible de démarer le serveur, le port est déjà utilisé");
				   e.printStackTrace();
				   return;
			   }
			   catch (Exception e) {
				   Log.logger.severe("Erreur Serveur");
				   e.printStackTrace();
				   try {
					srvr.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			   }
		  }
		try{
			srvr.close();
		} catch (Exception e) {
			Log.logger.warning("Impossible de fermer srvr (il peut �tre d�j� ferm�)");
		}
	}

	/**
	 * Cette fonction initialise le serveur
	 * @param port : le port à écouter
	 */
	@Override
	public void initServer(int pcPort, int androidPort, IhmInterface ihm) {
		this.port = pcPort;
		this.ihm = ihm;
		serverSending.initServerSending(androidPort);
	}
	
	public void stop() {
		isWorking = false;
		serverSending.stop();
		try {
			serverSendingThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Cette fonction analyse la commande re�ue
	 * @param command : La commande re�ue
	 */
	private void analysis(String command) {
		   Scanner scan = new Scanner(command);
		   switch (scan.next()) {
		   		case "M":
		   			zoneChoice(scan.nextInt());
		   			break;
		   		case "F":
		   			switchCam(scan.nextInt());
		   			break;
		   		case "S":
		   			changeMode(scan.nextInt());
		   			break;
		   		case "T":
		   			if (scan.nextInt() == 1) {
		   				startTracking(scan.nextInt(), scan.nextInt(), scan.nextInt(), scan.nextInt());
		   			} else {
		   				stopTracking();
		   			}
		   			break;
		   		case "C":
		   			camControl(scan.next());
		   			break;
		   		case "E":
		   			recording(scan.nextInt());
		   			break;
		   		case "A":
		   			addMusician(scan.nextInt(), scan.nextInt(), scan.nextInt(), scan.nextInt(), scan.nextInt());
		   			break;
		   		case "D":
		   			delMusician(scan.nextInt());
		   			break;
		   		case "R":
		   			selectFrame(scan.nextInt(), scan.nextInt());
		   			break;
		   		case "Z":
		   			if (scan.nextInt() == 1) {
		   				zoomPlus();
		   			} else {
		   				zoomMinus();
		   			}
		   			break;
		   		case "G":
		   			connectionTest();
		   			break;
		   		default:
		   			Log.logger.warning("Mauvaise commande");
		   			break;
		   }
		   scan.close();
	   }
	
	/**
	 * Cette fonction choisit le musicien � viser selon l'id re�u
	 * @param musicianID : L'ID du musicien � viser
	 */
	private void zoneChoice(int zoneID) {
		ihm.chooseZoneToAim(zoneID);
	}
	
	/**
	 * Cette fonction active le tracking
	 * @param left : Le x du cadre
	 * @param top : Le y du cadre
	 * @param width : La largeur du cadre
	 * @param height : La hauteur du cadre
	 */
	private void startTracking(int left, int top, int width, int height) {
		ihm.startTracking(left, top, width, height);
	}
	
	/**
	 * Cette fonction arrete le tracking
	 */
	private void stopTracking() {
		ihm.stopTracking();
	}
	
	/**
	 * Cette fonction permet de bouger la cam�ra vers la direction indiqu�e
	 * @param direction : La direction vers laquelle on veut tourner la cam�ra
	 */
	private void camControl(String direction) {
		switch (direction) {
		case "U":
			ihm.cameraUp();
			break;
		case "D":
			ihm.cameraDown();
			break;
		case "L":
			ihm.cameraLeft();
			break;
		case "R":
			ihm.cameraRight();
			break;
		default:
			Log.logger.warning("Erreur dans la commande de contr�le de la cam�ra : " + direction);
		}
	}
	
	/**
	 * Cette fonction permet d'activer ou de d�sactiver l'enregistrement
	 * @param choice
	 */
	private void recording(int choice) {
		if (choice == 0) {
			ihm.stopRecording();
		} else {
			ihm.startRecording();
		}
	}
	
	/**
	 * Cette fonction permet de changer de mode
	 * @param mode
	 */
	private void changeMode(int mode) {
		if (mode == 0) {
			ihm.setManualMode();
		} else {
			ihm.setAutomaticMode();
		}
	}
	
	/**
	 * Cette fonction permet de changer le flux
	 * @param camera : Le flux que l'on veut selectionner
	 */
	private void switchCam(int camera)
	{
		switch(camera){
		case 0:
			ihm.showPanoramicCam();
			break;
		case 1:
			ihm.showFixedCam();
			break;
		case 2:
			ihm.showMobileCam();
			break;				
		}
	}
	
	/**
	 * Cette fonction permet d'ajouter un musicien
	 * @param id : L'id du musicien
	 * @param canal : Le canal DMX du musicien
	 * @param voie : La voie du musicien
	 * @param top : Le Y du point du cadre
	 * @param left : Le X du point du cadre
	 */
	private void addMusician(int id, int canal, int voie, int top, int left) {
		ihm.addMusician(id, canal, voie, left, top);
	}
	
	/**
	 * Cette fonction permet de supprimer un musicien
	 * @param id : L'id du musicien
	 */
	private void delMusician(int id) {
		ihm.delMusician(id);
	}
	
	/**
	 * Cette fonction permet de selectionner une frame a filmer
	 * @param top : Le y de la frame
	 * @param left : Le x de la frame
	 */
	private void selectFrame(int top, int left) {
		ihm.selectFrame(left, top);
	}
	
	/**
	 * Cette fonction permet de zoomer
	 */
	private void zoomPlus() {
		ihm.zoomPlus();
	}
	
	/**
	 * Cette fonction permet de d�zoomer
	 */
	private void zoomMinus() {
		ihm.zoomMinus();
	}
	
	/**
	 * Cette fonction permet de repondre au test de connection
	 */
	private void connectionTest() {
		serverSending.Send("B", String.valueOf(Constants.FIXED_CAM_WIDTH), String.valueOf(Constants.FIXED_CAM_HEIGHT), String.valueOf(Constants.CROP_WIDTH), String.valueOf(Constants.CROP_HEIGHT));
	}

	@Override
	public void sendZoneChange(int idZone) {
		serverSending.Send("M", String.valueOf(idZone));
	}
	
	@Override
	public void sendStreamChange(int id) {
		serverSending.Send("F", String.valueOf(id));		
	}
}

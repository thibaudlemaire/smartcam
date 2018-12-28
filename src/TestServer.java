import java.io.*;
import java.net.ConnectException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

class TestServer {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ServerSocket srvr = null;
		try {
			srvr = new ServerSocket(1234);
			while (true) {
				String data;
				Socket skt = srvr.accept();
				BufferedReader in4 = new BufferedReader(new InputStreamReader(skt.getInputStream()));
				data = in4.readLine();
				in4.close();
				skt.close();
			

				if (data.startsWith(".") & data.endsWith(".")) {
					   analyse(data.substring(2, data.length()-2));
				   } else {
					   System.err.println("Pas le bon format de string");
				   }
			}
		}
		catch(ConnectException e) {
			System.out.print("Whoops! It didn't work!\n");
		}
		catch (Exception e1) {
			System.out.print("Whoops! It didn't work!\n");
			e1.printStackTrace();
		}
	}

	private static void analyse(String command) {
		   Scanner scan = new Scanner(command);

		   switch (scan.next()) {
		   		case "M":
		   			choixMusicien(scan.nextInt());
		   			break;
		   		case "F":
		   			choixFlux(scan.nextInt());
		   			break;
		   		case "S":
		   			choixMusicienAutomatique(scan.nextInt());
		   			break;
		   		case "T":
		   			if (scan.nextInt() == 0) {
		   				stopTracking();
		   			} else {
		   				startTracking(scan.nextInt(), scan.nextInt(), scan.nextInt(), scan.nextInt());
		   			}
		   			break;
		   		case "C":
		   			controleCamera(scan.next());
		   			break;
		   		case "E":
		   			enregistrement(scan.nextInt());
		   			break;
		   		case "A":
		   			addMusician(scan.nextInt(), scan.nextInt(), scan.nextInt(), scan.nextInt(), scan.nextInt());
		   			break;
		   		case "D":
		   			delMusician(scan.nextInt());
		   			break;
		   		case "R":
		   			newCadre(scan.nextInt(), scan.nextInt());
		   			break;
		   		case "Z":
		   			zoom(scan.nextInt());
		   			break;
		   		case "G":
		   			connectionTest();
		   			break;
		   		default:
		   			System.err.println("Mauvaise commande");
		   			break;
		   }
		   scan.close();
	   }

	   private static void controleCamera(String direction) {
		   // A compléter pour contrôler la caméra, faire assez court pour ne pas retarder la réception de commandes
		   System.out.println("Contrôle caméra : " + direction);
	   }

	   private static void enregistrement(int choix) {
		   // A compléter pour lancer/arrêter l'enregistrement, faire assez court pour ne pas retarder la réception de commandes
		   System.out.println("Enregistrement : " + choix);

	   }

	   private static void choixMusicienAutomatique(int choix) {
		   // A compléter pour lancer/arrêter le choix automatique du musicien, faire assez court pour ne pas retarder la réception de commandes
		   System.out.println("Choix automatique du musicien : " + choix);

	   }

	   private static void choixFlux(int idFlux) {
		   // A compléter pour switcher le flux, faire assez court pour ne pas retarder la réception de commandes
		   System.out.println("Choix du flux : " + idFlux);

	   }

	   private static void choixMusicien(int idMusicien) {
		   // A compléter pour switcher le focus de musicien, faire assez court pour ne pas retarder la réception de commandes
		   System.out.println("Choix du musicien : " + idMusicien);

	   }

	   private static void stopTracking() {
		// A compléter pour activer/désactiver le tracking, faire assez court pour ne pas retarder la réception de commandes
		   System.out.println("Désactivation du tracking");

	   }
	   
	   private static void startTracking(int left, int top, int width, int height) {
		   System.out.println("Activation du tracking : " + left + " " + top + " " + width + " " + height);
	   }
		
		private static void addMusician(int id, int canal, int voie, int cadreTop, int cadreLeft) {
			// ihm.addMusician(id, canal, voie, cadreTop, cadreLeft, cadreBot, cadreRight, pointX, pointY);
			System.out.println("Ajout musicien : " + id + " " + canal  + " " + voie  + " " + cadreTop  + " " + cadreLeft);
		}
		
		private static void delMusician(int id) {
			// ihm.delMusician(id);
			System.out.println("Suppression musicien : " + id);
		}
		
		private static void newCadre(int cadreTop, int cadreLeft) {
			// ihm.newCadre(cadreTop, cadreLeft, cadreBot, cadreRight);
			System.out.println("Nouveau cadre : " + cadreTop  + " " + cadreLeft);
		}
		
		private static void connectionTest() {
			TestServerSending.main(null);
		}
		
		private static void zoom(int zoom) {
			//ihm.zoomPlus() or ihm.zoomMinus();
			System.out.println("Contrôle zoom : " + zoom);
		}

}

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import tools.Log;

public class TestServerSending {

	public static void main(String[] args) {
		try {
			ServerSocket srvr = new ServerSocket(3000);
			srvr.setSoTimeout(5000);
			Socket skt = srvr.accept();
			System.out.println("Connected!");
			PrintWriter out3 = new PrintWriter(skt.getOutputStream(), true);
			System.out.println("Sending string!");
			out3.println(". B 3840 2180 1920 1080 .");
			System.out.println("String sent!");
		} catch (Exception e) {
			Log.logger.severe("Impossible d'envoyer la commande");
		}

	}

}

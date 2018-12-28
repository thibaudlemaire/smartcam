package server;

import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import tools.Log;

public class ServerSending implements Runnable {
	
	private int port = 3000;
	private String command;
	private boolean loop = true;

	@Override
	public void run() {
		try {
			synchronized(this) { this.wait(); }
			while(loop)
			{
				Log.logger.info("Envoi de la commande " + this.command + " sur le port " + port);
				ServerSocket srvr = new ServerSocket(port);
				Socket skt = srvr.accept();
				PrintWriter out = new PrintWriter(skt.getOutputStream(), true);
				out.print(command);
				out.close();
				srvr.close();
				synchronized(this) { this.wait(); }
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		 } catch (Exception e) {
			Log.logger.severe("Impossible d'envoyer la commande");
		}

	}

	/**
	 * Cette fonction initialise le serveur
	 * @param port : le port à écouter
	 * @param arg0 : les arguments de la commande � envoyer
	 */
	public void initServerSending(int port) {
		this.port = port;
	}
	
	/**
	 * Cette fonction envoie une commande
	 * @param arg0 : les arguments de la commande � envoyer
	 */
	public void Send(String... arg0) {
		String command = ". ";
		for (String x: arg0) {
			command += x + " ";
		}
		command += ".";
		this.command = command;
		synchronized(this) { this.notify(); }
	}

	/**
	 * Cette fonction stoppe le thread d'emission
	 */
	public void stop()
	{
		loop = false;
		synchronized(this) { this.notify(); }
	}
}

package materiel;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.swing.event.EventListenerList;
import com.fazecast.jSerialComm.*;
import interfaces.DmxListenerInterface;
import tools.Log;

public class Dmx implements SerialPortDataListener {
	public static final int DMX_IN = 1;
	public static final int DMX_OUT = 0;
	
	private static final byte DMX_PRO_MESSAGE_START = (byte) 0x7E; // 128
	private static final byte DMX_PRO_MESSAGE_END = (byte) 0xE7; // 231

	static final byte WIDGET_GET_PARAMETERS = (byte) 3;
	static final byte WIDGET_SET_PARAMETERS = (byte) 4;
	static final byte WIDGET_SEND_PACKET = (byte) 6;
	static final byte WIDGET_GET_SERIALNUMBER = (byte) 10;

	private SerialPort port;
	private int universeSize = 512;
	private byte[] universeValues; // Buffer
	private final int direction;

	private final EventListenerList listeners = new EventListenerList();
	private int[] channelsToMonitor = {};
	
	private Thread readerThread;
	private SerialReader serialReader;

	/**
	 * Constructeur, initialise le buffer à 0
	 */
	public Dmx(int direction) {
		this.direction = direction;
		// Setting all values to zero
		universeValues = new byte[universeSize];
		for (int i = 0; i < universeSize; i++) {
			universeValues[i] = 0;
		}
	}

	/**
	 * Retourne le numéro de série de l'interface
	 */
	public void getSerialNumber() {
		byte[] data = new byte[0];
		dmxMessage(WIDGET_GET_SERIALNUMBER, data);
		return;
	}

	/**
	 * Récupère les paramètres de l'interface
	 */
	public void getParameters() {
		byte[] data = new byte[0];
		dmxMessage(WIDGET_GET_PARAMETERS, data);
	}

	/**
	 * Renvoie l'état des cannaux DMX
	 * 
	 * @return
	 */
	public Integer[] getDmxStatus() {
		Integer[] values = new Integer[universeValues.length];
		for (int i = 0; i < universeValues.length; i++)
			values[i] = (int) universeValues[i];

		return values;
	}

	/**
	 * Fonction utilisée pour lister les ports séries
	 * @return
	 */
	public static String getComPorts() {
		String ports = "";
		for (SerialPort p : SerialPort.getCommPorts())
			ports += p.getSystemPortName() + '\n';
		return ports;

	}

	/**
	 * Ouvre le port série vers l'interface
	 * 
	 * @param Nom du port série à ouvrir
	 * @return true en ca de succes
	 * @throws InterruptedException
	 */
	public boolean open(String name) {
		port = SerialPort.getCommPort(name);
		if (!port.openPort())
		{
			Log.logger.severe("Impossible d'ouvrir l'interface " + name);
			return false;
		}
		Log.logger.info("Interface DMX " + name + " connectée");
		port.addDataListener(this);
		port.setComPortParameters(57600, 8, SerialPort.ONE_STOP_BIT, SerialPort.NO_PARITY);
		serialReader = new SerialReader(port.getInputStream());
		readerThread = new Thread(serialReader);
		readerThread.setName("DMX_Reader_"+port.getSystemPortName());
		readerThread.start();
		return true;
	}

	public void close() {
		if (port.isOpen())
		{
			Log.logger.info("Fermeture de l'interface DMX " + port.getSystemPortName());
			serialReader.stop();
			try {
				readerThread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			port.closePort();
		}
	}

	/**
	 * Lis la valeur d'un canal dans le buffer d'entrée
	 * 
	 * @param channel
	 * @return
	 */
	int getDMXInput(int channel) {
		if (direction == DMX_IN)
			return toUnsigned(universeValues[channel-1]);
		else
			return -1;
	}
	
	public int toUnsigned(byte b) {
		return b & (0xff);
	}

	/**
	 * Lis la valeur d'un canal dans le buffer de sortie
	 * 
	 * @param channel
	 * @return
	 */
	int getDMXOutput(int channel) {
		if (direction == DMX_OUT)
			return universeValues[channel];
		else 
			return -1;
	}

	/**
	 * Fonction qui ajoute un listener Attention ! Pour l'instant ne gère qu'un
	 * seul listener à la fois
	 * 
	 * @return
	 */
	public void setListener(DmxListenerInterface l, int[] channelToMonitor) {
		for (DmxListenerInterface listener : listeners.getListeners(DmxListenerInterface.class))
			listeners.remove(DmxListenerInterface.class, listener);
		listeners.add(DmxListenerInterface.class, l);
		this.channelsToMonitor = channelToMonitor;
	}

	/**
	 * Fonction qui supprime un listener
	 * 
	 * @param l
	 */
	public void unsetListener(DmxListenerInterface l) {
		listeners.remove(DmxListenerInterface.class, l);
	}
	
	private void generateEvent()
	{
		for(DmxListenerInterface l : listeners.getListeners(DmxListenerInterface.class))
		{
			l.dmxInputChanged();
		}	
	}

	/**
	 * Permet de mettre à jour la valeur d'un canal DMX, si la valeur change
	 * Envoie la mise à jour à l'interface si flush est vraie
	 * 
	 * @param channel
	 * @param value
	 * @param flush
	 */
	public void setDMXOutput(int channel, int value, boolean flush) {
		if (direction == DMX_IN)
			return;
		if (universeValues[channel - 1] == (byte) value) {
			return;
		}
		universeValues[channel - 1] = (byte) value;

		byte[] data = new byte[universeSize + 1];

		data[0] = 0; // Octet de commande DMX

		for (int i = 0; i < universeSize; i++) {
			data[i + 1] = universeValues[i];
		}
		if (flush) {
			dmxMessage(WIDGET_SEND_PACKET, data);
		}
	}

	/**
	 * Permet de modifier plusieurs cannaux d'un seul coup
	 * 
	 * @param levels
	 */
	public void setDmxArray(Integer[] levels) {
		if (direction == DMX_IN)
			return;
		int length = -1;
		if (levels.length > universeSize)
			length = universeSize;
		else
			length = levels.length;

		byte[] data = new byte[universeSize + 1];
		data[0] = 0;

		for (int i = 0; i < length; i++)
			if (levels[i] != null)
				data[i] = levels[i].byteValue();
			else
				data[i] = universeValues[i];

		dmxMessage(WIDGET_SEND_PACKET, data);
	}

	/**
	 * Le port est il ouvert ?
	 * 
	 * @return
	 */
	public boolean isOpen() {
		return port.isOpen();
	}

	/**
	 * Envoie un message à l'interface DMX
	 * 
	 * @param messageType
	 * @param data
	 */
	void dmxMessage(byte messageType, byte[] data) {
		byte[] message;
		int dataSize = data.length;
		message = new byte[5 + dataSize];

		message[0] = DMX_PRO_MESSAGE_START;
		message[1] = messageType;
		message[2] = (byte) (dataSize & 255);
		message[3] = (byte) ((dataSize >> 8) & 255);

		if (dataSize > 0) {
			for (int i = 0; i < dataSize; i++) {
				message[i + 4] = data[i];
			}
		}

		message[4 + dataSize] = DMX_PRO_MESSAGE_END;

		try {
			OutputStream out = port.getOutputStream();
			/*
			 * System.out.print("Sortie : "); for(byte car : message) {
			 * System.out.print(toHex(car)); } System.out.println("");
			 */
			out.write(message);
		} catch (Exception E) {
			E.printStackTrace();
		}
	}

	/**
	 * Récupère les évènement du port série
	 */
	@Override
	public void serialEvent(com.fazecast.jSerialComm.SerialPortEvent e) {
		//System.out.println("Event:"+e.toString());
		synchronized(serialReader) {
			readerThread.notify();
		}
	}

	/**
	 * Définit le type d'évènement attendu : données disponibles
	 */
	@Override
	public int getListeningEvents() {
		return SerialPort.LISTENING_EVENT_DATA_AVAILABLE;
	}

	/**
	 * Affiche le contenu du buffer
	 * 
	 * @param buffer
	 * @param l
	 */
	static void dump(byte[] buffer, int l) {
		int i;
		System.out.print(l + " bytes:");
		for (i = 0; i < l; i++) {
			System.out.print(toHex(buffer[i]) + " ");
		}
		System.out.println("");
	}

	/**
	 * Détermine la chaine hexadecimale d'un octet
	 * 
	 * @param b
	 * @return
	 */
	static String toHex(byte b) {
		char[] X = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
		StringBuffer Sb = new StringBuffer();
		int lsb = b & 0x0F;
		int msb = b & 0x70;
		msb >>>= 4;
		if (b < 0) {
			msb += 8;
		}
		Sb.append(X[msb]);
		Sb.append(X[lsb]);
		return Sb.toString();
	}

	// ##########################################

	/**
	 * Classe qui attend la réception d'un octet sur le port série
	 * 
	 * @author thibaud
	 *
	 */
	public class SerialReader implements Runnable {
		InputStream in;
		int state = 0;
		byte[] temp = new byte[600];
		int here;
		int ptype;
		int dlen;
		boolean loop = true;

		public SerialReader(InputStream in) {
			this.in = in;
		}

		public int toUnsigned(byte b) {
			return b & (0xff);
		}
		
		/**
		 * Function to stop this thread
		 */
		public void stop()
		{
			try {
				in.close();
				synchronized(this) {
					this.notify();
				}
				loop = false;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		public void run() {
			@SuppressWarnings("unused")
			int len = -1;
			try {
				while(in.available() > 0)
					in.read();
				while ((len = this.in.available()) > -1 && loop) {
					
					while(in.available() == 0 && loop)
						synchronized(this) {
							this.wait(100);
						}
					if(!loop)
						return;
					int i = in.read();
					if (state == 0) { // Attend l'octet de départ
						if (i == toUnsigned(DMX_PRO_MESSAGE_START)) {
							state = 1;
							ptype = -1;
						}
						continue;
					}
					if (state == 1) { // Détermine le type de message
						if (i < 0) {
							state = 0;
							continue;
						} // Mauvais type
						if (i > 11) {
							state = 0;
							continue;
						} // Mauvais type
						ptype = i;
						dlen = 0;
						state = 2;
						continue;
					}
					if (state == 2) { // Récupère le LSB
						dlen += i;
						state = 3;
						continue;
					}
					if (state == 3) { // Récupère le MSB
						if (i > 1) {
							state = 0;
							continue;
						}
						i = i * 255;
						dlen = dlen + i;
						// Vide le buffer
						for (i = 0; i < temp.length; i++) {
							temp[i] = 0;
						}
						int r = in.read(temp, 0, dlen); // Lecture de la réponse
						if (r != dlen) {
							state = 0;
							continue;
						}
						state = 4;
						continue;
					}
					if (state == 4) { // Récupère la fin du packet
						if (i == toUnsigned(DMX_PRO_MESSAGE_END)) { // Tout
							// s'est
							// bien
							// passé
							//System.out.println("Crack MSG");
							crackMsg(ptype, dlen, temp);
							state = 0;
						} else { // Problème
							Log.logger.warning("Problème dans le driver DMX : " + i + " expecting " + toUnsigned(DMX_PRO_MESSAGE_END) + " - " + port.getSystemPortName());
							Log.logger.warning("Ptype : " + ptype + " - Dlen : " + dlen + " - Temp : " + temp.length);
							//System.out.println("state 4: bad message end packet");
							//System.out.println("was " + i + " expecting " + toUnsigned(DMX_PRO_MESSAGE_END));
							
						}
						//state = 0;
						continue;
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		/**
		 * Décode le message
		 * 
		 * @param typ
		 * @param len
		 * @param buff
		 */
		void crackMsg(int typ, int len, byte[] buff) {
			//System.out.println("***msg Type:" + typ);
			//dump(buff, len);
			switch (typ) {
			case 03:
				System.out.println("fw rev:" + toHex(buff[1]));
				System.out.println("fw rev:" + toHex(buff[0]));
				System.out.println("Breaktime:" + toHex(buff[2]));
				System.out.println("Mark time:" + toHex(buff[3]));
				System.out.println("Update:" + toHex(buff[4]));
				break;

			case 05:
				receveDMX(len, buff);
				break;

			case 10:
				StringBuffer Sb = new StringBuffer();
				Sb.append(toHex(buff[3]));
				Sb.append(toHex(buff[2]));
				Sb.append(toHex(buff[1]));
				Sb.append(toHex(buff[0]));
				System.out.println("Ser:" + Sb.toString());
				break;
			}
		}
		
		private void receveDMX(int len, byte[] buff) {
			if (direction == DMX_OUT)
				return;
			for(int i : channelsToMonitor)
				if(universeValues[i-1] != buff[i+1])
				{
					generateEvent();
				}
			for (int i = 2; i < len; i++) {
				universeValues[i-2] = buff[i];
			}
		}
	}
}
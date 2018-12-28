package materiel;

import java.io.OutputStream;

import tools.Log;

import com.fazecast.jSerialComm.*;

/**
 * Classe utilisée pour envoyer des commandes à la caméra via l'interface Arduino
 * @author thibaud
 *
 */
public class CameraController {
	
	private SerialPort port;
	public final int maxZoom = 1;
	public final int maxFocus = 1;
	private int currentZoom;
	private int currentFocus;
	
	public CameraController()
	{
		port = SerialPort.getCommPort("ttyACM0");
		port.setComPortParameters(9600, 8, SerialPort.ONE_STOP_BIT, SerialPort.NO_PARITY);
	
	}
	
	public void initCamera()
	{
		if (!port.openPort())
		{
			Log.logger.severe("Impossible de se connecter à la carte Arduino " + port.getSystemPortName());
			return;
		}
		Log.logger.info("Carte Arduino " + port.getSystemPortName() + " connectée");
		initZoom();
		initFocus();
	}
	
	private void initZoom()
	{
		for(int i = 0; i < maxZoom; i++)
			send(CameraControllerProtocol.zoomDown);
		currentZoom = 0;
	}
	
	private void initFocus()
	{
		for(int i = 0; i < maxFocus; i++)
			send(CameraControllerProtocol.focusDown);
		currentFocus = 0;
	}
	
	public void zoomUp()
	{
		if(currentZoom >= maxZoom)
			return;
		send(CameraControllerProtocol.zoomUp);
		currentZoom++;
	}
	
	public void zoomDown()
	{
		if(currentZoom <= 0)
			return;
		send(CameraControllerProtocol.zoomDown);
		currentZoom--;
	}
	
	public int getZoom()
	{
		return currentZoom;
	}
	
	public void record()
	{
		send(CameraControllerProtocol.record);
	}
	
	public void focusUp()
	{
		if(currentFocus >= maxFocus)
			return;
		send(CameraControllerProtocol.focusUp);
		currentFocus++;
	}
	
	public void focusDown()
	{
		if(currentFocus <= 0)
			return;
		send(CameraControllerProtocol.focusDown);
		currentZoom--;
	}
	
	public int getFocus()
	{
		return currentFocus;
	}
	
	private void send(char command)
	{
		try {
			OutputStream out = port.getOutputStream();
			out.write(command);
		} catch (Exception E) {
			E.printStackTrace();
		}
	}
	
	public void close()
	{
		if (port.isOpen()) 
		{
			port.closePort();
			Log.logger.info("Carte Arduino " + port.getSystemPortName() + " déconnectée");
		}
	}
}

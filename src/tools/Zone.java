package tools;

import interfaces.InstrumentInterface;

/**
 * Outil Zone
 * Définit une zone formée par une frame (caméra 4K), un centre (viseur pour la caméra mobile)
 * éventuellement un canal DMX correspondant au projecteur éclairant la zone et un 
 * instrument qui joue dans cette zone
 * @author thibaud
 *
 */
public class Zone {

	private final int id;
	private Frame frame;
	private Point center;
	private int zoom = 0;
	private int dmxChannel = 0;
	private InstrumentInterface instrument = null;
	
	public Zone(int id, Frame frame, Point center, int zoom) {
		this.frame = frame;
		this.center = center;
		this.id = id;
		this.zoom = zoom;
	} 
	
	public Zone(int id, Frame frame) {
		this.frame = frame;
		setCenterAuto();
		this.id = id;
		this.zoom = 0;
	} 

	public Zone(int id) {
		this.id = id;
	}

	public void setDmxChannel(int dmxChannel)
	{
		this.dmxChannel = dmxChannel;
	}
	
	public void setInstrument(InstrumentInterface instrument)
	{
		this.instrument = instrument;
	}
	
	public void setFrame(Frame frame) 
	{
		this.frame = frame;
	}
	
	public void setCenter(Point center)
	{
		this.center = center;
	}
	
	public void setCenterAuto() 
	{
		if (frame != null)
			center = Point.barycentre(frame.getPointA(), frame.getPointB());
	}
	
	public int getDmxChannel() {
		return dmxChannel;
	}
	public Point getCenter() {
		return center;
	}
	public Frame getFrame() {
		return frame;
	}
	public InstrumentInterface getInstrument()
	{
		return instrument;
	}
	public int getId() {
		return id;
	}
	public void setZoom(int zoom)
	{
		this.zoom = zoom;
	}
	public int getZoom()
	{
		return zoom;
	}

}

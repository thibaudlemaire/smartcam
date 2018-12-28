package tools;

/**
 * Orientation de la caméra mobile
 * @author thibaud
 *
 */
public class Orientation {

	private int pan;
	private int tilt;
	
	public Orientation(int pan, int tilt)
	{
		this.pan = pan;
		this.tilt = tilt;
	}
	
	public int getPan()
	{
		return pan;
	}
	
	public int getTilt()
	{
		return tilt;
	}
	
	public Orientation addToPan(int toAdd)
	{
		pan += toAdd;
		return this;
	}
	
	public Orientation addToTilt(int toAdd)
	{
		tilt += toAdd;
		return this;
	}
	
	public String toString()
	{
		return "Pan : " + pan + " - Tilt : " + tilt;
	}
}

package tracking;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.opencv.core.Mat;

public class JPanelOpenCV extends JPanel{

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private BufferedImage image;
    int cpt=0;
    private JPanel contentPane;
    
    
    
   
    public JPanelOpenCV(){  
    	super(); 
    }
    
    private BufferedImage getimage(){  
        return image;  
      } 
    
    public void setimage(BufferedImage newimage){  
        this.image = newimage;  
        return;
      }
    
    @Override
    public void paint(Graphics g) {
    	//g = this.getGraphics(); 
        //g.drawImage(temp,10,10,temp.getWidth(),temp.getHeight(), this); 
    	if (this.image==null){ 
    			return;
    		}
    	BufferedImage temp=this.getimage(); 
    	g.drawImage(temp, 0, 0, this);
    	return;
    }
}


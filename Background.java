import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

/* Background (class)
 * Panel with function to draw background image
 */

public class Background extends JPanel {
    private Image background; //background variable stores background image

    /*
     *Background (method)
     * Panel with function to draw background image
     *@param (None)
     * Pre: none
     * Post : loads image file
     */

    public Background() {
        super ();
	
	setDoubleBuffered(true);

        try { // try to load the image file and save it in background
            background = ImageIO.read (new File ("./background.png"));
        } catch (IOException ignored) {
            System.out.println ("File not found");
        }
    }

    /*
     *paintComponent (method)
     * Draw background image if file is found
     *@param (None)
     * Pre : image initialized
     * Post: image paints to panel aligned to bottom right corner
     */

    @Override
    protected void paintComponent (Graphics g)  {
        super.paintComponent (g); //call the the parent class's function to maintain original functionality
        g.drawImage (background, 0, 0, getWidth (), getHeight (), background.getWidth (this) - getWidth (), background.getHeight (this) - getHeight (), background.getWidth (this), background.getHeight (this), this); //draw image so that the bottom right corner of the image lines up with the bottom right corner of the panel
    }
}
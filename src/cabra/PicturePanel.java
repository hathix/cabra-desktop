/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cabra;

import javax.swing.*;
import java.awt.*;

/** A reusable panel containing a picture
 *
 * @author Neel
 */
public class PicturePanel extends JPanel{
    
    public static final int PICTURE_WIDTH = 110;
    public static final int PICTURE_HEIGHT = 110;
    
    private ImageIcon picture;
    
    public PicturePanel(ImageIcon picture){
        setPreferredSize(new Dimension(PICTURE_WIDTH,PICTURE_HEIGHT));
        this.picture = picture;
    }
    
    public void reset(){
        //reset the panel's appearance
//        Image i;
    }
    
    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        
        //resize picture to appropriate size
        ImageIcon newPicture = GUI.scaleImage(picture, PICTURE_WIDTH, PICTURE_HEIGHT); 
        
        int width = newPicture.getIconWidth();
        int height = newPicture.getIconHeight();
        
        //get offsets in order to center picture
        int verticalOffset = (PICTURE_HEIGHT - height) / 2;
        int horizontalOffset = (PICTURE_WIDTH - width) / 2;
            //if the image is thinner than this panel, offset it

        //paint the picture
        g.drawImage(newPicture.getImage(),horizontalOffset,verticalOffset,this);
            //offsets let the image be centered
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cabra;

import javax.swing.*;
//import javax.swing.ImageIcon;
/*import java.awt.Image;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;*/
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import sun.awt.image.ToolkitImage;

/** Provides utility methods for using images.
 *
 * @author Neel
 */
public final class ImageManager {
    
    private ImageManager(){}
    
    /** Returns an ImageIcon, or null if the path was invalid. */
    public static ImageIcon createImageIcon(String path) {
        String filename = "images/" + path;
        java.net.URL imgURL = new Main().getClass().getResource(filename);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            System.err.println("Couldn't find file: " + path);
            //track down the code that's using the image
            new Throwable().printStackTrace();
            return null;
        }
    }

    /** Creates an image icon if you know its full path (C:\Users\...)
     * 
     * @param path the absolute path to the image (.png, .jpg, .gif)
     * @return the created icon
     */
    public static ImageIcon createImageIconFromFullPath(String path){
        return new ImageIcon(path);
    }
    
    /**
     * Creates a list of Images (not Image Icons!) Good for use as frame icons.
     * @param paths several URLs you want as possible icons, in any order.
     * @return the created image icons converted into images.
     */
    public static ArrayList<Image> createImages(String... paths){
        ArrayList<Image> images = new ArrayList<Image>();
        for(String path : paths){
            images.add(createImageIcon(path).getImage());
        }
        return images;
    }

    /**Saves the given imageicon in the given file.
     * 
     * @param imageIcon the image to save
     * @param saveTo where to save file
     */
    public static void saveImage(ImageIcon iconImage, File saveTo){
        try {
            BufferedImage bufferedImage = null;
            Image image = iconImage.getImage();
            
            if(image instanceof ToolkitImage){
                ToolkitImage toolkitImage = (ToolkitImage)image;
                bufferedImage = toolkitImage.getBufferedImage();
            }
            else{
                //do it the normal way
                bufferedImage = (BufferedImage)image;
            }
            
            javax.imageio.ImageIO.write(bufferedImage, Utils.getExtension(saveTo), saveTo);
            
        } catch (java.io.IOException e) {
            System.out.println("Error saving image " + saveTo + "! Details: " + e);
        }
    }
    
    /** Copies the given image file to the given location.
     * 
     * @param original the file containing the image to be copied
     * @param copyTo the file to be written to
     */
    public static void copyImage(File original, File copyTo){
        saveImage(createImageIconFromFullPath(original.getAbsolutePath()),copyTo);
    }
    
    /**
     * Resizes an image using a Graphics2D object backed by a BufferedImage.
     * @param srcImg - source imageicon to scale
     * @param w - desired max width
     * @param h - desired max height
     * @return - the new resized imageicon
     */
    public static ImageIcon scaleImage(ImageIcon image, int w, int h){
        int imageWidth = image.getIconWidth();
        int imageHeight = image.getIconHeight();
        double scale = 1.0; //how much to scale image
        //int verticalOffset = 0; //how much to offset the image up/down
        //int horizontalOffset = 0; //left/right
        //find the best dimensions
        if(imageWidth <= w && imageHeight <= h){
            //the picture is small enough that it can fit without scaling, so don't scale
            //shrink w and h to image width/height
            w = imageWidth;
            h = imageHeight;
            //do offsets
            //verticalOffset = (w - h)/2;
            //horizontalOffset = (h - w)/2;
        } else if(imageWidth > imageHeight){
            //if the image is wider than tall, scale based on width
            scale = (w + 0.0) / imageWidth;
            //w is kept, but height needs to compensate for that
            h = (int)(scale * imageHeight);
            //if image is 400 x 200 and needs to get to 100 x 100...
            //width remains 100, so scale is 1/4 (100/400)
            //height is 1/4 * 200 or 50. offset is (100 - 50)/2 or 25, and up/down or vertical
            //verticalOffset = (w - h)/2; //image is centered so offset is equal on top and bottom 
        } else{
            //image is taller than wide
            scale = (h + 0.0) / imageHeight;
            w = (int)(scale * imageWidth);
            //horizontalOffset = (h - w)/2;
            //say image is 200 x 500 and needs to get to 100 x 100
            //height will be 100, so scale is 1/5 (100/500) to get to 100 height
            //but 1/5 must als be applied to width, so scale the image width (200 * 1/5 = 40)
            //width is 40, so 30 on each side, or (100 - 40) / 2.
        }
            
        //System.out.println("Original dimensions: height " + imageHeight + ", width " + imageWidth);
        //System.out.println("New dimension: height " + h + ", width " + w);
        //System.out.println("Offsets: vertical " + verticalOffset + ", horizontal " + horizontalOffset);
        
        Image srcImg = image.getImage();
        BufferedImage resizedImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = resizedImg.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(srcImg, 0, 0, w, h, null); 
            //first int is x location of top left, second is y location
        g2.dispose();
        return new ImageIcon(resizedImg);
    }
    
    /**
     * Creates a dialog containing the image (at full size) and shows it to the user.
     * Best used to show an image full size when the thumbnail is clicked on.
     * @param image the image to show full size.
     * @param frame the parent frame of the dialog that will be shown
     */
    public static void showImage(ImageIcon image, JFrame frame){
        int width = image.getIconWidth();
        int height = image.getIconHeight();
        
        JPanel imagePanel = new JPanel(new GridLayout(1,1));
        imagePanel.add(new JLabel(image));
        imagePanel.setPreferredSize(new Dimension(width, height));
        
        JDialog dialog = Utils.putPanelInDialog(
                imagePanel, 
                frame, 
                "Full size image (" + width + "x" + height + ")", 
                "pics.png",
                -1, 
                -1
           );
        
        dialog.setVisible(true);
    }
}

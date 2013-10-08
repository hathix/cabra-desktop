/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cabra;

import java.io.File;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

/**Assorted utilities.
 *
 * @author Neel
 */
public final class Utils {
    
    private Utils(){} //can't be instantiated

    /**Returns the extension of a file.
     * 
     * @param file the file to check
     * @return the extension
     * @return <code>null</code> if the file's a folder
     */
    public static String getExtension(File file){
        String extension = null;
        String name = file.getName(); //like foo.txt
        int dot = name.lastIndexOf('.');

        if (dot > 0 &&  dot < name.length() - 1) {
            //there is an extension and it's not at the end
            extension = name.substring(dot+1).toLowerCase();
        }
        return extension;    
    }
    
        
    /**Tests to see if the given string ends with any of the options. Used often with files/extensions.
     * 
     * @param test the string to test
     * @param ends the strings that you want to see at the end
     * @return true if the string ends with one of the specified string, false otherwise
     */    
    public static boolean endsWith(String test, String... ends){
        for(String end : ends){
            if(test.endsWith(end)) return true;
        }
        return false;
    }    
    
    /** Cleans up a URL, because Java doesn't like URLs with spaces in them
     * 
     * @param url the raw URL with spaces
     * @return the URL with %20's instead of spaces
     */
    public static String sanitizeURL(String url){
        return url.replaceAll(" ","%20");
    }
    
    /**
     * Decides if the random chance goes through or not (a probability trial.)
     * @param chance the decimal chance that it will happen (0.1 = 10%)
     * @return true if the random chance goes through, false otherwise
     */
    public static boolean pushLuck(double chance) {
        return Math.random() < chance;
    }    
    
    /** Finds the sum of an array of ints.
     * 
     * @param nums the array
     * @return their sum
     */
    public static int arraySum(int[] nums){
        int sum = 0;
        for(int i : nums)
            sum += i;
        return sum;
    }
    
    /* Finds the average of the numbers.
     * @param nums as many integers as you want
     * @return the average, shortened to an int
     */
    public static int average(int... nums){
        int count = 0;
        int total = 0;
        for(int num : nums){
            total += num;
            count++;
        }
        return total / count;
    }
    
    /**Converts the given selection into a percent.
     * 
     * @param chosen the numerator, or the number of chosen items
     * @param total the denominator, or the total number of items
     * @return the percent, formatted as xx% or just x%
     */
    public static String toPercent(int chosen, int total){
        //for example, let's say we had 2/3 passed
        int percent = percent(chosen,total);
        String percentString = percent + "%"; //67 + %
        return percentString;
    }
    
    /**Returns the percentage, like 5.
     * 
     * @param chosen the numerator
     * @param total the denominator
     * @return the percent
     */
    public static int percent(int chosen, int total){
        if(total == 0){
            //dividing by 0 is bad; just return 0%
            return 0;
        }
        //let's say we had 2,3 passed
        double decimal = chosen / (total + 0.0); //2/3.0 is 0.6666
        int percent = (int)(Math.round(decimal * 100)); //0.6666 * 100 ~= 0.67 (we're rounding)
        return percent;
    }
    
    /**
     * Converts the given number of days to milliseconds.
     * @param days  a certain number of days
     * @return  a number of milliseconds equal to that. It's a long.
     */
    public static long daysToMillis(int days){
        return days * 24 * 60 * 60 * 1000; //24 hours, 60 mins, 60 sec, 1000 ms
    }
    
    /**
     * Turns the given int[] array into an Integer[] array.
     * @param a an array of ints
     * @return an array of Integers with the same contents
     */
    public static Integer[] toIntegerArray(int[] a){
        Integer[] Ints = new Integer[a.length];
        for(int i=0; i<a.length; i++){
            Ints[i] = new Integer(a[i]);
        }
        
        return Ints;
    }
    
    /**
     * Returns the given string with an "s" at the end if the quantity is present.
     * stringWithPlural("dog", 5) -> "dogs"
     * Warning: doesn't take into account unusual endings (curse you, English.) "child" would become "childs" :(
     * @param string some sort of noun that can be counted
     * @param quantity the number of things there are of the string
     * @return  the string + "s" if there's anything but 1, the original string only if there's only 1
     */
    public static String stringWithPlural(String string, int quantity){
        if(quantity != 1)
            return string + "s";
        else
            return string;
    }
    
    /**
     * Returns the number of digits in x.
     * @param x a whole number.
     * @return 1 if x is 0, otherwise the number of digits.
     */
    public static int numDigits(int x){
        if(x == 0) return 1;
        return (int)(Math.log10(x)) + 1; //250 -> log250 = 2.something -> 2+1 -> 3
    }
    
    /**
     * Given a number, pads it with leading zeros so the string representation has the given number of digits.
     * If there are more digits in x than the parameter digits, then the representation of x is returned.
     * @param x a number >= 0.
     * @param digits a number >= 0.
     * @return e.g. "00034" if x=34 and digits=5. "532" if x=532 and digits=2 (digits ignored since digits in x > [digits])
     */
    public static String padWithLeadingZeroes(int x, int digits){
        String string = x + ""; //default string representation
        int zeroesInFront = digits - numDigits(x); //total digits - digits in number is number to add on front
        
        //add specified # of 0s on front of string
        for(int i=0; i<zeroesInFront; i++){
            string = "0" + string;
        }
        
        return string;
    }
    
    /**
     * Given a string array, converts it into an array representation.
     * @param array an array of strings. Elements should not have semicolons.
     * @return the stringified array. Use arrayFromString() to decode it.
     */
    public static String stringFromArray(String[] array){
        String string = "[";
        //tack on each string, add a semicolon to separate them
        for(String arrayString : array){
            string += arrayString + ";";
        }
        //chop off the last bit
        string = string.substring(0, string.length() - 1);
        string += "]";
        
        return string;
    }
    
    /**
     * Given a string representing an encoded list of strings (from stringFromArray), changes it into a list of strings.
     * @param stringified represents a string array.
     * @return an array of the strings inside stringified.
     */
    public static String[] arrayFromString(String stringified){
        //chop off starting and ending brackets
        stringified = stringified.substring(1, stringified.length() - 1);
        //now each item is delimited by a semicolon; split them
        String[] strings = stringified.split(";");
        return strings;
    }
    
    
    /**
     * Guts the given folder. All files inside it are deleted. The given folder is not deleted.
     * @param file a folder
     */
    public static void purgeFolder(File folder){
        //delete everything inside this
        for(File file : folder.listFiles()){
            obliterate(file);
        }
    }
    
    /**
     * Completely obliterates the given file, leaving no trace of it.
     * @param file 
     */
    public static void obliterate(File file){
        if(file.isFile()){
            file.delete();
            file.deleteOnExit();
        }
        else if(file.isDirectory()){
            //delete everything inside, then delete this on exit
            for(File child : file.listFiles()){
                obliterate(child);
            }
            file.delete();
            file.deleteOnExit();
        }
    }
    
    //GUI UTILITIES
    
    /** Creates a JPanel that shows some advice to the user.
     * 
     * @param text the advice (can be HTML)
     * @return the created JPanel
     */
    public static JPanel createAdvicePanel(String text){
            JPanel panel = new JPanel(false){
            @Override
                    public void paintComponent(Graphics g){
                        super.paintComponent(g);
                        Utils.drawEmblem(this, g);
                    }
            };
            JLabel filler = new JLabel(text);
            filler.setHorizontalAlignment(JLabel.CENTER);
            //filler.setVerticalAlignment(JLabel.CENTER);
            panel.setLayout(new java.awt.GridLayout(1, 1));
            panel.add(filler);
            return panel;
    }
    
    /** Launches the user's browser to the specified URL.
     * 
     * @param url the url to open to
     */
    public static void browse(String url){
       url = sanitizeURL(url);
       java.net.URI uri = null;
       java.awt.Desktop desktop = java.awt.Desktop.getDesktop();
       try{
           uri = new java.net.URI(url);
           desktop.browse(uri);
       }
       catch(java.io.IOException io){
           //io error
           System.err.println("Error launching browser! Details: " + io);
       }
       catch(java.net.URISyntaxException ex){
           //error with syntax of URI
           System.err.println("Bad URI!: " + uri + ", details: " + ex);
       }              
    }
    
    /** Creates a JEditorPane (for viewing the web) and puts it in a panel and returns it
     * 
     * @param pageURL the URL of the page to open in this text pane
     * @param width, height
     */
    public static JPanel createEditorPane(String pageURL, int width, int height){
        //build editor pane
        JEditorPane editorPane = new JEditorPane();
        editorPane.setEditable(false);
        editorPane.setFont(FontManager.PREFERRED_FONT);       

        //set the page
        java.net.URL url = null; 
        try{
            url = new java.net.URL(pageURL);
        }
        catch(java.net.MalformedURLException m){
            //bad URL
            System.err.println("Bad url! " + m);
        }
        if(url != null){
            try{
                editorPane.setPage(url);
            }
            catch(java.io.IOException io){
                //error with setting page
                //System.err.println("Error loading page " + url);
            }
        }

        //enable hyperlinks
        editorPane.addHyperlinkListener(new HyperlinkListener() {
            public void hyperlinkUpdate(HyperlinkEvent evt) {
                if(evt.getEventType() == HyperlinkEvent.EventType.ACTIVATED){
                    Utils.browse(evt.getURL().toString());
                }
            }
        }); 

        //put it in a scroll pane
        JScrollPane scrollPane = new JScrollPane(editorPane);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED); //mostly for the license, which has preformatted text

        //put it in a JPanel
        JPanel panel = new JPanel(new java.awt.BorderLayout());
        panel.add(scrollPane);
        panel.setPreferredSize(new java.awt.Dimension(width,height));
        return panel;
    }    
   
    /**
     * Uses a JEditorPane to open the given URL in a dialog
     * @param url the url to load
     * @param title the title of the dialog
     * @param frame the parent frame
     * @param visible true if you want the dialog to be visible, false if you want it to be invisible to user
     */
    public static void openURLinDialog(String url, String title, JFrame frame, boolean visible){
        openURLinDialog(url, title, "goat16.png", frame, visible);
    }
    
    /**
     * Uses a JEditorPane to open the given URL in a dialog
     * @param url the url to load
     * @param title the title of the dialog
     * @param frame the parent frame
     * @param visible true if you want the dialog to be visible, false if you want it to be invisible to user
     * @param iconPath the path to the image icon for the dialog.
     */
    public static void openURLinDialog(String url, String title,  String iconPath, JFrame frame, boolean visible){
        //create panel with page in it
        final int WIDTH = 400;        
        final int HEIGHT = 300;
        JPanel webView = createEditorPane(url, WIDTH, HEIGHT);
        
        //put it in a dialog
        JDialog dialog = new JDialog(frame);
        dialog.setTitle(title);
        dialog.setIconImage(GUI.createImageIcon(iconPath).getImage());
        dialog.setContentPane(webView);
        dialog.pack();
        dialog.setResizable(false);
        dialog.setModal(true);
        Utils.centerComponent(dialog,frame);   
        
        //open it
        dialog.setVisible(visible);
    }
        
    /** Changes the frame's location to a more central screen location
     * @param frame the frame to be moved
     * @param X how far right to move the frame
     * @param Y how far down to move the frame
     */
    public static void changeFrameLocation(Component frame, int X, int Y){
        Point location = frame.getLocation(); //the window's current location
        //move the window over and down a certain amount of pixels
        location.translate(X, Y);
        //set the location
        frame.setLocation(location);
    }    

    /** Centers the given component in relation to its owner.
     * 
     * @param component the component to center
     * @param owner the parent frame
     */
    public static void centerComponent(Component component,Component owner){
        //find the difference in width to see the offsets
        int widthDifference = owner.getWidth() - component.getWidth();
        int heightDifference = owner.getHeight() - component.getHeight();
        
        //we can divide the differences by 2 and add that to the owner's top left
        //and then make that the top left of the component
        //to center the frame
        int leftOffset = widthDifference / 2;
        int topOffset = heightDifference / 2;
        
        //these are the new locations
        int left = owner.getX() + leftOffset;
        int top = owner.getY() + topOffset;
        
        Utils.changeFrameLocation(component, left, top);
    }
    
      /**
       * Centers the given component on the user's screen.
       * @param component a component (usually a frame.)
       */
      public static void centerOnScreen(Component component){
            Toolkit toolkit = java.awt.Toolkit.getDefaultToolkit();
            Dimension screenSize = toolkit.getScreenSize();
            int screenWidth = (int)screenSize.getWidth();
            int screenHeight = (int)screenSize.getHeight();

            int componentWidth = component.getWidth();
            int componentHeight = component.getHeight();

            int top = (screenHeight - componentHeight) / 2;
            int left = (screenWidth - componentWidth) / 2;

            Utils.changeFrameLocation(component, left, top);
      }
    
    /** Draws an emblem (based on current theme) in the bottom left of the given component. 
     * Call this in paintComponent() of the component.
     * 
     * @param component the component to draw on
     * @param g the Graphics object from paintComponent()
     */
    public static void drawEmblem(JComponent component, Graphics g){
            Themes currentTheme = Themes.getCurrentTheme();
            ImageIcon image = GUI.createImageIcon("translucent/" + currentTheme.getImageIconPath());
            
            /*if(Themes.getCurrentTheme() == Themes.SNOW){
                image = GUI.createImageIcon("translucent/snow.png");
            }*/
            
            int imageWidth = image.getIconWidth();
            int imageHeight = image.getIconHeight();
            //top left corner of where to start drawing
            int topLeftX = component.getWidth() - imageWidth; //x (horizontal) coordinate
            int topLeftY = component.getHeight() - imageHeight; //y (vertical) coordinate
            
            //draw in bottom right corner
            g.drawImage(image.getImage(), topLeftX, topLeftY, (java.awt.image.ImageObserver)null);
            //g.drawImage(image.getImage(), 0,0, (java.awt.image.ImageObserver)null);   
    }
    
    /**
     * Utility method to put the given panel in a JDialog. You'll have to show it on your own.
     * @param panel the panel to put in a dialog
     * @param owner the JFrame that owns the dialog. You can pass null.
     * @param dialogTitle the title for the dialog
     * @param iconPath the path to the dialog's icon, such as foo.png
     * @param width the dialog's width. Pass -1 to pack().
     * @param height the dialog's height. Pass -1 to pack().
     * @return the JDialog the panel is in
     */
    public static JDialog putPanelInDialog(JPanel panel, JFrame owner, String dialogTitle,
            String iconPath, int width, int height){
        JDialog dialog = new JDialog(owner, dialogTitle, true); //boolean means modality
        
        dialog.add(panel);
        dialog.setIconImage(ImageManager.createImageIcon(iconPath).getImage());
        if(width < 0 && height < 0)
            dialog.pack();
        else
            dialog.setSize(width, height);
        dialog.setResizable(false);
        if(owner != null)
            Utils.centerComponent(dialog, owner);
        else
            dialog.setLocationRelativeTo(null);
        
        return dialog;
    }
    
    /**
     * Creates and shows a dialog.
     * @param frame the parent frame. Can be null.
     * @param whatToSay the body of the dialog.
     * @param title the title of the dialog.
     * @param iconPath the icon's name (x.png)
     */
    public static void showDialog(JFrame frame,String whatToSay,String title){
        showDialog(frame, whatToSay, title, "goat64.png");
    }
    
    /**
     * Creates and shows an alert dialog.
     * @param frame the parent frame. Can be null.
     * @param whatToSay the body of the dialog.
     * @param title the title of the dialog.
     * @param iconPath the icon's name (x.png)
     */
    public static void showDialog(JFrame frame,String whatToSay,String title, String iconPath){
        JOptionPane.showMessageDialog(
                            frame, //parent
                            whatToSay, //text
                            title, //title
                            JOptionPane.INFORMATION_MESSAGE, //mesage type
                            GUI.createImageIcon(iconPath) //icon
                        );      
    }
    
    /**
     * Convenience overload for debug(Exception, String.) Title is default.
     * @param e the exception to show
     */
    /*public static void debug(Exception e){
        debug(e, "Cabra error");
    }*/
    
    /**
     * Shows an alert dialog for when an exception occurs.
     * @param e the exception to show
     * @param title the title of the dialog
     */
    public static void debug(Exception e, String title){
        e.printStackTrace();
        String text = "<i>" + e.toString() + "</i><br>";
        //add stack trace
        for(StackTraceElement ste : e.getStackTrace()){
            text += ste.toString() + "<br>";
        }
        text += "<br><b>Please take a screenshot and email it to <u>neel@hathix.com</u>.</b>";
        
        showDialog(null, "<html>Sorry! Cabra has encountered an error. Details:<br><br>" + text,title);
    }
}

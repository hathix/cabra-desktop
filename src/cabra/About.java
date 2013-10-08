/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cabra;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import static cabra.Utils.*;

/**
 *
 * @author Neel
 */
public class About {
    
    /** The version.
     * Stable releases should use x.x.x.
     * Beta releases should use x.x.x beta x.
     * 
     */
    public static final String VERSION = "0.7.1 beta 1";
    /**
     * Judges if the current version is a beta/alpha/nightly (prerelease) or stable release.
     */
    public static final boolean PRERELEASE = VERSION.contains("beta") || VERSION.contains("alpha") || VERSION.contains("nightly");
    
    /**
     * Judges if the current version is a nightly build.
     */
    public static final boolean NIGHTLY = VERSION.contains("nightly");
    
    /**
     * The name of this program. It can be easily changed if it's a prerelease version etc.
     */
    public static final String PROGRAM_NAME = "Cabra";
    
    /**
     * Add this to the end of an URL you open here so Piwik can track it.
     */
    public static final String TRACKING_URL_ADDON = "?pk_campaign=Cabra&pk_kwd=MenuBar";    
    
    /** The string that contains the URL to the about page. Based off the version.
     * 
     */
    public static final String ABOUT_PAGE = "http://www.cabra.hathix.com/cabra/"
            + Utils.sanitizeURL(VERSION) //URL with spaces doesn't seem to work so escape them
            + ".php";
    
    private static JPanel ABOUT_PANEL = null;
    private static JDialog ABOUT_DIALOG = null;
    
    /** Builds the About Dialog. Call showDialog() to finally show it.
     * 
     * @param frame the frame that will be the parent of this dialog
     */
    public static void createDialog(JFrame frame){     
        ABOUT_DIALOG = new JDialog(frame);
        ABOUT_DIALOG.setTitle("About Cabra " + VERSION);
        ABOUT_DIALOG.setIconImage(GUI.createImageIcon("goat16.png").getImage());
        ABOUT_DIALOG.setContentPane(ABOUT_PANEL);                
        //ABOUT_DIALOG.setSize(new java.awt.Dimension(AboutBrowser.MY_WIDTH,AboutBrowser.MY_HEIGHT));
        ABOUT_DIALOG.pack();
        ABOUT_DIALOG.setResizable(false);
        ABOUT_DIALOG.setModal(true);
        Utils.centerComponent(ABOUT_DIALOG,frame);        
    }
    
    /** Shows the about dialog (if the about panel has been created.)
     * 
     * @param frame the frame that will be the parent of this dialog
     */
    public static void showDialog(JFrame frame){
        if(ABOUT_PANEL == null){
            //no panel; make it
            buildDialogPanel();
        }
        createDialog(frame);
        ABOUT_DIALOG.setVisible(true);
    }
    
    /**Create the about browser's panel (but don't create/show the browser.)
     * 
     */
    public static void buildDialogPanel(){
        ABOUT_PANEL = new AboutBrowser();
    }
    
    /**The about browser.
     * 
     */
    private static class AboutBrowser extends JPanel{
        
        public static final int MY_WIDTH = 400;
        public static final int MY_HEIGHT = 400;
        
        public AboutBrowser(){
            //add a text pane with data and GPL
            add(createTabPane());
        }
        /** Build and return the tab pane.
         * 
         * @return the tab pane
         */
        private JTabbedPane createTabPane(){
            final JTabbedPane tabPane = new JTabbedPane();
            
            //loading HTML pages is time-consuming; run in background
            //however it needs to be thread safe
            SwingUtilities.invokeLater(new Runnable(){
                public void run(){
                    tabPane.addTab("About", createEditorPane(ABOUT_PAGE, MY_WIDTH, MY_HEIGHT));
                    tabPane.addTab("Changelog", createEditorPane("http://www.cabra.hathix.com/changelog/" + Utils.sanitizeURL(VERSION) + ".php", MY_WIDTH, MY_HEIGHT));
                    tabPane.addTab("Thanks",createEditorPane("http://www.cabra.hathix.com/cabra/thanks.php", MY_WIDTH, MY_HEIGHT));
                    tabPane.addTab("License",createEditorPane("http://www.gnu.org/licenses/gpl-3.0-standalone.html", MY_WIDTH, MY_HEIGHT));    
                }
            });
            
            
            tabPane.setPreferredSize(new java.awt.Dimension(MY_WIDTH,MY_HEIGHT));
            
            return tabPane;
        }
    }
}

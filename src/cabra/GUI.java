/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cabra;

/**
 *
 * @author Neel
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
//import java.io.*;
import java.util.ArrayList;
import javax.swing.UIManager.*;

public final class GUI extends Object {
    //basically creates the GUI and shows it
    
    private JFrame frame;
    private ProjectListPanel projectListPanel;
    private TabPane tabPane;
    private TopMenuBar menuBar;
    private Controller controller;
    
    public static final int FRAME_WIDTH = 590;
    public static final int FRAME_HEIGHT = 450;
    
    public GUI(final Controller controller, ArrayList<Project> projects){
        this.controller = controller;
        
        GUI.setNimbusLookAndFeel();

        //build frame... lack of project doesn't matter yet
        frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(FRAME_WIDTH,FRAME_HEIGHT);
        frame.setResizable(false);
        frame.setIconImages(createIconImages());
        //move the frame over a bit
        //Utils.changeFrameLocation(frame,100,100); 
        Utils.centerOnScreen(frame);
        //ask if you want to save notes upon exit
        frame.addWindowListener(new WindowAdapter(){
            @Override
                    public void windowClosing(WindowEvent e){
                        //save... don't ask for their confirmation, that's not necessary
                        //make sure there's a project first
                        if(controller.getActiveProject() != null){
                            saveAllNotes();
                        }                  
                    }
        });
       
        //this.setFrameTitleByProject(controller.getActiveProject());
        
        //standard initialization code

        //add the left panel
        projectListPanel = new ProjectListPanel(projects,controller,this);
        projectListPanel.createPanel();
        frame.add(BorderLayout.WEST,projectListPanel);
        
        //add right panel
        tabPane = new TabPane(projects,controller,this);
        tabPane.createPanel();
        frame.add(BorderLayout.CENTER,tabPane);
        
        //add the menu bar
        menuBar = new TopMenuBar(controller,this);
        frame.setJMenuBar(menuBar); 
        
        //set default color scheme
        //controller.setTheme(Themes.getCurrentTheme());
        //controller.setTheme(Themes.SPRING);
        
        //finally make the frame visible 
        SwingUtilities.updateComponentTreeUI(frame);         
        
        //frame.setVisible(true);
    }
    
    private ArrayList<Image> createIconImages(){
        //create image icons of different sizes
        ArrayList<Image> images = new ArrayList<Image>();
        int[] sizes = new int[]{12,16,20,32,64}; //sizes, in pixels, of available icons
        
        for(int size : sizes){
            //add a picture with that size
            images.add(GUI.createImageIcon("goat" + size + ".png").getImage());
        }
        
        return images;
    }
    
    public TabPane getTabPane(){
        return tabPane;
    }
    
    public JFrame getFrame(){
        return frame;
    }
     
    public void makeFrameVisible(){
        frame.setVisible(true);
        repaint();
    }
    
    public ImageIcon requestImage(){
        //open file chooser
        return InputManager.requestImage(frame);      
    }
    
    public java.io.File requestImageFile(){
        return InputManager.requestImageFile(frame);
    }
    
    /**Queries the user for text and then returns it
     * 
     * @param whatToAskFor something like "name of your project:" to prompt the user for
     * @param isCancelOK true if you're allowed to cancel, false if not
     * @return what the user inputted
     */
    
    public String getUserInput(String whatToAskFor,boolean isCancelOK){
        return InputManager.getUserInput(whatToAskFor,"",isCancelOK,frame);
    }
    
    /**Asks the user to confirm something.
     * 
     * @deprecated use InputManager.confirm(whatToAsk,gui.getFrame()) instead.
     * @param whatToAsk the text to present to the user
     * @return true if they confirmed, false otherwise
     */
    
    public boolean confirm(String whatToAsk){
        return InputManager.confirm(whatToAsk,frame);
    }
    
    public void update(){
        frame.validate();
        frame.repaint();
        SwingUtilities.updateComponentTreeUI(frame);        
    }
    
    public void refresh(){
        //called when a new project is set
        tabPane.refresh();
        projectListPanel.refresh();
        menuBar.refresh();
    }
    
    public void refreshHomePage(){
        tabPane.refreshHomePage();
    }
    
    /**Called when a new project is set as active.
     * 
     * @param project the new project that's active, or null if there is none
     */
    public void newActiveProject(Project project){
        tabPane.newActiveProject(project);
        setFrameTitleByProject(project);
        
        //projectListPanel.activateProject(project);
    }
    
    /** Sets the frame's title based on the project name.
     * 
     * @param project the active project, pass <code>null</code> if there's no active project
     */
    public void setFrameTitleByProject(Project project){
        if(project == null)
            frame.setTitle(About.PROGRAM_NAME);
        else
            frame.setTitle(project.getName() + " - " + About.PROGRAM_NAME);
    }
    
    public void setActiveTab(int tabIndex){
        //sets the active tab
        tabPane.setActiveTab(tabIndex);
    }
    
    /** Call this when switching to a new project.
     * 
     */
    
    public void saveAllNotes(){
        tabPane.updateNotes();
    }
    
    public void addProject(Project project){
        //adds the project to the view pane
        projectListPanel.addProject(project);
    }
    
    public void repaint(){
        //frame.repaint(); //don't do this; updateComponentTreeUI already calls repaint()
        //frame.validate();
        SwingUtilities.updateComponentTreeUI(frame);
    }
    
    /**
     * Updates the badge showing how many points were earned.
     * @param pointsEarned how many points were earned
     */
    public void showPointsBadge(int pointsEarned){
        menuBar.updatePointsEarnedBadge(pointsEarned);
    }
    
    public static ImageIcon createImageIcon(String path){
        return ImageManager.createImageIcon(path);
    }
    
    public static ImageIcon createImageIconFromFullPath(String path){
         return ImageManager.createImageIconFromFullPath(path);
    }
    
    public static ImageIcon scaleImage(ImageIcon image, int width, int height){
        return ImageManager.scaleImage(image,width,height);
    }

/** Changes the GUI's default theme to none. Be sure to setNimbusLookAndFeel() later.
 * 
 */

public static void resetLookAndFeel(){
            LookAndFeel none = null;
            try {
                UIManager.setLookAndFeel(none);
            } catch (UnsupportedLookAndFeelException ex) {
                System.out.println("Can't be done");
            }
}

/** Makes the current L&F the Nimbus one.
 * 
 */
public static void setNimbusLookAndFeel(){
    //set look and feel
    try {
        //go for Nimbus
        for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
            if ("Nimbus".equals(info.getName())) {
                UIManager.setLookAndFeel(info.getClassName());
                break;
            }
        }
    } catch (Exception e) {
        //Nimbus is not available
        try{
            //try for a generic system theme
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch(Exception f){
            //that's weird... must be a problem since cross-platform is always there
            throw new InternalError("Look and feel is not available!");
        }
    }    
}

/** Changes the frame's location to a more central screen location*/
 /** @param frame the frame to be moved
 * @param X how far right to move the frame
 * @param Y how far down to move the frame
 */
public static void changeFrameLocation(Component frame, int X, int Y){
    Utils.changeFrameLocation(frame, X, Y);
}

/* Changes the given JComponent so it's big, according to Nimbus
 */
public static void makeLarge(JComponent component){
    component.putClientProperty("JComponent.sizeVariant", "large");
}

public static void makeSmall(JComponent component){
    component.putClientProperty("JComponent.sizeVariant","small");
}

}

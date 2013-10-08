package cabra;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.beans.*;
import javax.swing.filechooser.*;

/** A bunch of utility input-requesting methods.
 *
 * @author Neel
 */
public abstract class InputManager {
    
    private InputManager(){} //can't be instantiated
    
    public static File requestZipFile(JFrame frame,String title){
         JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle(title);
                //this filter only lets zip files be chosen
                FileNameExtensionFilter filter = new FileNameExtensionFilter(
                    "Cabra projects (*." + Zipper.EXTENSION + ")", Zipper.EXTENSION);
                fileChooser.setFileFilter(filter);
                fileChooser.setAcceptAllFileFilterUsed(false); //disable All Files filter
                FileView view = new FileView(){
                    @Override
                    public Icon getIcon(File file) {
                        //show a special picture for zipped cabra projects
                        Icon icon = null;
                        
                        String extension = Utils.getExtension(file);
                        if(extension != null && extension.equals(Zipper.EXTENSION)){
                            //it's the one
                            icon = GUI.createImageIcon("goatfile.png");
                        }
                        return icon;
                    }                 
                };
                fileChooser.setFileView(view);                
         int returnVal = fileChooser.showOpenDialog(frame);
         if(returnVal == JFileChooser.APPROVE_OPTION) {
            //so the user has selected a zipped file          
            String path = fileChooser.getSelectedFile().getAbsolutePath();
            if(Utils.endsWith(path,Zipper.EXTENSION)){
                return new File(path);
            }
            else{
                //bad file, request another
                return requestZipFile(frame,title);
            }
         }     
         else{
             //the user declined, so return null
             return null;
         }       
    }
    
    /**
     * Asks the user to choose a folder.
     * @param frame the file chooser's parent frame
     * @param title the title for the dialog
     * @return the file (folder) if the user chose one, null if they canceled
     */
    public static File requestFolder(JFrame frame,String title){
         JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle(title);
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY); //you can only select folders
        
         int returnVal = fileChooser.showDialog(frame, "Select");
         if(returnVal == JFileChooser.APPROVE_OPTION) {
            //so the user selected something          
            String path = fileChooser.getSelectedFile().getAbsolutePath();
            File file = new File(path);
            return file;
         }     
         else{
             //the user declined, so return null
             return null;
         }     
    }

    public static File requestImageFile(JFrame frame){
         JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Choose an image to attach:"); 
                //this filter only lets certain images be chosen
                FileNameExtensionFilter filter = new FileNameExtensionFilter(
                    "Images (*.png, *.gif, *.jpg, *.jpeg)", "png", "jpg", "jpeg", "gif");
                fileChooser.setFileFilter(filter);
                fileChooser.setAcceptAllFileFilterUsed(false); //disable All Files filter
                
                FileView view = new FileView(){
                    @Override
                    public Icon getIcon(File file) {
                        //show a special picture for usable pictures
                        Icon icon = null;
                        
                        String extension = Utils.getExtension(file);
                        if(extension != null && 
                                (extension.equals("png")
                                ||extension.equals("jpg")
                                ||extension.equals("jpeg")
                                ||extension.equals("gif")
                                )){
                            //it's the one
                            icon = GUI.createImageIcon("pics.png");
                        }
                        return icon;
                    }                 
                };           
                fileChooser.setFileView(view);
                
                fileChooser.setAccessory(new Previewer(fileChooser));
         int returnVal = fileChooser.showDialog(frame,"Attach");
         if(returnVal == JFileChooser.APPROVE_OPTION) {
            //so the user has selected a picture             
            String path = fileChooser.getSelectedFile().getAbsolutePath();
            if(Utils.endsWith(path,".gif",".png",".jpg",".jpeg")){
                return new File(path);
            }
            else{
                //bad file, request another
                return requestImageFile(frame);
            }
         }     
         else{
             //the user declined, so return null
             return null;
         }
    }
    
    static class Previewer extends JComponent implements PropertyChangeListener {
        ImageIcon thumbnail = null;
        File file = null;

        public Previewer(JFileChooser fileChooser) {
            setPreferredSize(new Dimension(100, 50));
            fileChooser.addPropertyChangeListener(this);
        }

        public void loadImage() {
            if (file == null) {
                thumbnail = null;
                return;
            }

            ImageIcon tmpIcon = new ImageIcon(file.getPath());
            if (tmpIcon != null) {
                try{
                    thumbnail = ImageManager.scaleImage(tmpIcon,100,100);
                }
                catch(IllegalArgumentException e){
                    //width and height of image are -1
                    thumbnail = null;
                    return;
                }
            }
        }

        public void propertyChange(PropertyChangeEvent e) {
            boolean update = false;
            String prop = e.getPropertyName();

            //If the directory changed, don't show an image.
            if (JFileChooser.DIRECTORY_CHANGED_PROPERTY.equals(prop)) {
                file = null;
                update = true;

            //If a file became selected, find out which one.
            } else if (JFileChooser.SELECTED_FILE_CHANGED_PROPERTY.equals(prop)) {
                file = (File) e.getNewValue();
                update = true;
            }

            //Update the preview accordingly.
            if (update) {
                thumbnail = null;
                if (isShowing()) {
                    loadImage();
                    repaint();
                }
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            if (thumbnail == null) {
                loadImage();
            }
            if (thumbnail != null) {
                //offset it
                int width = thumbnail.getIconWidth();
                int height = thumbnail.getIconHeight();

                //get offsets in order to center picture
                int verticalOffset = (getHeight() - height) / 2;
                int horizontalOffset = (getWidth() - width) / 2;
              
                thumbnail.paintIcon(this, g, horizontalOffset, verticalOffset);
            }
        }
    }      
    
    public static ImageIcon requestImage(JFrame frame){
        File file = requestImageFile(frame);
        if(file == null) return null;
        return GUI.createImageIconFromFullPath(file.getAbsolutePath());
    }


    
    /**Asks the user to select a project and returns it.
     * 
     * @param whatToSay what to ask the user
     * @param projects an array of all projects
     * @param frame the frame
     * @return the chosen project
     */
    public static Project getProject(String whatToSay,Project[] projects,JFrame frame){
        Project project = (Project)JOptionPane.showInputDialog(
                            frame,
                            whatToSay,
                            "Cabra Input",
                            JOptionPane.PLAIN_MESSAGE,
                            GUI.createImageIcon("goatinput.png"),
                            projects,
                            projects[0]);  
        return project;
    }
    
    public static String getUserInput(String whatToAskFor, String defaultText, boolean isCancelOK,JFrame frame){
        //InputDialog inputDialog = new InputDialog(frame,whatToAskFor);
        //System.out.println(inputDialog.getInput());
        //return null;
        
        String input = ""; 
        //Object[] options = {GUI.createImageIcon("check_ok.png"), GUI.createImageIcon("cancel_cancel.png")};
            //button options
        
        while(true){
            //keep asking for input as long as what they said was either none or invalid
           
            input = (String)JOptionPane.showInputDialog(
                        frame,
                        whatToAskFor,
                        "Cabra Input",
                        JOptionPane.PLAIN_MESSAGE,
                        GUI.createImageIcon("goatinput.png"),
                        null,
                        defaultText);
            
            if(input == null && isCancelOK)
                return null; //the user hit cancel. if canceling is ok, return null
            
            if(input == null || input.equals(""))
                continue; //they tried to cancel
            
            input = Sanitizer.sanitize(input); //remove illegal characters
            if(input.equals(""))
                continue; //it's made up of only invalid characters
            break;
        }
        return input;        
    }
    
    public static boolean confirm(String whatToAsk,JFrame frame){
        //Custom buttons
        Object[] options = new Object[] { "Yes","No" };
        //Object[] options = new Object[] { GUI.createImageIcon("check_yes.png"), GUI.createImageIcon("cancel_no.png") };
            //the checkyes and cancelno have the words built in
        //Object[] options = new Object[] { new JButton("Yes",GUI.createImageIcon("check.png")),new JButton("No",GUI.createImageIcon("cancel.png")) };
            //the flaw with this is that nothing happens when the buttons are clicked
        int choice = JOptionPane.showOptionDialog(frame,
            whatToAsk,
            "Cabra Confirm",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.PLAIN_MESSAGE,
            GUI.createImageIcon("goatconfirm.png"),
            options,
            options[1]); //the default selection is no
        return choice == JOptionPane.YES_OPTION;        
    }
    
    /** Asks the user to choose a color.
     * 
     * @param frame the gui's frame
     * @return the chosen color, or null if nothing is chosen
     */
    /*public static Color getColor(JFrame frame){
        Color color = JColorChooser.showDialog(frame, "Choose a color", Color.black);
        return color;
    }*/
}

package cabra;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 *
 * @author Neel
 */
public final class SettingsPanel extends JTabbedPane{
    
    private Controller controller;
    private GUI gui;
    
    public static final int MY_WIDTH = 400;
    public static final int MY_HEIGHT = 300;
    
    public SettingsPanel(Controller controller, GUI gui){
        this.controller = controller;
        this.gui = gui;
        
        /** NOW ADD STUFF **/
        
        addGeneralRow();
        addProjectFolderRow();
        addFontRow();
        addStudyingRow();
        
        setPreferredSize(new Dimension(MY_WIDTH - 20, MY_HEIGHT - 100));
    }
    
    /**
     * Creates a panel containing this tabbed pane and returns it.
     * @return a JPanel
     */
    public JPanel getPanel(){
        JPanel holder = new JPanel(){
            @Override
            public void paintComponent(Graphics g){
                super.paintComponent(g);

                Utils.drawEmblem(this, g);                
            }
        };
        //holder.setLayout(new GridLayout(1,1));
        holder.add(this);
        
        return holder;
    }

    /**
     * Adds a general row (check for updates.)
     */
    private void addGeneralRow(){
        //JSpinner where they can change the check for updates interval
        int updateInterval = UserData.getIntPref("UpdateInterval");
        SpinnerModel spinnerModel = new SpinnerNumberModel(updateInterval, 1, 31, 1); //start - min - max - step
        final JSpinner spinner = new JSpinner(spinnerModel);
        spinner.addChangeListener(new ChangeListener(){
            public void stateChanged(ChangeEvent e){
                //get the new value
                SpinnerNumberModel model = (SpinnerNumberModel) spinner.getModel();
                int newInterval = model.getNumber().intValue();
                //update pref
                setPref("UpdateInterval", newInterval + "");
            }
        });
        
        JPanel holder = new JPanel();
        JLabel label = new JLabel("Days between checking for updates");
        holder.add(label);
        holder.add(spinner);
        
        addRow("General", holder);
    }
    
    /**
     * Creates a row where the user can change the settings for studying (cards per session.)
     */
    private void addStudyingRow(){
        //JSpinner where they can change the max cards in a session
        int maxCards = UserData.getIntPref("MaxSession");
        SpinnerModel spinnerModel = new SpinnerNumberModel(maxCards, 10, 200, 10); //start - min - max - step
        final JSpinner spinner = new JSpinner(spinnerModel);
        spinner.addChangeListener(new ChangeListener(){
            public void stateChanged(ChangeEvent e){
                //get the new value
                SpinnerNumberModel model = (SpinnerNumberModel) spinner.getModel();
                int newMaxCards = model.getNumber().intValue();
                //update pref
                setPref("MaxSession", newMaxCards + "");
            }
        });
        
        //create holder to hold a label and the spinner
        JPanel holder = new JPanel();
        JLabel label = new JLabel("<html><center>Max. cards to study in<br>a session. Reduce this<br>to have shorter sessions.");
        //label.setPreferredSize(new Dimension(200, 200));
        holder.add(label);
        holder.add(spinner);
        
        //add it
        addRow("Studying", holder);
    }
    
    /**
     * Creates a row where the user can change the font size and name
     */
    private void addFontRow(){
        Font preferredFont = FontManager.PREFERRED_FONT;
        
        final JLabel fontPreview = new JLabel("<html><center><i>Preview:</i> The quick brown fox jumped over the lazy dog");
        fontPreview.setToolTipText("Font preview");
        fontPreview.setHorizontalAlignment(JLabel.CENTER);
        fontPreview.setFont(preferredFont);
        
        //gridded JPanel to hold size/name
        JPanel holder = new JPanel(new GridLayout(2,2));
        
        //sizeRow has label saying "Size" and a size combo box
        //JPanel sizeRow = new JPanel();     
        int[] sizes = FontManager.GOOD_FONT_SIZES;
        final JComboBox fontDrop = new JComboBox(Utils.toIntegerArray(sizes));
        //<TODO>: make font sizes in their font size ("12" in 12pt)
        //what should the starting value be?
        int preferredSize = preferredFont.getSize();
        for(int i=0; i<sizes.length; i++){
            if(sizes[i] == preferredSize)
                fontDrop.setSelectedIndex(i);
        }
        //handle changes
        fontDrop.addActionListener(new ActionListener(){
           public void actionPerformed(ActionEvent e){
               //determine what the new font size is
               Object selected = fontDrop.getSelectedItem();
               int newFontSize = ((Integer)selected).intValue();
               
               //change preferred font; this will change prefs while it's at it
               FontManager.updatePreferredFont(null, newFontSize); //don't change name
               //update preview
               fontPreview.setFont(FontManager.PREFERRED_FONT);
               //alert controller
               controller.refresh();
           } 
        });
        //add to sizeRow
        holder.add(new JLabel("Font Size"));
        holder.add(fontDrop);
        
        //font name row
        //JPanel nameRow = new JPanel();
        //String[] names = FontManager.getAvailablePreferredFontNames();
        String[] names = FontManager.getAllAvailableFontNames();
        final JComboBox nameDrop = new JComboBox(names);
        //what should the starting value be?
        String preferredName = preferredFont.getName();
        for(int i=0; i<names.length; i++){
            if(names[i].equals(preferredName))
                nameDrop.setSelectedIndex(i);
        }        
        //handle changes
        nameDrop.addActionListener(new ActionListener(){
           public void actionPerformed(ActionEvent e){
               //determine what the new font name is
               String newFontName = (String)nameDrop.getSelectedItem();
               
               //change preferred font; this will change prefs while it's at it
               FontManager.updatePreferredFont(newFontName, 0); //don't change size
               //update preview
               fontPreview.setFont(FontManager.PREFERRED_FONT);
               //alert controller
               controller.refresh();
           } 
        });        
        //add to nameRow
        holder.add(new JLabel("Font Name"));
        holder.add(nameDrop);
        
        addRow("Font", fontPreview, holder);
    }
    
    /**
     * Adds a row about choosing a new project folder.
     */
    private void addProjectFolderRow(){
        //create text field
        final JTextField projectFolderTextField = new JTextField();
        projectFolderTextField.setFont(FontManager.PREFERRED_FONT);
        projectFolderTextField.setEditable(false);
        projectFolderTextField.setText(getPref("ProjectFolder"));
        
        //create "move projects" button
        JButton moveProjectsButton = new JButton("Move projects to new folder");
        moveProjectsButton.setToolTipText("Move all your projects to a new folder");
        moveProjectsButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                //get new folder
                File newFolder = InputManager.requestFolder(gui.getFrame(), 
                        "To which folder do you want to move your projects?");
                if(newFolder == null) return;
                
                if(newFolder.listFiles().length != 0){
                    //now just make a subfolder, too much confusion otherwise
                    /*//there's already files in the folder, ask what user wants to do (overwrite possibly, make subfolder, cancel
                    String[] options = new String[]{ "Use this folder", "Create subfolder (recommended)", "Cancel" };
                    String text = "<html><center>Warning: the folder you chose to store your projects already contains some files.<br>"
                            + "Do you want to <b>use this folder</b> (and possibly overwrite existing files),<br>"
                            + "or <b>create a subfolder</b> inside this folder to store your projects (<u>recommended</u>)?";
                    int returnValue = JOptionPane.showOptionDialog(
                            gui.getFrame(), 
                            text,
                            "Choosing new folder", 
                            JOptionPane.DEFAULT_OPTION,
                            JOptionPane.PLAIN_MESSAGE,
                            ImageManager.createImageIcon("goat64.png"), 
                            options,
                            null
                        );
                    switch(returnValue){
                        case JOptionPane.CLOSED_OPTION:
                        case 2:
                            //user closed dialog or chose the Cancel button
                            return;
                        case 0:
                            //user wants to use this folder
                            break;
                        case 1:*/
                            //user wants to make subfolder inside this folder
                            File subFolder = new File(newFolder.getAbsolutePath() + "/CabraProjects");
                            subFolder.mkdir();
                            newFolder = subFolder;
                           // break;
                    //}
                }
               
                //move current project folder to the new one
                File projectFolder = SaveLoad.getProjectFolder();
                for(File oldFile : projectFolder.listFiles()){
                    //move it into the new folder
                    if(oldFile.getName().equals("UserData.txt")) continue; //don't move user data file
                    //create a new file (new folder's path, old file's name) and move old to new
                    File newFile = new File(newFolder.getAbsolutePath() + "/" + oldFile.getName());
                    oldFile.renameTo(newFile);
                }
                
                //update user data
                setPref("ProjectFolder", newFolder.getAbsolutePath());
                
                //update text field
                projectFolderTextField.setText(newFolder.getAbsolutePath());
            }
        });
       
        //create "load from new folder" button
        JButton newFolderButton = new JButton("Load projects from new folder");
        newFolderButton.setToolTipText("Keep your old projects intact, but load projects from a different folder");
        newFolderButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                //get new folder
                File newFolder = InputManager.requestFolder(gui.getFrame(), 
                        "Which folder should Cabra load projects from?");
                if(newFolder == null) return;
                
                //all we do here is ask for a new folder, then set that as the new project folder
                //no need to check for overwrite or anything since we aren't moving files
                
                //update user data
                setPref("ProjectFolder", newFolder.getAbsolutePath());
                
                //update text field
                projectFolderTextField.setText(newFolder.getAbsolutePath());
                
                //alert user that changes will take effect next time
                Utils.showDialog(
                        gui.getFrame(),
                        "<html><center>The next time you load Cabra, your projects will be loaded from<br>"
                        + "<i>" + newFolder.getAbsolutePath() + "</i>.",
                        "New project folder"//,
                        //"goat64.png"
                    );
            }
        });        
        
       /* //put text field and buttons in holder panel
        JPanel holder = new JPanel(new GridLayout(3,1));
        holder.add(projectFolderTextField);
        holder.add(moveProjectsButton);
        holder.add(newFolderButton);*/
        
        //JLabel descriptor
        JLabel description = new JLabel("<html><center>Projects are loaded from this folder:");
        description.setHorizontalAlignment(SwingConstants.CENTER);
        
        addRow("Project Location", description, projectFolderTextField, moveProjectsButton, newFolderButton);        
    }
    
    /**
     * Adds a tab with the given components.
     * @param title the title of the tab
     * @param components the components to add; will be put in a GridBagLayout and put in the tabbed panel
     */
    public void addRow(String title, JComponent... components){
        //create panel to hold components
        JPanel holder = putInPanel(components);
        
        addTab(title, holder);
    }
    
    /**
     * Adds the given JComponents to a panel that can be put into the tabbed pane.
     * @param components the component to add. They will be placed in a GridBagLayout.
     * @return a panel containing the components.
     */
    private JPanel putInPanel(JComponent... components){
        //unlimited rows, 1 column
        JPanel holder = new JPanel(new GridBagLayout());
        GridBagConstraints c2 = new GridBagConstraints();
        c2.fill = GridBagConstraints.BOTH;
        c2.insets = new Insets(3,3,3,3);
        c2.weightx = 1;        
        c2.gridx = 0;
        c2.gridy = 0;
        
        for(JComponent component : components){
            holder.add(component, c2);
            c2.gridy++;
        }
        
        return holder;
    }
    
    /**
     * Utility method for the very lazy. Gets a certain preference.
     * @param pref the preference's name
     * @return the value of the preference
     */
    private static String getPref(String pref){
        return UserData.getPref(pref);
    }
    
    /**
     * Utility method for the very lazy. Sets a certain preference.
     * @param pref the preference's name
     * @param value the new value of the preference
     */
    private static void setPref(String pref, String value){
        UserData.setPref(pref, value);
    }    
}

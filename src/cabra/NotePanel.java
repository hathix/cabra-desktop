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
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;

/** A panel that lets you write down notes and look at them too
 * 
 * 
 */

public class NotePanel extends JPanel{
    
    private NoteTabPane tabPane; //events here are handled there
    private GUI gui; //used for confirm box use
    private Controller controller;
    private NoteTextPane textPane; //where you can put your text
    private JToolBar toolbar; //toolbar with buttons for the textPane on it
        private JMenu fontSizes; //for the font sizes dropping down
        private JMenu fontFamilies; //sans-serif, serif, etc.
        private JMenu fontColors; //red, blue, etc.
    
    private Note note; //each notePanel has its own note it takes care of
    
    /**Creates a new note panel to add to the note tab pane
     * 
     * @param tabPane The tab pane that created this
     * @param note The note this tab is associated with (create it earlier)
     */
    
    public NotePanel(NoteTabPane noteTabPane,GUI gui,Controller controller,Note note){
        this.tabPane = noteTabPane;
        this.gui = gui;
        this.controller = controller;
        this.note = note;        
        
        textPane = new NoteTextPane(note.getDocument(),this);
        JScrollPane scroller = new JScrollPane(textPane);
        scroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        
        //set size of scroller
        int width = NoteTextPane.PANE_WIDTH;
        int height = NoteTextPane.PANE_HEIGHT;
        scroller.setPreferredSize(new Dimension(width,height));
        //scroller.setMaximumSize(new Dimension(width,height));        
        
        add(scroller);
        
        //note panel initialization
        //JScrollPane scrollPane = new JScrollPane(textPane);
        //scrollPane.setPreferredSize(new Dimension(200, 200));        
        
        //set text area's text to note's text
        //textPane.setText(note.getText()); 
        
        //before we add save/delete, add the toolbar
        addToolbar();
        
       // //now add the textpane's buttons to this toolbar
        //textPane.addUndoRedoButtons(this); 
        
        
        //add save/delete buttons
        JButton save = new JButton("Save",GUI.createImageIcon("floppy.png"));
            save.addActionListener(new SaveListener());
            add(save);
        
        JButton delete = new JButton("Delete",GUI.createImageIcon("trash.png"));
            delete.addActionListener(new DeleteListener());
            add(delete);
    }
    
    /* TOOLBAR STUFF */
    
    /** Creates the toolbar to go below the core note buttons
     * 
    */
    private void addToolbar(){
        createStyleToolbar();
        //fix it a little
        toolbar.setFloatable(false); //can't be dragged
        toolbar.setRollover(true); //shows box when moused over 
        toolbar.setBorderPainted(false);
        //add it to the panel
        add(toolbar);
    }
    
   //Create and add the style/edit toolbar.
    private void createStyleToolbar() {
        toolbar = new JToolBar("Note Editor");    
        
        Action action;

        action = new StyledEditorKit.BoldAction();
        action.putValue(Action.NAME, "Bold");
        addToolbarButton(action,"bold.png","Bold");
        
        action = new StyledEditorKit.ItalicAction();
        action.putValue(Action.NAME, "Italic");
        addToolbarButton(action,"italic.png","Italic");

        action = new StyledEditorKit.UnderlineAction();
        action.putValue(Action.NAME, "Underline");
        addToolbarButton(action,"underline.png","Underline");
        
        toolbar.addSeparator();
        
        JMenuBar menuBar = new JMenuBar(); //the jmenus go on here
        
        fontFamilies = new JMenu("Font");
        String randomFontName = FontManager.randomFontName();
        addFontFamilyMenuItems(new String[]{"Times New Roman","Times New Roman"},
                               new String[]{"Arial","Arial"},
                               new String[]{"Georgia","Georgia"},
                               new String[]{"Calibri","Calibri"},
                               new String[]{"Lao UI","Lao UI"},
                               new String[]{"Courier New","Courier New"},
                               new String[]{"Ubuntu","Ubuntu"},
                               new String[]{"Geneva","Geneva"},
                               new String[]{randomFontName,randomFontName}
                               );
        menuBar.add(fontFamilies);
        
        fontSizes = new JMenu("Size");
        addFontSizeMenuItems(12,14,16,18,20); //don't use the more restrictive FontManager.GOOD_FONT_SIZES
        menuBar.add(fontSizes);
        
        fontColors = new JMenu("Color");
        addColorMenuItems(  new Object[]{"Black",Color.BLACK},
                            new Object[]{"Red",Color.RED},
                            new Object[]{"Green",ColorManager.createColor("009100")},
                            new Object[]{"Blue",Color.BLUE},
                            new Object[]{"Purple",ColorManager.createColor("B200FF")},
                            new Object[]{"Random",ColorManager.randomColor()} /* we don't want overly-light colors */
                            );
        //create menu item that requests a color
        final JMenuItem chooseColor = new JMenuItem("Choose your own");
        /*chooseColor.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                
            }
        });*/
        
        menuBar.add(fontColors);
        
        //finally add the menu bar to the toolbar  
        toolbar.add(menuBar);
        
        toolbar.addSeparator();
        
        //now that all the other components are on the toolbar, add the undo and redo buttons to the far right
        textPane.addUndoRedoButtons(this); //creates redo/undo buttons and adds it to our toolbar     
          
        toolbar.addSeparator();          
        
        //image inserter
        action = new InsertImageAction();
        action.putValue(Action.NAME, "Insert image");
        addToolbarButton(action,"insertimage.png","Insert an image");     
    }
    
    /** Creates a menu item for each color and adds it to the fontColors menu.
     * 
     * @param namesAndColors each Object[] contains the name of the color and the color itself, i.e. "Red",Color.red
     */
    
    private void addColorMenuItems(Object[]... namesAndColors){
        for(Object[] nameAndColor : namesAndColors){
            //unpack
            String name = (String)nameAndColor[0];
            Color color = (Color)nameAndColor[1];
   
            String html = "<html><span style='color:\"#" + ColorManager.toHex(color) + "\";'>" + name;
            
            //add the menu item
            fontColors.add(new StyledEditorKit.ForegroundAction(html,color));
        }
    }
    
    /** Creates a menu item for each font family (if available) and adds it to the fontFamilies menu item
     * 
     * @param fontNames the first one is the shown name, the second is the proper name
     */
    
    private void addFontFamilyMenuItems(String[]... fontNames){
        for(String[] names : fontNames){
            //does the user have the font installed?
            if(FontManager.hasFontName(names[1])){
                //write the text in its own font 
                String html = "<html><span style='font-family:\"" + names[1] + "\";'>" + names[0]; //use proper name here
                //create an action that uses the second and first one
                fontFamilies.add(new StyledEditorKit.FontFamilyAction(html,names[1]));                
            }
        }
        if(fontFamilies.getMenuComponents().length == 0){
            //not a single font worked, so add sans-serif and serif
            addFontFamilyMenuItems( new String[]{"Serif","Serif"},
                                    new String[]{"Sans-Serif","SansSerif"}
                                    );
        }
    }
    
    /** Creates a menu item for each font size and adds it to the fontSizes menu item.
     * 
     * @param size a list of font sizes
     */
    
    private void addFontSizeMenuItems(int... sizes){
        for(int size : sizes){
            //show size in the actual size (so "18" looks larger than "12")
            String html = "<html><span style='font-size:" + size + "px;'>" + size;
            fontSizes.add(new StyledEditorKit.FontSizeAction(html,size));
        }
    }
    
    /** Creates a JButton given the parameters and adds it to the toolbar. ActionListeners are built in.
     * 
     * @param onClick the Action to be taken when the button is clicked
     * @param imagePath the path to the image, i.e. "save.png"
     * @param toolTip the text to be displayed while mousing over the button
     * @return the created JButton that was added to toolbar
     */
       
    public JButton addToolbarButton(Action onClick, String imagePath,String toolTip){
        JButton button = new JButton(GUI.createImageIcon(imagePath)); //create the button
        button.setToolTipText(toolTip); //add tool tip text
        
        //add given action listener
        button.addActionListener(onClick);
        //add a generic action listener that causes focus on the text pane
        button.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                //give focus to text pane
                focusOnTextPane();
            }
        });
        
        //add to toolbar
        toolbar.add(button);
        return button;
    }
    
    
    
    
    /* MORE STUFF */
    
    public void focusOnTextPane(){
        //actually this is irritating; don't do that
        //focusOnTextPane();
    }
    
    /** Updates this guy's note's text, and starts the save ball rolling
     * 
     */
    
    public void saveNotes(){
         //wait a bit
          /*String newText = textPane.getText();
          if(newText.equals(note.getText())){
              //nothing changed, so why save?
              return;
          }
          note.setText(newText); //update the note's text*/
        StyledDocument document = (StyledDocument) textPane.getDocument();
        note.setDocument(document);
        tabPane.saveNotes();      
    }
    
     /**
     * 
     * @return the name of the note, which is the same as the name of this panel
     */
        public String getPanelName(){
            return note.getName(); //this guy's title is the same as the note's
        }
        
        public Note getNote() { return note; }
        
    @Override
        public boolean equals(Object aNotePanel){
            if(aNotePanel == null || aNotePanel instanceof NotePanel == false) return false;
            NotePanel notePanel = (NotePanel) aNotePanel;
            return this.note.equals(notePanel.note); //check for note equality
        }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 61 * hash + (this.note != null ? this.note.hashCode() : 0);
        return hash;
    }
        
     //CONFIRM BOX STUFF
    
    public void delete(){
        //ask for confirmation
        boolean shouldDelete = gui.confirm("<html><center>Are you sure you want to delete <b>" + note.getName() + "</b>?");
        if(shouldDelete){
            tabPane.removeNotePanel(this); //does it all
        }
    }
    
    class InsertImageAction extends AbstractAction{

        public void actionPerformed(ActionEvent e) {
            //get an image
            java.io.File pictureFile = gui.requestImageFile();
            if(pictureFile != null){
                ImageIcon picture = GUI.createImageIconFromFullPath(pictureFile.getAbsolutePath()); 
                //move image to project
                //this also lets us get a permanent link to the image (when it's floating out on the host machine, it could move)
                //java.io.File newFile = controller.getActiveProject().movePictureFile(pictureFile);
                //ImageIcon newPicture = GUI.createImageIconFromFullPath(newFile.getAbsolutePath()); 
                //finally add the picture
                textPane.insertImageIcon(picture); 
            }
        }
        
    }
        
     class SaveListener implements ActionListener{
            public void actionPerformed(ActionEvent e){
                 saveNotes();
                 //focus the text pane to alert user that saving was successful
                 textPane.requestFocus();
            }
        }
     
     class DeleteListener implements ActionListener{
         public void actionPerformed(ActionEvent e){
             delete();
         }
     }
}

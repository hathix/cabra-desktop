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
//import java.awt.*;
import java.awt.event.*;

public class NewNoteTab extends JLabel{
    //this goes on the "New Note" tab
    //contains a button that lets you make a new tab
    //when new note is clicked we ask for the name of the new note tab
   
    private NoteTabPane tabPane; //when the new tab button is clicked, fire an event here
    private GUI gui; //used to call askForUserInput
    
    public NewNoteTab(NoteTabPane tabPane,GUI gui){
        this.tabPane = tabPane;
        this.gui = gui;
        
        this.setIcon(GUI.createImageIcon("note-add.png")); 
        //so all you see on this tab is the new icon
        this.setToolTipText("Create a new note");
        
        //when this actual thing is clicked, a new tab is made
        addMouseListener(new clickListener());
        
       // newTabButton = new JButton(null,GUI.createImageIcon("new.png"));
       // add(newTabButton);
    }
    // extend mouseAdapter to get click info 
    
    class clickListener extends MouseAdapter{
        @Override
                public void mouseClicked(MouseEvent e){
                    //they clicked on the new note icon
                    String noteName = gui.getUserInput("Name of note:", true);
                    if(noteName == null)
                        return; //that means they hit cancel
                    tabPane.addNotePanel(noteName);                    

                }
    }
}

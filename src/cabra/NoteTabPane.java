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
//import javax.swing.event.*;
import java.awt.*;
//import java.awt.event.*;
import java.util.ArrayList;

public class NoteTabPane extends JTabbedPane{
    //it's a tabbed pane inside the original tabbed pane. Go figure.
    //each tab is its own note page
    
    private TabPane mainTabPane; //the main tab pane ("Create card","Study","Notes")
    private GUI gui; //used to handle input requests
    private Controller controller; //for checking on note status
    
    public NoteTabPane(TabPane tabPane,GUI gui,Controller controller){
        this.mainTabPane = tabPane;
        this.gui = gui;
        this.controller = controller;
        
        //make tabs scroll if there are too many so they don't spill over
        setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        
        //create the tab pane... there's just one tab to start, but hey
        createNoteTab();
        
        
        /*addChangeListener(new ChangeListener(){
            public void stateChanged(ChangeEvent e){
                System.out.println(e);
            }
        });*/
    }   
    
    public void createNoteTab(){
        //make a new note tab
       this.addTab(null,createNewNotePanel());
       this.setTabComponentAt(getTabCount()-1, new NewNoteTab(this,gui)); //add the new note icon to the tab        
    }
    
    private JPanel createNewNotePanel(){
        return Utils.createAdvicePanel("<html><center>Create a new note by clicking the <b>picture of the note</b><br>on the new note tab.");
    }
    
    /** Deletes a certain note panel and its associated note
     * 
     */
    public void removeNotePanel(NotePanel notePanel){
        //first set the previous tab as active
        //get the index of the current tab
        int currentIndex = indexOfTab(notePanel.getPanelName()); //find index of current tab
        if(currentIndex == 0){
            //it's the first tab, in which case set the next tab as active
            setSelectedIndex(1); //either the next tab or the "new tab" tab
        }
        else{
            //set the one to the left as active
            setSelectedIndex(currentIndex - 1);
        }
        
        //remove the panel
        remove(notePanel);
        //remove the note from the project
        mainTabPane.removeNote(notePanel.getNote());
    }
    
    /**Finds the note panel that has the associated note.
     * 
     * @param note the note to find the associated panel of
     * @return the notepanel
     * @return null if there's no associated panel
     */
    private NotePanel findAssociatedPanel(Note note){
        for(Component component : this.getComponents()){
            //find the note panels
            if(component instanceof NotePanel){
                NotePanel panel = (NotePanel) component;
                //check its note
                if(panel.getNote().equals(note)){
                    //got it
                    return panel;
                }
            }
        }
        //didn't find anything
        return null;
    }
    
    /** Removes the tab with the given tab name
     * 
     * @param tabName the name of the tab to delete
     */
    
   // private void removeTabByName(String tabName){
    //    
    //}
    
    /** Empties the note panel and refills it with the current project's notes
     * 
     */
    public void refresh(){
        //clear the tabs
        removeAll();
        
        if(mainTabPane.getActiveProject() == null){
            //the active project was deleted
            dealWithNoActiveProject();
            return;
        }
        
        //load notes from the new active project
        //first get the new notes
        loadTabs(mainTabPane.getActiveProject().getNotes());                
    

    }
    
    /** Uses the given notes to fill up the tabs. Use this when loading tabs
     * 
     * @param notes - the given notes
     */
    
    private void loadTabs(ArrayList<Note> notes){
        //create a tab for each note... the new tab one will be taken care of later

        //add the new note panel to start
        createNoteTab();   
        
        for(Note note : notes){
            //create a note panel with that note and add it
            NotePanel notePanel = new NotePanel(this,gui,controller,note);
            addNoteTab(notePanel);
        }
        
    }
    
    /** Call this when you've made a note panel but need to add it
     * 
     * @param note the note to make a tab of
     */
    
    private void addNoteTab(NotePanel notePanel){
        //insert the tab
        int insertHere = getTabCount() - 1;
        this.insertTab(notePanel.getPanelName(), null, notePanel, "", insertHere);
        
        //focus on the newly inserted tab
        this.setSelectedIndex(insertHere);
        
        //tell the panel to focus on the text area of the new note panel
        notePanel.focusOnTextPane();        
    }
    
      
    /** Creates a new note panel with the given name. Only use this when the new note button is pressed.
     * 
     * @param panelName the name of the panel, also the name of the note associated with it
     */
    public void addNotePanel(String panelName){
        //called by the NewNoteTab button when a new note should be made
        //we're adding this tab at the second-to-last index (the last one is the New Note tab)
        
        //check for presence of notes with identical name to this one
        for(Note note : controller.getActiveProject().getNotes()){
            if(note.getName().equals(panelName)){
                //there's already a note with the same name
                //ask for confirmation "do you want to delete the old one?"
                String ask = ("<html><center>A note named <b>" + panelName + "</b> already exists.<br>Do you want to delete the old note and replace it with a blank one?");

                /*Object[] options = new Object[] { "Overwrite old note","Do nothing" };
                int choice = JOptionPane.showOptionDialog(
                                gui.getFrame(),
                                ask,
                                "Cabra Confirm",
                                JOptionPane.YES_NO_OPTION,
                                JOptionPane.PLAIN_MESSAGE,
                                GUI.createImageIcon("goatconfirm.png"),
                                options,
                                options[1]); //the default selection is no  */      
                
                if(gui.confirm(ask) == false){
                    //the user decided not to overwrite the old note
                    return;
                }
                else{
                    //they said yes; delete the note of that name
                    //first find the associated note panel
                    try{
                        NotePanel panel = findAssociatedPanel(note);
                        //delete the panel and its note
                        removeNotePanel(panel);
                    } catch(java.util.ConcurrentModificationException c){
                        //some sort of error
                    } catch(NullPointerException n){
                        //no associated note panel; do nothing
                    }
                }   
            }
        }
        
        //earn points for creating note
        controller.gainPoints(PointEnums.Activity.CREATE_NOTE);
        
        Note newNote = new Note(panelName);
        NotePanel notePanel = mainTabPane.addNote(newNote);
        addNoteTab(notePanel);
       
    }
    
    public void saveNotes(){
        mainTabPane.saveNotes();
    }
    
    public void updateNotes(){
        //tell each note panel to save itself
        for(int i=0;i<=this.getTabCount();i++){
            Component component = this.getComponent(i);
            //check if the component is a note tab pane
            if(component instanceof NotePanel){
                NotePanel notePanel = (NotePanel)component; //cast it to a note panel
                notePanel.saveNotes();
            }
        }
    }
    
    public void dealWithNoActiveProject(){
        //there's no active project...
        //that's OK, though, no tabs
    }
  
}

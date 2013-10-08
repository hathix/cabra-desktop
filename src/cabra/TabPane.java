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
import java.util.ArrayList;

public class TabPane extends JTabbedPane{
    //a tabbed pane that contains controls for viewing cards,
    //making cards/projects, and managing cards/projects
    
    //note that the panels added to this fire events here, and here you talk to Controller
    
    private Controller controller;
    //private GUI gui;
    private ArrayList<Project> projects;
    private HomePanel homePanel;
    private NoteTabPane noteTabPane;
    private CardViewerPanel cardViewerPanel;
    private PointPanel pointPanel;
    private StudyPanel studyPanel;
    
    public static final int NUM_TABS = 4;
    public static final int NUM_TABS_NO_PROJECT = 2;
    
    public TabPane(ArrayList<Project> projects,Controller controller,GUI gui){
        this.controller = controller;
        this.projects = projects;
        //this.gui = gui;
        
        //create tab windows
        studyPanel = new StudyPanel(this,gui,controller);
        homePanel = new HomePanel(this,studyPanel,gui,controller);    
        cardViewerPanel = new CardViewerPanel(controller,gui.getFrame());        
        noteTabPane = new NoteTabPane(this,gui,controller);
        pointPanel = new PointPanel(this,gui,controller);
    }
    
    public void createPanel(){
        //create panels
        //cardCreator.createPanel();
        studyPanel.createPanel();
        
        addTabs();
        
        setPreferredSize(new Dimension(390, 400));
    }
    
    private void addTabs(){
        //spaces put distance between words and images
        addTab("Home ", GUI.createImageIcon("home.png"),homePanel,"Project home");
        //addTab("Study",GUI.createImageIcon("lightbulb.png"),studyPanel,"Study from your flashcards");
        addTab("Card Manager ",GUI.createImageIcon("cards.png"),cardViewerPanel,"View, edit, and delete flashcards");
        addTab("Notes ",GUI.createImageIcon("notes.png"),noteTabPane,"Read and write notes");       
        addTab("Vault ",GUI.createImageIcon("coins.png"),pointPanel,"Keep track of your coins");
    }
    
    
    public void setActiveTab(int tab){
        this.setSelectedIndex(tab);
    }
    
    public void refresh(){
        int tabs = this.getTabCount();
        if(controller.getActiveProject() == null){
            //someone just deleted the last active project
            removeAll();
            //replace with a tab telling them to make a new project
            addTab("Home ", GUI.createImageIcon("home.png"),Utils.createAdvicePanel(
                    "<html><center>You have no projects.<br>Create a new project by clicking<br>"
                    + "<b>Add a project</b> in the sidebar."),
                    "Cabra home");
            //points tab still works
            addTab("Points ",GUI.createImageIcon("coins.png"),pointPanel,"Keep track of your progress");
            //refresh only points panel
            pointPanel.refresh();
            return;
        }
        else if(controller.getActiveProject() != null && tabs == NUM_TABS_NO_PROJECT){
            //a project was added when there was none earlier
            //replace home tab with the main tabs. Points tab will have to go too
            removeAll();
            addTabs();
        }
        //studyPanel.refresh();
        homePanel.refresh();
        cardViewerPanel.refresh();
        noteTabPane.refresh();
        pointPanel.refresh();
    }
  
    public void refreshHomePage(){
        homePanel.refresh();
    }
    
    // The panels that are contained in this tab pane call these methods 
    public Project getActiveProject(){
        return controller.getActiveProject();
    }
    
    /**Called when a new project is set as active.
     * 
     * @param project the new active project.
     */
    public void newActiveProject(Project project){
        //studyPanel.newActiveProject(project);
        
    }
    
    /** Tells the controller to do its thing with the note, and returns the created note panel
     * 
     * @param note the note
     * @return the created note panel
     */
    public NotePanel addNote(Note note){
        return controller.addNoteToActiveProject(noteTabPane, note);
    }
    
    /** Removes the given note from the project. Be sure to remove its tab too.
     * 
     * @param note the note to remove
     */
    public void removeNote(Note note){
        controller.getActiveProject().removeNote(note);
    }
    
    /** Call this when letting the user choose when to save notes.
     * 
     */
    
    public void saveNotes(){
        controller.getActiveProject().saveNotes();
    }
    
    /** Call this when changing to a new project.
     * 
     */
    
    public void updateNotes(){
        //tell the note tab pane to update its notes
        noteTabPane.updateNotes();
    }
    
    /** Deletes the given card from the project list
     * 
     * @param card the card to remove
     */
    
    public void removeCard(Card card){
        controller.getActiveProject().removeCard(card);
    }
}

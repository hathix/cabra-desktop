/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cabra;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import static cabra.ImageManager.*;

/**
 * A project's panel. This is found in the ProjectListPanel to the left. Each project gets one of these.
 * @author Neel
 */
public class ProjectPanel extends JPanel{
    private Project project;
    private ProjectListPanel listPanel;
    private JLabel projectName;
    
    /**
     * Num clicks in a double-click.
     */
    private static final int DOUBLE_CLICK = 2;
    
    public ProjectPanel(Project project, ProjectListPanel listPanel){
        this.project = project;
        this.listPanel = listPanel;
        
        //make bg transparent
        setBackground(ColorManager.translucent(Color.white, 0));
        
        buildPanel();
        
        //tool tip contains peek at project - # cards and notes
        int cards = project.numCards();
        int notes = project.numNotes();
        //create label for notes/cards... take plural into accound
        String cardText = Utils.stringWithPlural("card", cards);
        String noteText = Utils.stringWithPlural("note", notes);
        String toolTipText = cards + " " + cardText + ", " + notes + " " + noteText;
        setToolTipText(toolTipText);
    }
    
    /**
     * Returns whether this panel's project is the same as the given project.
     * @param other the project to compare against
     * @return true if they're the same, false otherwise
     */
    public boolean projectEquals(Project other){
        return this.project.equals(other);
    }
    
    /**
     * Updates the project name label to match the actual project name.
     */
    public void refresh(){
        projectName.setText(project.getName());
    }
    
    /**
     * Builds the panel, which will include project name and buttons with ops.
     */
    private void buildPanel(){
        this.setLayout(new FlowLayout(FlowLayout.LEADING));
        
        //text (project name)
        projectName = new JLabel(project.getName());
        projectName.setFont(FontManager.PREFERRED_FONT);
        add(projectName);
        
        //add listener to bring up popup menu
        this.addMouseListener(new MenuListener());
    }
    
    /**
     * Shows a menu that "pops up" on top of this panel.
     * It contains items that let the user edit the project, delete it, etc. 
     * Only call this when 1) the project has been clicked and 2) the click was supposed to trigger a popup menu (right-click).
     * @param event the MouseEvent created when the click was fired. The Listener should provide this.
     */
    private void showPopupMenu(MouseEvent event){
        //build menu
        JPopupMenu menu = new JPopupMenu(this.project.getName()); //some OSes may show a title on top of the menu; use the project name
        
        //create menu items
        JMenuItem item;
        //activate
        item = new JMenuItem("Activate project", createImageIcon("star.png"));
        item.addActionListener(new ActivateListener());
        menu.add(item);
        //rename
        item = new JMenuItem("Rename project", createImageIcon("pencil.png"));
        item.addActionListener(new RenameListener());
        menu.add(item);
        //export
        item = new JMenuItem("Export project", createImageIcon("export.png"));
        item.addActionListener(new ExportListener());
        menu.add(item);        
        //delete
        item = new JMenuItem("Delete project", createImageIcon("trash.png"));
        item.addActionListener(new DeleteListener());
        menu.add(item);
        
        //show it
        menu.show(this, event.getX(), event.getY());
    }
    
    /**
     * Brings up the popup menu when this panel is right-clicked (or some other action, based on the OS.)
     */
    private class MenuListener extends MouseAdapter{
        public void mousePressed(MouseEvent e) {
            //try showing the popup, if the event isn't eligible to show a popup see if they double-clicked
            boolean popupMenuShown = maybeShowPopup(e);
            if(!popupMenuShown){
                if(e.getClickCount() >= DOUBLE_CLICK){
                    //they double-clicked; activate the project (that's the most common action)
                    activate();
                }
            }
        }

        public void mouseReleased(MouseEvent e) {
            maybeShowPopup(e);
        }        
        
        /**
         * Shows the popup menu if the event is supposed to bring up a popup menu - e.g. it's a right-click (for Windows.)
         * @param event the click event
         * @return true if the event triggered a popup menu, false otherwise
         */
        public boolean maybeShowPopup(MouseEvent event){
            if(event.isPopupTrigger()){
                //yup, it's supposed to trigger a popup menu; show the popup menu
                showPopupMenu(event);
                return true;
            }
            return false;
        }
    }
    
    /**
     * Changes whether or not this project panel is active - that is, its project is active.
     * Client classes should call this.
     * @param isActive true if this panel should be active, false otherwise
     */
    public void setActive(boolean isActive){
        if(isActive){
            //bold the text
            //<TODO> figure out why this doesn't work
            projectName.setFont(FontManager.PREFERRED_FONT.deriveFont(Font.BOLD));
        }
        else{
            //set normal weight text
            projectName.setFont(FontManager.PREFERRED_FONT);
        }
        
        projectName.repaint();
        repaint();
    }
    
    
    /**
     * Opens the dialog to rename this panel's project.
     */    
    private void rename(){
        //tell the project list panel
        if(project != null)
            listPanel.renameProject(project);
    }    
    
    /**
     * Automatically exports this panel's project (an easier way than the way from the top menu bar.)
     */        
    private void export(){
        //tell the project list panel
        if(project != null)
            listPanel.exportProject(project);        
    }
    
    /**
     * Sets this panel's project as active.
     */    
    private void activate(){
        //tell the project list panel
        if(project != null){
            listPanel.activateProject(project, this); 
        }
    }
  
    /**
     * Deletes this panel's project.
     */    
    private void delete(){
        //tell the project list panel
        if(project != null)
            listPanel.deleteProject(project);        
    }
    

    //LISTENERS; these just redirect to the methods
    
    /**
     * Opens the dialog to rename this panel's project.
     */
    private class RenameListener implements ActionListener{
        public void actionPerformed(ActionEvent e){
            rename();
        }
    }
    
    /**
     * Automatically exports this panel's project (an easier way than the way from the top menu bar.)
     */
    private class ExportListener implements ActionListener{
        public void actionPerformed(ActionEvent e){
            export();
        }
    }    
    
    /**
     * Sets this panel's project as active.
     */
    private class ActivateListener implements ActionListener{
        public void actionPerformed(ActionEvent e){
            activate();
        }
    }
    
    /**
     * Deletes this panel's project.
     */
    private class DeleteListener implements ActionListener{
        public void actionPerformed(ActionEvent e){
            delete();
        }
    }
    

}

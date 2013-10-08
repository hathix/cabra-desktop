/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cabra;

/**
 *
 * @author Neel
 */


import cabra.PointEnums.Activity;
import cabra.dinero.VaultManager;
import java.util.ArrayList;
import java.io.*;
import java.util.Calendar;
import java.util.Collections;
import javax.swing.JLabel;

public final class Controller extends Object{
    //communicates with the GUI and object classes to get stuff done
    
    private GUI gui; //the GUI that is used here
    private PointManager pointManager;
    private ArrayList<Project> projects;
    private Project activeProject; //the project that you create cards for, study from, etc.
      
    public static final double CHANCE_TO_GET_LUCKY = 0.05;
    
    public Controller(){
        /* the plan:
         * IF no existing projects:
         *      GET new project and make a project with it
         * ELSE: (existing projects)
         *      LOAD projects:
         *          LOAD cards and give them to project
         *          LOAD notes and give them to project
         * FINALLY:
         *      INITIALIZE GUI
         *      BUILD the GUI using the projects
         *      IF userData exists:
         *          LOAD it
         *      ELSE:
         *          CREATE a new one with defaults
         *      TELL the GUI to adapt to these changes
         * 
         * 
         */
        try{
        /** Is it the first time the program's being booted up?
         * 
         */
        boolean firstRun = false;

        //if there isn't a cabraprojects file, create it
        if(SaveLoad.getProjectFolder().exists() == false){
            SaveLoad.getProjectFolder().mkdir();
            //unless the user deleted their data directory, this means this is the first run
            firstRun = true;
        }
        
        UserData.load();
        //USER DATA IS NOW LOADED; do any init of prefs or such here
        
        //create point manager
        try{
            pointManager = new PointManager();
        }
        catch(NumberFormatException nfe){
            //if there's an exception like this, the wrong data was loaded into the User Data
            //probably done by 0.6.0
            
            //alert user
            Utils.showDialog(null, 
                    "Sorry! Your user data seems to have been corrupted and has been reset.", 
                    "User data corrupted");
            
            //clear all data since something's corrupted
            UserData.makeAllDefault();
            
            //reload points
            pointManager = new PointManager();
        }
       
        //user data is set, so update font
        updatePreferredFont(
                UserData.getPref("FontName"), 
                UserData.getInt("Prefs.FontSize")
                );          
        
        //load projects
        ArrayList<Project> loadedProjects = loadProjectsFromFile(); //these projects are all stocked with cards/notes   
        this.projects = loadedProjects;
        this.gui = new GUI(this,loadedProjects);      

        //lack of projects matters now
        if(projects.isEmpty()){
            //no active project
            setNoActiveProject();
        }
        else{
            //there is an active project
            String projectName = UserData.getString("Project"); //the raw name of the project
            setActiveProject(projectName,false);
        }

        Themes theme = Themes.getThemeByName(UserData.getString("Theme"));
        setTheme(theme);
        
        gui.makeFrameVisible();
        
        //give first run info
        if(firstRun){
            //show advice
            Utils.openURLinDialog("http://www.cabra.hathix.com/cabra/welcome.php", 
                    "Welcome to Cabra!", 
                    gui.getFrame(), true);
            
            //set user data's latest version to this
            UserData.setString("Version", About.VERSION);
            
            //add a default project
            //this.addProject(new Project("My First Project"), true);
        }
        
        //show changelog if this is a new version
        boolean upgrade = UserData.getString("Version").equals(About.VERSION) == false;
        if(!firstRun && upgrade){
            //not first run (then new version doesn't matter)   &&   old version != new version

            //store new version
            String version = About.VERSION;
            UserData.setString("Version", About.VERSION);
            
            //show changelog
            if(About.NIGHTLY){
                //don't bother with showing nightly changelog; docs are rarely written for nightlies
                Utils.showDialog(gui.getFrame(),
                        "<html><center>Thanks for testing Cabra " + version + "!<br>As thanks, here's <b>100</b> points!",
                        "Thanks for upgrading to Cabra " + version + "!"
                        );
            }
            else{
                Utils.openURLinDialog("http://cabra.hathix.com/changelog/" + Utils.sanitizeURL(version) + ".php", 
                        "Thanks for upgrading to Cabra " + version + "!", 
                        gui.getFrame(), true);
            }
            
            //earn points for upgrading
            if(About.PRERELEASE)
                gainPoints(Activity.USE_BETA);
            else
                gainPoints(Activity.USE_NEW_VERSION);
        }
        
        //if you're lucky, you earn some free points; also don't do it on first run and overwhelm them w/dialogs
        if(!firstRun && !upgrade && Utils.pushLuck(CHANCE_TO_GET_LUCKY)){
            //earn points!
            int points = Activity.GET_LUCKY.getPoints();
            Utils.showDialog(gui.getFrame(),
                    "<html><center>"
                    + "I'm feeling generous, so here's <b>" +  points + "</b> free points! Enjoy!",
                    "You got lucky!",
                    "goatgift.png");
            gainPoints(Activity.GET_LUCKY);
        }
        
        //is it time for an upgrade? see how long it's been since the last check
        long lastCheck = Long.parseLong(UserData.getString("LastUpdateCheck"));
        long rightNow = Calendar.getInstance().getTimeInMillis();
        long updateInterval = Utils.daysToMillis(UserData.getIntPref("UpdateInterval"));
        if(rightNow - lastCheck >= updateInterval){
            //time to check for updates, it's been more than the chosen interval since the last one
            Updates.checkForUpdates(gui);
        }
        
        gui.update();
        gui.refresh();
        }
        catch(Exception e){
            //some sort of exception threw off the whole thing
            Utils.debug(e, "Fatal error");
        }
    }
    
    public GUI getGUI(){
        return gui;
    }
    
    /**
     * Returns how many points the user has.
     * @return the amount of points the user has
     */
    public int getPoints(){
        return pointManager.getPoints();
    }
   
    /**
     * The user gains points by doing an activity.
     * @param activity the activity that the user did to gain these points.
     * @param refresh if the GUI should refresh.
     */
    public void gainPoints(PointEnums.Activity activity, boolean refresh){
        pointManager.gainPoints(activity);
        
        //show how many points were earned
        gui.showPointsBadge(activity.getPoints());
        
        if(refresh)
            gui.refresh();
    }
    
    /**
     * The user gains points. The GUI will refresh.
     * @param activity the activity that the user did to gain those points.
     */
    public void gainPoints(PointEnums.Activity activity){
        gainPoints(activity, true);
    }
    
    /**
     * Returns the vault manager used to control buying and display of prizes.
     * @param pointLabel the label that will be used to display the points the user has. Should be pre-made and added to view.
     */
    public VaultManager createVaultManager(JLabel pointLabel){
        return new VaultManager(pointManager, gui, pointLabel);
    }
    
    /**
     * A wrapper around FontManager.updatePreferredFont() that works better.
     * Updates the PREFERRED_FONT to the given parameters. You should only pass one. NOTE: you have to validate the frame after this
     * @param fontName the new font name/family. pass null if you don't want to change it.
     * @param fontSize the new size of the font. pass 0 if you don't want to change it.
     */
    public void updatePreferredFont(String fontName, int fontSize){
        FontManager.updatePreferredFont(fontName, fontSize);
        
        //validate frame so the changes take effect
        if(gui != null)
            gui.update();
    }
    
    /** Finds all the projects that the user has and returns them
     * 
     * @return the user's projects
     */
    private ArrayList<Project> loadProjectsFromFile(){
        //looks for existing project files and, if they're there, creates the projects
        File mainProjectFolder = SaveLoad.getProjectFolder();
        if(!mainProjectFolder.exists()){
            //there is no projects folder, since you're a first-time user
            mainProjectFolder.mkdir();
            //we know there's nothing in the folder so let's leave
            return new ArrayList<Project>();
        }
             
        ArrayList<Project> loadedProjects = new ArrayList<Project>();
        
        for(File projectFolder : mainProjectFolder.listFiles()){
            //projectFolder is a folder that contains a project
            
            if(projectFolder.isFile())
                continue; //that means it's probably UserData... but regardless, don't mess with it
            
            Project project = new Project(projectFolder.getName());
            loadedProjects.add(project);
            
            //give it some cards
            SaveLoad.loadCardsFromProject(project);
            
            //do this regardless of the user's having cards in the project
            //and now give notes to the project
            SaveLoad.loadNotesFromProject(project);
        } //end foreach
        
        return loadedProjects;
    }
    
    
    /** Adds a note to the active project, and while doing that creates the note panel
     * 
     * @param note the note to add to the active project
     * @param tabPane the note tab pane that invokes this method
     * @return the created note panel
     */
    
    public NotePanel addNoteToActiveProject(NoteTabPane tabPane,Note note){
        activeProject.addNote(note);

        //save while we're at it
        activeProject.saveNotes(); //no need to save cards too
        
        return new NotePanel(tabPane,gui,this,note);
    }

    
    public void setTheme(Themes theme){
        Themes.setTheme(theme); //that'll do it all for us
        
        //change and save user data
        UserData.setString("Theme",theme.getName());
        
        
        //update the look
        refresh();        
        gui.repaint(); 
    }
    
    public Project getActiveProject(){
        return activeProject;
    }
    
    public ArrayList<Project> getAllProjects(){
        return projects;
    }
    
    public int getNumberOfProjects(){
        return projects.size();
    }
    
    /** Same as refresh() except it happens in this thread.
     * 
     */
    public void refreshNow(){
        gui.refresh();
    }
    
    public void refresh(){
        //called when the active project is changed or has its cards manipulated
        //helps disable/enable buttons
        //Runnable r = new Runnable(){
        //    public synchronized void run(){
                refreshNow();
        //    }
        //};
        //javax.swing.SwingUtilities.invokeLater(r);
        //new Thread(r).start();
        //gui.refresh();
    }
    
    public void refreshHomePage(){
        Runnable r = new Runnable(){
            public void run(){
                gui.refreshHomePage();
            }
        };
        //javax.swing.SwingUtilities.invokeLater(r);
        new Thread(r).start();
    }
  
    /** Differs from setActive project in that that just changes active project, this handles
     *  user interaction
     * @param projectName the name of the project you wish to be made active
     * @param shouldSave whether or not user data should be saved
     */    
    public void setActiveProject(Project project, boolean shouldSave){
        //if nothing has matched, there's a problem
        if(project == null){
            //set the first project as active, then call this method again
            setActiveProject(projects.get(0),false);
            return;
        }
        
        if(shouldSave){
            //quick! Save the old notes if they weren't saved yet
            if(activeProject != null){
                try{
                    gui.saveAllNotes(); //that should do it                
                }
                catch(NullPointerException n){
                    //error with saving project
                    System.out.println("Error saving project notes!");
                }
            }
        }
        
        activeProject = project;
        //alert all 
        gui.newActiveProject(project);

        //set user data
        UserData.setString("Project",project.getName()); 
        
        //load the notes for the project
       // activeProject.loadNotes();

        //fix the home panel, which shows nothing unless this is done
        refresh();
    }
    
    /** Differs from setActive project in that that just changes active project, this handles
     *  user interaction
     * @param projectName the name of the project you wish to be made active
     * @param shouldSave whether or not user data should be saved
     */
    
    public void setActiveProject(String projectName, boolean shouldSave){
        //find the matching project
        Project project = null;
        for(Project proj : projects){
            //System.out.println(proj.getName());
            if(proj.getName().equals(projectName))
                project = proj;
        }

        setActiveProject(project, shouldSave);
    }
    
    /**Sets nothing as the active project.
     * 
     */
    public void setNoActiveProject(){
        UserData.makeDefault("Project");
        activeProject = null;
        gui.setFrameTitleByProject(null);
        //only refresh the tab pane (that removes all the panels and shows a new one)
        refresh();
    }
    
    /** Creates a new project and adds it
     * 
     * @param projectName the name of the project you want made
     * @param shouldSave true if the userData should be saved, false otherwise
     * @return the created project
     */
    
    public Project addProject(String projectName, boolean shouldSave){
        Project project = new Project(projectName);
        
        File projectFolder = new File(SaveLoad.getProjectFolder().getAbsolutePath() + "/" + project.getName()); //puts the new folder in the projects folder
            projectFolder.mkdir();        
        
        //really all this method does is make a project and tell addProject to do its stuff using the project
        return addProject(project,shouldSave);
    }
    
    private Project addProject(Project project, boolean shouldSave){
        projects.add(project);
        gui.addProject(project); 
        
        //add project data
            //create the card file
            //File cardFile = new File(projectFolder.getPath() + "/cards.txt");
            
        project.saveCards(); //forces the creation of cards.txt
        
        //make this project active
        setActiveProject(project, shouldSave);
        
        //sort project list
        Collections.sort(projects);
        
        return project;       
    }
    
    /**
     * Renames the given project so it has the given name.
     * @param project the project
     * @param newName the project's new name.
     */
    public void renameProject(Project project, String newName){
        project.setName(newName);
        //re-sort projects; the name change may have put project out of order
        Collections.sort(projects);
    }
    
    
    /** Creates a project assuming you have all the files (i.e. you've just imported it.)
     * 
     * @param projectName the name of the project
     * @param projectFolder the path to the project's folder (inside the Project Folder)
     */
    public void createProjectFromExistingFile(String projectName,File projectFolder){
        Project project = new Project(projectName);
        
        //add cards
        SaveLoad.loadCardsFromProject(project);
        
        //add notes
        SaveLoad.loadNotesFromProject(project);
        
        addProject(project,true);
    }
    
    public void removeProject(Project project){
        //removes the project at the given index
        //by the time we get here, we know something will be deleted
        
        //first delete the project file
        //Project project = projects.get(projectIndexInList); //get the project slated for deletion
        int projectIndexInList = projects.indexOf(project); //location of the project in the list
        File projectFile = new File(SaveLoad.getProjectFolder() + "/"+project.getName());
        //before we delete the directory we need to delete files inside
        for(File file : projectFile.listFiles()){
            file.delete();
        }
        //now delete the directory
        projectFile.delete();
     
        //what if the project to be removed was the active one?
        if(project.equals(activeProject)){
            //set the previous active project
            if(projects.size() == 1){
                //the last project was deleted, so nothing's left
                setNoActiveProject(); //takes care of making activeproject = null
            }
            else{
                //there's still a project left
                if(projectIndexInList == 0){
                    setActiveProject(projects.get(1),true);
                }
                else{
                    setActiveProject(projects.get(projectIndexInList-1),true);               
                }    
                refresh();              
            }
        }        
        
        //now remove the project from list
        projects.remove(projectIndexInList);
        
        //update project list panel
    }
    
    public void addCardToActiveProject(Card card){
        //add the card to the project... it'll save itself
        
        if(activeProject != null){
            activeProject.addCard(card);
            
            //gain points
            gainPoints(Activity.CREATE_CARD);
            if(card.hasPicture()){
                gainPoints(Activity.ADD_IMAGE);
            }
                   
            refresh();
        }
        //if there's no active project, take no action
    }

    
}

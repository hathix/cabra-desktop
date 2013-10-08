/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cabra;

/**
 *
 * @author Neel
 */

import java.io.*;
import java.util.ArrayList;

public class SaveLoad extends Object{
    //a class for all your saving/loading/random IO needs.
    
    private SaveLoad(){}
    
    //public static final String DEF_PROJECT_FOLDER = javax.swing.filechooser.FileSystemView.getFileSystemView().getDefaultDirectory().getAbsolutePath() + "/CabraProjects";
    public static final String DEF_PROJECT_FOLDER = "Cabra"; //portable normal
    //public static final String DEF_PROJECT_FOLDER = "Data"; //portableapps
    
    public static File getProjectFolder(){
        return new File(UserData.getPref("ProjectFolder"));
    }
    
    /** If you have a pre-existing project, you can load cards from its cards.txt file.
     * 
     * @param project the project whose cards are to be loaded
     */
    public static void loadCardsFromProject(Project project){
            //give it some cards
            File cardsFile = new File(getProjectFolder().getAbsolutePath()+ "/" + project.getName()+ "/cards.txt");
            
            ArrayList<String> lines = readFromCardFile(cardsFile); //read the file and get the lines in an arraylist
            
            if(lines == null || cardsFile.exists() == false){
                //Cards file doesn't exist or is corrupted! No cards in project
                return;
            }
            if(lines.isEmpty() == false){

                ArrayList<Card> cards = new ArrayList<Card>(); //stores the cards until they're ready to be written

                //build cards
                for(String line : lines){
                    //create a card
                    Card card = Card.createCardBasedOnText(line);
                    if(card == null) continue; //malformed card
                    //add to projects
                    cards.add(card);
                }

                //finally, add cards
                project.addCards(cards);
            }//end if        
    }
    
    /** Reads all the cards from the given file (must be cards.txt) and puts it in an arraylist
     * 
     * @param cardFile the cards.txt file containing the cards
     * @return the arraylist containing all the cards
     */
    public static ArrayList<String> readFromCardFile(File cardFile){
        try{
            BufferedReader reader = new BufferedReader(new FileReader(cardFile));
            ArrayList<String> lines = new ArrayList<String>();
            String line = null;
            while((line = reader.readLine()) != null){
                lines.add(line);
            }
            reader.close();
            return lines;
        }
        catch(IOException io){
            System.out.println("Error reading cards from file! Details: " + io);
            return null;
        }
    }    
    
    /**Serializes all the notes of a project
     * 
     * @param project the projects whose notes need to be serialized
     */
    
    public static void saveNotes(Project project){
        //some notes might have been renamed from last time; delete those
        File projectFolder = new File(SaveLoad.getProjectFolder() + "/" + project.getName()); //folder where notes are
        //just wipe out all the old notes
        //TODO: only remove notes you need to
        /*for(File file : projectFolder.listFiles()){
            if(Utils.getExtension(file).equals(Note.EXTENSION)){
                //it's a note file
                System.out.println(file.delete());
            }
        }*/
        
        //now put the notes back
        try{
            for(Note note : project.getNotes()){
                //the file we're going to use
                //String sanitizedNoteName = SaveLoad.removeSpaces(note.getName()); //remove spaces in the note name first
                String sanitizedNoteName = note.getName(); //might call removeSpaces, might not
                File saveTo = new File(projectFolder + "/" + sanitizedNoteName + "." + Note.EXTENSION); //all the notes go in the main project folder
                
                ObjectOutputStream noteWriter = new ObjectOutputStream(new FileOutputStream(saveTo));
                noteWriter.writeObject(note);
                noteWriter.close();
            }
        }
        catch(FileNotFoundException f){
            //the file isn't there... that probably means the active project just got deleted but we're trying to save its notes
            
        }
        catch(IOException io){
            System.out.println("Error saving notes! Details: " + io);
            io.printStackTrace();
        }
    }
    
    
    /** A convenience method for loading a project object with the notes it has saved.
     * 
     * @param project the project whose notes are to be loaded.
     */
    public static void loadNotesFromProject(Project project){
        for(Note note : SaveLoad.getNotesFromProject(project)){
                    project.addNote(note);
                }
    }
    
    /**Gets all the notes from the given project
     * 
     * @param project the project whose notes you want to find
     * @return the notes
     */
    public static ArrayList<Note> getNotesFromProject(Project project){
        //we'll be putting notes in this
        ArrayList<Note> notes = new ArrayList<Note>();
        
        try{
            //get the files
            File projectFile = new File(SaveLoad.getProjectFolder() + "/" + project.getName());
            File[] possibleFiles = projectFile.listFiles();
            for(File file : possibleFiles){
                if(file.getName().indexOf("." + Note.EXTENSION) == -1)
                    continue; //anything without .cnote can't be a note
                ObjectInputStream noteReader = new ObjectInputStream(new FileInputStream(file));
                //read the note
                
                Note note;
                
                try{
                    note = (Note)noteReader.readObject();
                }
                catch(Exception e){
                    //incompatible kind of note (from an old version of Cabra)
                    System.out.println("Error reading notes!");
                    e.printStackTrace();
                    note = null;
                    file.deleteOnExit();
                    /*Utils.showDialog(null, 
                            "Sorry, there's been an error reading the note " + file.getName() + "!",
                            "Error reading notes!");*/
                }
                finally{
                    noteReader.close();
                }
                
                //add the note to the note list
                if(note != null)
                    notes.add(note); //if note is null, there was an error reading it, so don't load it
                //try for the other files
            } //end foreach
        }
        catch(IOException io){
            System.out.println("Error loading notes!");
            io.printStackTrace();
            return null;
        }  
        
        return notes;
    }
}

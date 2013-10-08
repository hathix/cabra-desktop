/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cabra;

/**
 *
 * @author Neel
 */

import java.util.ArrayList;
import java.io.*;
import javax.swing.*;

public class Project implements Comparable<Project>{
    private Session session = null; //if a study session is going on, it's here
    private Deck deck; //all the cards in this project are here
    private String name; //the name of this project, like "History Test"
    //private Card currentCard; //just for tracking, the current card you're on
    private ArrayList<Note> notes; //each project has its own notes
    
    public Project(String name){
        //name is something like "History Test"
        this.name = name;
        deck = new Deck();
        notes = new ArrayList<Note>();
    }
    
    /* SESSION STUFF */
    public void setSession(Session session){
        this.session = session;
    }
    
    /** Creates a new session for this project.
     * 
     */
    public void newSession(){
        do{
            setSession(new Session(this));
        }
        while(getSession().isEmpty());
    }
    
    public Session getSession(){
        return this.session;
    }
    
    /* NOTE STUFF */
    
    /**Add an existing note to this project
     * 
     * @param note the note to add
     */
    public void addNote(Note note){
        notes.add(note);
    }
    
    /** Takes the given note out of the notes list.
     * 
     * @param note the note to remove
     */
    public void removeNote(Note note){
        notes.remove(note);
        
        //delete the file
        
        File noteFile = new File(SaveLoad.getProjectFolder() + "/" + name + "/" + note.getName() + "." + Note.EXTENSION);
            //get the note's file name

        //deletes the file
        noteFile.delete();
    }
    
    /***
     * Returns this project's notes.
     * @return the notes, in ArrayList form
     */
    public ArrayList<Note> getNotes(){
        return notes;
    }
    
    /**
     * Returns how many notes there are.
     * @return the number of notes
     */
    public int numNotes(){
        return notes.size();
    }
    
    /* CARD STUFF */
    
    public void addCard(Card card,Status status){
        card.setStatus(status);
        deck.add(card);
        
        //if the card has a picture, move the picture over here
        if(card.hasPicture()){
            /*if(!new File(this.getPathTo(card.getPictureName())).exists()){
                //the picture file is corrupted or doesn't exist... remove it
                card.removePicture();
                
                return;
            }*/
            
            File copiedFile = copyPictureFile(card.getPictureFile());              
            
            //resize the image so it's the same size as the studying picture panel; reduces file size
            /** DISABLED so we can see image in full size some time **/
            /*ImageManager.saveImage(ImageManager.scaleImage(
                    GUI.createImageIconFromFullPath(copiedFile.getAbsolutePath()), 
                    PicturePanel.PICTURE_WIDTH,
                    PicturePanel.PICTURE_HEIGHT),
                    copiedFile);*/
                 
            card.setPictureName(copiedFile.getAbsolutePath());
            //and now trim the card's picture file... we won't need the full path any more
            card.trimPictureFile();
        }        
        
        saveCards();
        
        //since there's a new card, notify the session
        if(session != null){
            session.update();
        }
    }
    
    public void addCard(Card card){
        //we don't know if it's important or not
        //however, the card knows if it's important or not... let's ask
        addCard(card,card.getStatus()); //ask the card if it's importnat
    }
    
    public void addCards(ArrayList<Card> givenCards){
        //called during initialization to create cards
        //significantly reduces overhead by only saving at end
        for(Card card : givenCards){ //go through each card
            deck.add(card);
        }
        //now that the cards have been added, shuffle and save
        shuffle();
        
        //session = new Session(this);
    }
    
    /** Removes the given card from the card array... that's it. Well, it also saves.
     * 
     * @param cardToRemove the card to get rid of
     */
    
    public void removeCard(Card cardToRemove){
        deck.remove(cardToRemove);
        //if the card being removed was active (it probably was), set active card to null
        if(cardToRemove.equals(deck.getCurrentCard())){
            deck.makeCurrentCardNull();
        }
        //delete the card's picture, if it has one
        if(cardToRemove.hasPicture()){
            String path = getPathTo(cardToRemove.getPictureName()); //finds the full path to the image
            File fileToRemove = new File(path);
            //delete the file
            fileToRemove.delete();
        }
        //save
        saveCards();
    }
    
    /** Copies the given picture file to this guy's folder.
     * 
     * @param pictureFile the picture file to be copied
     * @return the new location of the file
     */
    
    public File copyPictureFile(File pictureFile){
        String fileName = pictureFile.getName();
        File newFile = new File(SaveLoad.getProjectFolder() + "/" + name + "/" + fileName);
        ImageManager.copyImage(pictureFile,newFile);
        return newFile;
    }
    
    /** Retrieves an imageicon that is stored in this project's folder
     * 
     * @param imageName the name of the icon (foo.png)
     * @return the created imageicon, or null if the image cannot be found
     */
    
    public ImageIcon getImageIcon(String imageName){
        return GUI.createImageIconFromFullPath(getPathTo(imageName));   
    }
    
    /** Finds the absolute location of a card/note based on its name
     * 
     * @param thing the name of the card/note/picture's file, like foo.png
     * @return the full path to foo.png
     */
    
    public String getPathTo(String thing){
        String folderPath = SaveLoad.getProjectFolder().getAbsolutePath() + "/" + this.getName(); //to the folder of the image
        String absolutePath = folderPath + "/" + thing; //the absolute path to the image        
        return absolutePath;
    }
    
    public void save(){
        //called when this project needs to be saved

        //the methods are split up for convenience
        saveCards();

        saveNotes();
    }
    
    public void saveCards(){
        //save all the cards
        //new Thread(new Runnable(){
        //    public synchronized void run(){
              try{
                    BufferedWriter writer = new BufferedWriter(new FileWriter(new File(SaveLoad.getProjectFolder().getAbsolutePath() + "/" + name + "/cards.txt"))); //write to my card file
                    for(Card card : deck.getCards()){
                        //write down each card
                        writer.write(card.toString()); //card's toString() does that question/answer thing
                        writer.newLine();
                    }
                    writer.close();
                }
                catch(IOException io){
                    System.out.println("Couldn't save cards!");
               }     
         //   }
        //}).start();
    }
    
    public void saveNotes(){
        //save notes
        final Project proj = this;
        //save in background
        new Thread(new Runnable(){
            public void run(){
                SaveLoad.saveNotes(proj);
            }
        }).start();
        SaveLoad.saveNotes(this);
    }
    
    /** Tells this project to load notes from the saved files. Call this when switching to a new active project
     * 
     */
    
    public void loadNotes(){
        notes = SaveLoad.getNotesFromProject(this);
    }
    
    public void shuffle(){
        deck.shuffle();
        saveCards();
    }
    
    public String getName(){
        return name;
    }
    
    public void setName(String newName){
        File folder = getFolder(); //folder of this project with old name
        
        this.name = newName;

        //rename project's folder
        folder.renameTo(new File(SaveLoad.getProjectFolder() + "/" + newName));
    }
    
    /***
     * Prints out this project's cards. The user earns some points by doing this.
     * @param controller the controller. Used to gain points.
     */
    public void print(Controller controller){
        Printer.print(this, getCards());   

       //earn the points
       controller.gainPoints(PointEnums.Activity.PRINT_CARDS);
    }
    
    /** Resets all cards in this deck to not studied
     * 
     */
    public void resetAllCards(){
        for(Card card : deck.getCards()){
            card.setStatus(Status.DEFAULT_STATUS);
        }
        saveCards();
    }
    
    /** The entire session was skipped.
     * 
     */
    public void skipAll(){
        for(Card card : getCards()){
            card.skip();
        }
    }
    
    public ArrayList<Card> getCards(){
        return deck.getCards();
    }
    
    /***
     * Returns true if and only if there are 0 cards in the project.
     * @return true if there are 0 cards, false otherwise
     */
    public boolean isEmpty(){
        return numCards() == 0;
    }
    
    public int numCards(){
        //returns the number of cards in the card list
        return deck.numCards();
    }
    
    /** Returns, for example, how many not studied cards there are.
     * 
     * @param status the status to check for (learned, not learned, not studied)
     * @return the number of matching cards
     */
    public int numMatchingCards(Status status){
        return deck.numMatchingCards(status);
    }
    
    /** Returns the statuses of the cards: [A,B,C,D,E]
     * 
     * @return [cards with status A, cards with B, C, D, E]
     */
    public int[] cardStatuses(){
        return new int[]{
            numMatchingCards(Status.A),
            numMatchingCards(Status.B),
            numMatchingCards(Status.C),
            numMatchingCards(Status.D),
            numMatchingCards(Status.E)
        };
    }
    
    public Card nextCard(){
        return deck.getCard();
    }
    
    public File getFolder(){
        return new File(SaveLoad.getProjectFolder().getAbsolutePath() + "/" + name);
    }
    
    public Card getCurrentCard(){
        return deck.getCurrentCard();
    }
    
    public int getCurrentIndex(){
        return deck.getCurrentIndex();
    }
    
    @Override
            public String toString(){
                //like "History Test"
                return name; //this guy's toString is just his name
            }
    @Override
            public boolean equals(Object aProject){
                if(aProject == null) return false;
                if(aProject instanceof Project == false)return false;
                try{
                    Project project = (Project)aProject;
                    //if(project == null) return false;
                    return project.name.equals(this.name);//compare by name, i.e. "History Test"                    
                }
                catch(Exception e){
                    return false;
                }
            }
    
    /**
     * Compares the two projects based on name, case insensitive. "ABC" > "XYZ".
     * @param other
     * @return +ve if this project is bigger than other, -ve if it's smaller, 0 if they are equal (names are the same)
     */
    public int compareTo(Project other){
        if(this.equals(other)) return 0;
        String thisname = this.name.toLowerCase();
        String othername = other.name.toLowerCase();
        return thisname.compareTo(othername);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 19 * hash + (this.name != null ? this.name.hashCode() : 0);
        return hash;
    }
    
    
    public static void createSampleProject(Controller controller){
        Project project = controller.addProject("Sample", true);
        ArrayList<Card> cards = new ArrayList<Card>();
        cards.add(new Card(
                "What is the ultimate answer to life, the universe, and everything?", 
                "42"));
        project.addCards(cards);
        
        //return project;
        controller.refresh();
    }
}

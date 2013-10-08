/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cabra;

import java.io.*;
import javax.swing.text.*;

/** A note is a document of sorts where you can jot down notes and such
 *
 * @author Neel
 */
public class Note extends Object implements Serializable{
    
    //static final long serialVersionUID = 836684734869324501L; //used to ensure backwards compatibility
    public static final String EXTENSION = "cnote";
    
    //private String document; //all the document in a note is stored here
    private StyledDocument document; //the rtf-fortmatted document of the note
    private String name; //the name of the note, like "Michelangelo Lecture"
    
    public Note(String name){
        this.name = name;
        
        this.document = new DefaultStyledDocument();
    }
    
    public Note(StyledDocument document,String name){
        //called when you know what you want the document to be
        this(name); //in case something needs to be done
        this.document = document;
    }
    
    /** Returns the name of this note, i.e. "Michelangelo Lecture"
     * 
     * @return 
     */
    public String getName() { return name; }
    
    /*public void rename(String newName){
        this.name = newName;
    }*/
    
    /** Returns the contents of this note, i.e. "Four score and seven years ago..."
     * 
     * @return 
     */
    public StyledDocument getDocument() { return document; }
    
    public void setDocument(StyledDocument document){
        this.document = document;
    }
    
    /** Determines whether or not this note's document matches the given string
     * 
     * @param string the document you want to test
     * @return true if the document matches, false if not
     */
    
    public boolean documentEquals(StyledDocument doc){
        return document.equals(doc);
    }
    
    @Override
            public boolean equals(Object aNote){
                if(aNote == null) return false;
                if(aNote instanceof Note == false) return false;
                Note note = (Note)aNote;
                return this.name.equals(note.name); //base equality off of note name
            }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 89 * hash + (this.name != null ? this.name.hashCode() : 0);
        return hash;
    }
}

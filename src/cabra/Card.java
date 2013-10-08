/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cabra;

//import javax.swing.*;
import java.io.*;

/**
 *
 * @author Neel
 */
public class Card extends Object{
    //a simple quiz card with a question and answer
    
    private String questionText;
    private String answerText;
    private String pictureName; //this card might have a picture
    private Status status; //rank
    private int sessionsLeft; //sessions until next study
    
    public static final String NO_PICTURE_STRING = " "; //not really empty, just represents no picture
    public static final String DELIMITER = "//"; //separates fields
    public static final String NEWLINE_REPLACER = "-nl-"; //\n's are replaced with this string during saving and loading
    public static final String NEWLINE = "\n"; //signifies a new line in card text
    
    
    //the ultimate one
    //the rest cascade under this, overloading to the one above it
    public Card(Status status, int sessionsLeft, String question, String answer, String pictureName){
        setStatus(status);
        this.sessionsLeft = sessionsLeft;
        
        this.questionText = question;
        this.answerText = answer;
        this.pictureName = pictureName;
    }  
    
    public Card(Status status,String question,String answer,String pictureName){
        this(status,status.getReps(),question,answer,pictureName);
    }
    
    public Card(String question,String answer,String pictureName){
        this(Status.DEFAULT_STATUS,question,answer,pictureName);
    }
    
    public Card(String question,String answer){
        this(question,answer,NO_PICTURE_STRING);
    }
    
    
    public void trimPictureFile(){
        //right now we have to full path to the picture... get just the name "foo.png"
        File picture = new File(pictureName);
        this.pictureName = picture.getName();        
    }
    
    /** Tells if this card has a picture
     * 
     * @return true if it has a picture, false otherwise
     */
    public boolean hasPicture(){
        return !pictureName.equals(NO_PICTURE_STRING);
    }

    
    /** Status stuff */
    public void setStatus(Status status){
        //System.out.println("Setting status: " + status.toString());
        this.status = status;
        this.sessionsLeft = status.getReps();
        //System.out.println(status.toString() + sessionsLeft);
    }
    
    public Status getStatus(){
        return this.status;
    }
    
    public int sessionsLeft(){
        return sessionsLeft;
    }
    
    public boolean isDueForStudying(){
        return sessionsLeft <= 0;
    }
    
    /** This card is studied.
     * 
     * @param result Choices.YES if it was known, Choices.NO if it wasn't
     */
    public void study(KnowPanel.Choices result){
        switch(result){
            case YES:
                //send rank up
                setStatus(status.nextRank());
                break;
            case NO:
                //send it back to bottom
                setStatus(Status.A);
                break;
            case SORT_OF:
                //send rank down 1
                setStatus(status.previousRank());
                break;
            case SKIPPED:
                break;
        }
    }
    
    /** This card isn't studied this round
     * 
     */
    public void skip(){
        if(sessionsLeft > 0)
            sessionsLeft--;
    }

    public String getQuestion(){
        //replace newlines
        String text = bringBackNewlines(questionText);
        //return question
        return text;
    }
    public String getAnswer(){
        return bringBackNewlines(answerText); //replace the newline replacer with the actual \n character
    }
    
    public void setQuestion(String text){
        if(text != null && text.equals("")==false)
            this.questionText = text;
    }
    
    public void setAnswer(String text){
        if(text != null && text.equals("")==false)
            this.answerText = text; 
    }
    
    public String getPictureName(){
        return pictureName;
    }
    
    public File getPictureFile(){
        return new File(pictureName);
    }
    
    /** Changes the name (path) of the picture.
     * 
     * @param name the path (absolute or relative) of the picture file
     */
    public void setPictureName(String name){
       this.pictureName = name;
    }
    
    /** Removes the picture from this card.
     * 
     */
    public void removePicture(){
        setPictureName(Card.NO_PICTURE_STRING);
    }
    
    /**Replaces the newline replacers in the given text with real newlines
     * 
     * @param string the string to be fixed
     * @return the fixed string
     */
    
    public static String bringBackNewlines(String string){
        //escape all characters
        //String literal = Matcher.quoteReplacement(string);
        String fixed = string.replaceAll(Card.NEWLINE_REPLACER, Card.NEWLINE);
        return fixed;
    }
    
    /** Replaces the newlines in a string of text with the newline replacer.
     * 
     * @param string the string to be messed with
     * @return the new string
     */
    
    public static String replaceNewlines(String string){
            //get a literal interpretation of the string
            //String literal = Matcher.quoteReplacement(string);
            //now replace stuff
            string = string.replaceAll(Card.NEWLINE, Card.NEWLINE_REPLACER);
            return string;
    }
    
    /** Creates a card based on the raw data string passed.
     * 
     * @param text the raw text
     * @return the created card
     */
    public static Card createCardBasedOnText(String text){
        try{
            //create a card for each line here
            String[] stuff = text.split(Card.DELIMITER);
            //first string is status, next string is question, then answer, then image
            
            //cards made in older versions need to be slightly adapted
            String fixedFirst = Status.importFromPast(stuff[0]);

            Card card = new Card(
            Status.getStatus(fixedFirst.substring(0,1)),
            Integer.parseInt(fixedFirst.substring(1,fixedFirst.length())), //grab however many digits
            stuff[1],
            stuff[2],
            stuff[3]);
            
            return card;
        }
        catch(Exception e){
            //a malformed line, maybe?
            System.out.println("Malformed card! Details:" + e);
            return null;
        }
    }
    
    @Override
    public String toString(){
        //used during saving of this card
        String text = status.toString() + sessionsLeft + Card.DELIMITER
                    + questionText + Card.DELIMITER
                    + answerText + Card.DELIMITER
                    + pictureName;
            //replace newlines with the newline replacer
            text = replaceNewlines(text);
            //now that it's cleaned up return it
            return text;
    }
    
    @Override
            public boolean equals(Object aCard){
                if(aCard == null) return false;
                if(aCard instanceof Card == false)return false; //if it's not a card, stop it
                    Card card = (Card)aCard;
                    if(card == null) return false;
                    return card.answerText.equals(this.answerText) && card.questionText.equals(this.questionText)
                            && card.pictureName.equals(this.pictureName)
                            && card.status == this.status;
                    //everything must match
                
            }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 43 * hash + (this.questionText != null ? this.questionText.hashCode() : 0);
        hash = 43 * hash + (this.answerText != null ? this.answerText.hashCode() : 0);
        hash = 43 * hash + (this.pictureName != null ? this.pictureName.hashCode() : 0);
        hash = 43 * hash + (this.status != null ? this.status.hashCode() : 0);
        return hash;
    }
}

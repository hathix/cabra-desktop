/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cabra;

import java.awt.Color;
import javax.swing.ImageIcon;

/** A card's rank (A to E based on how well you know it) as well as how many sessions are left until it's due
 *
 * @author Neel
 */
public enum Status {
    
    //number is default reps
    A('A',0,"FF0000","These cards are new, so you'll study them the most."), 
        //red 
    B('B',2,"FF7F00","You know these cards just a little, so you'll study them a lot."), 
        //orange
    C('C',4,"FFC800","You're in the process of learning these cards, so you'll study these occasionally."), 
        //gold
    D('D',8,"0094FF","You know these cards very well, so you won't study them often."), 
        //blue
    E('E',12,"00E500","You know these cards cold, so you'll study them rarely.")  
        //green
    ;
    
    /* constants */
    /** Original status of all cards.
     * 
     */
    public static final Status DEFAULT_STATUS = Status.A;
    
    /* class stuff */
    
    /** Name of rank (A,B,C)
     * 
     */
    private char rank;
    
    /* The number of sessions left until this card is studied. If it's 0, the card will be studied immediately.
     * Otherwise it'll be reduced by one until it hits 0.
     * 
     */
    private int sessionsLeft;
    
    /** The color of this status's bar graph and other stuff
     * 
     */
    private Color color;
    
    /**
     * Text shown in a tool tip when the bar graph for this is moused over.
     */
    private String toolTipText;
    
    public int getReps(){
        return sessionsLeft;
    }
    
    public Color getColor(){
        return color;
    }
    
    public String getToolTipText(){
        return toolTipText;
    }
    
    public ImageIcon getImageIcon(){
        return GUI.createImageIcon(name() + ".png");
    }
    
    @Override
    public String toString(){
        String string = new String(new char[]{ rank });
        return string;
    }    
    
    /** Returns the rank after this one. If rank is A, it returns B.
     * 
     * @return the rank after this one.
     */
    public Status nextRank(){
        //determine it based on this rank
        switch(rank){
            case 'A':
                return Status.B;
            case 'B':
                return Status.C;
            case 'C':
                return Status.D;
            case 'D':
                return Status.E;
            case 'E':
                return Status.E; //can't go any higher
            default:
                return Status.DEFAULT_STATUS; //shouldn't happen, it's just here to please compiler
        }
    }

   /** Returns the rank before this one. If this rank is C, this method returns B.
     * 
     * @return the rank before this one.
     */
    public Status previousRank(){
        switch(rank){
            case 'A':
                return Status.A; //can't go any lower
            case 'B':
                return Status.A;
            case 'C':
                return Status.B;
            case 'D':
                return Status.C;
            case 'E':
                return Status.D;
            default:
                return Status.DEFAULT_STATUS; //shouldn't happen, it's just here to please compiler
        }        
    }
    
    /** A convenient overload that lets you pass the hex code of the color and not the color itself
     * 
     * @param rank the letter to display for the rank (A-E)
     * @param defaultReps how many study sessions elapse between studying.
     * @param hexCode the hex code of the color, like FF0000
     * @param toolTipText the text displayed on the bar graph's tooltip
     */
    Status(char rank, int defaultReps, String hexCode, String toolTipText){
        this(rank,defaultReps,ColorManager.createColor(hexCode),toolTipText);
    }
    
    Status(char rank, int defaultReps, Color color, String toolTipText){
        this.rank = rank;
        this.sessionsLeft = defaultReps;
        this.color = color;
        this.toolTipText = toolTipText;
    }    
    
    /** Tries to find the status with the given name.
     * 
     * @param statusName A0,E3,B1... first rank, then number of reps left
     * @return the status, or the default if nothing is found
     */
    public static Status getStatus(String statusName){
        try{
            Status status = Status.valueOf(statusName);
            
            return status;
        }
        catch(IllegalArgumentException e){
            //not a valid status
            //importing statuses from 0.4.x; learned is B, not learned/not studied is A
            if(statusName.equals("learned"))
                return Status.B;
            else
                return Status.A;
        }
    }
    
    /** Takes a status string (new or from 0.4.x) and changes it to the new version.
     * 
     * @param past the status string from the text file.
     * @return the new status string, like "A0" or "C2"
     */
    public static String importFromPast(String past){
        if(past.equals("learned")){
            //c status
            return "C" + Status.C.sessionsLeft;
        }
        else if(past.equals("not_learned")){
            //a status
            return "A" + Status.A.sessionsLeft;
        }
        else if(past.equals("not_studied")){
            //a status
            return "A" + Status.A.sessionsLeft;
        }
        else{
            //it's new, so no need to adapt it
            return past;
        }
    }
}

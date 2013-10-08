/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cabra;

import java.util.ArrayList;

/**A studying session. Allows for easier interfacing with the project.
 *
 * @author Neel
 */
public class Session extends Object{
    
    private Project project; //the project we're studying for
            
    private int currentIndex = 0; //how many cards we have studied
    private ArrayList<Card> cards; //cards we'll study
    private Card currentCard; //current card we're studying
    private int numLearned = 0;
    private int numNotLearned = 0;
    private int numSortOf = 0;
    private int numSkipped = 0;
    
    public Session(Project project){
        //new Exception().printStackTrace();
        this.project = project;
        
        project.setSession(this);

        cards = new ArrayList<Card>();
        setupSession(project.getCards());
    }
    
    /** Sets up the session; creates the list of cards that will be studied.
     * 
     * @param allCards 
     */
    private void setupSession(ArrayList<Card> allCards){
        //determine the maximum number of cards the user wants to study in a session
        //and use that as a limit
        int maxCards = UserData.getIntPref("MaxSession");
        int added = 0;
        for(int i=0; i<allCards.size() && added < maxCards; i++){
            Card card = allCards.get(i);
            if(card.isDueForStudying()){
                cards.add(card);
                added++;
            }
            else{
                //we won't study it
                card.skip();
            }            
        }
    }
    
    /**Update the numbers to match the current project's new numbers (so it's called when a card is added to the active project)
     * 
     * @return true if the session has ended, false otherwise
     */
    public boolean update(){
        //has the session ended?
        return this.getNumCards() == 0;
    }
        
     /*   //since totalCards was hard-coded, change it to reflect any new cards or something
        int oldCards = totalCards;
        totalCards = getAcceptedCards().size();
        
        if(oldCards != totalCards){
            //a card was just added, but that doesn't really matter
            //as long as totalCards in incremented we're happy (the card is added on the end of deck)
        }
        return totalCards == 0; //session ends if no cards are left
    }*/
    
    /**Signals this session that it's been ended, normally or prematurely.
     * 
     */
    public void end(){
        //if the session was ended prematurely, then the number of total cards won't equal the number of learned/notlearned/notstudied
        
        //if the user quit early, don't let the cards you didn't study show up as skipped
        //numCards = numLearned + numNotLearned + numSkipped;
        
        //totalCards = numLearned + numNotLearned + numSkipped;
        project.setSession(null);
    }
    
    /**Gets a card based on the filters and project.
     * 
     * @return the chosen card
     * @return null if the session has ended
     */
    public Card getCard(){
        if(currentIndex >= numCards()){
            //this session should be over
            return null;
        }
        //just grab a card that's been chosen
        currentCard = cards.get(currentIndex);
        currentIndex++; //this could go over, that's ok since the session should always be checked after each card
        
        return currentCard;
    }
    
    /**Doesn't do anything, this guy just wants to know when something happens.
     * 
     * @param card the card that is about to be removed
     */
    /*public void removeCard(Card card){
        if(currentIndex == totalCards){
            //because this is the last card of the deck (3 of 3), roll everything back 1 (2 of 2.)
            //since nextCard() increases currentIndex by 1, reduce currentIndex by 2.
            totalCards--;
            currentIndex -= 2;
            
        }
        else{
            totalCards--;
            currentIndex--;
        }
    }*/
    
    /**Adds data about how well the user did to this session.
     * 
     * @param status Status.LEARNED if they got it, Status.NOT_LEARNED if they didn't
     */
    public void putResult(KnowPanel.Choices choice){
        switch(choice){
            case YES:
                numLearned++;
                break;
            case NO:
                numNotLearned++;
                break;
            case SORT_OF:
                numSortOf++;
                break;
            case SKIPPED:
                //handled in cardSkipped()
                break;
        }
    }
    
    /**Finds this session's current card and sets it as active. Handy if you just switched from another project.
     * 
     */
    public Card reloadCard(){
        return currentCard;
    }
    
    /**As usual, this guy only wants to be informed. Call it when Skip is pressed
     * 
     */
    public void cardSkipped(){
        numSkipped++;
        //card doesn't need to be informed
    }
    
    public int getCurrentIndex(){
        return currentIndex;
    }
    
    /*public void decreaseCurrentIndex(){
        currentIndex--;
    }*/
    
    /* GETTERS */
    
    public int getNumCards(){
        //before you give it back make sure there aren't any cards you missed from quitting early
        return cards.size();
    }
    
    /**Alias for getNumCards().
     * 
     * @return the number of cards in this session
     */
    public int numCards(){
        return getNumCards();
    }
    
    /**Returns the stats of this session.
     * 
     * @return an int[] with 4 values: learned, not learned, sort of, skipped.
     */
    public int[] getCardStats(){
        return new int[] { numLearned, numNotLearned, numSortOf, numSkipped };
    }
    
    /**Determines if this session is done.
     * 
     * @return true if it is done, false otherwise.
     */
    public boolean isFinished(){
        //current index is 0 based, so anything too large returns true
        //System.out.println(currentIndex + " out of " + numCards());
        return currentIndex > numCards();
    }
    
    /** Finds out if there are no cards to be studied.
     * 
     * @return true if there are cards to be studied, false otherwise
     */
    public boolean isEmpty(){
        return numCards() == 0;
    }
}

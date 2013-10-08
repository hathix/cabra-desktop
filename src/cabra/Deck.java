/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cabra;

import java.util.ArrayList;

/** Holds all the note cards for a project. Each project has one.
 *
 * @author Neel
 */
public class Deck extends Object{

    private ArrayList<Card> cards; //flash cards of owner project
    private Card currentCard = null;
    private int currentIndex; //the index of the current card being viewed. Between 0 and length of cards
    
    public Deck(){
        cards = new ArrayList<Card>();
        currentIndex = 0;
    }
    
    public ArrayList<Card> getCards(){
        return cards;
    }
    
    public Card getCurrentCard(){
        return currentCard;
    }
    
    public void makeCurrentCardNull(){
        currentCard = null;
    }
    
    public int getCurrentIndex(){
        return currentIndex;
    }
    
    public int numCards(){
        return cards.size();
    }
    
    /**Returns the number of cards with the given status.
     * 
     * @param status the status you want to look for
     * @return the number of cards with that status
     */
    public int numMatchingCards(Status status){
        int numSelected = 0;
        
        for(Card card : cards){
            if(card.getStatus() == status){
                numSelected++;
            }
        }
            
        return numSelected;
    }
    
    public void add(Card card){
        cards.add(card);
    }
    
    public void remove(Card card){
        cards.remove(card);
    }
    
    //actual meat of the class here
    
    /**
     * Shuffles the deck by randomizing the list of cards
     */
    public void shuffle(){
        ArrayList<Card> newCards = new ArrayList<Card>(); //cards will be moved to here
        
        while(cards.isEmpty() == false){
            //keep going until there are no more cards
            int randomIndex = (int)(Math.random() * cards.size());
            Card randomCard = cards.get(randomIndex);
            //move it from old deck to new one
            cards.remove(randomCard);
            newCards.add(randomCard);
        }
        
        cards = newCards;
        currentIndex = 0; //now we'll draw from the top of the deck
    }
    
    
    /** Finds the next card in the deck and returns it.
     * 
     * @return the next card
     */
    private Card nextCard(){
        Card card = null;
        try{
            card = cards.get(currentIndex);
        }
        catch(IndexOutOfBoundsException e){
            //tried to access a bad location, so shuffle and try again
            shuffle();
            return nextCard(); //return a new card
        }
        currentIndex++;
        if(currentIndex >= cards.size()){
            //we've run out of cards
            shuffle(); //for next time
        }
        return card;
    }
    
    public Card getCard(){
        
        if(numCards() == 0)
            return null; //no cards here
        
            currentCard = nextCard();

            return currentCard;
      //  }  
    }
        
}

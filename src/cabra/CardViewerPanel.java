/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cabra;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/** A panel where you can view all the cards in a project
 *
 * @author Neel
 */
public class CardViewerPanel extends JPanel {

    public static final int MY_WIDTH = 340;
    public static final int MY_HEIGHT = 360;
    
    private Controller controller;
    private JFrame frame;
    
    //components
    /** A panel that holds the cards (is the viewport for the scroll pane.)
     * 
     */
    private JPanel cardHolder;
    
    public CardViewerPanel(Controller controller,JFrame frame){
        this.controller = controller;
        this.frame = frame;
        
        buildComponents();
        
        //add components
        //add(new JLabel("<html><center><b>Here you can view, edit, and delete flashcards.<br>To study them, click Study in the Home tab."));
        
        JScrollPane cardScroller = new JScrollPane(cardHolder,ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        cardScroller.setPreferredSize(new Dimension(MY_WIDTH,MY_HEIGHT));
        add(cardScroller);
        
        update();
    }
    
    public void refresh(){
        SwingUtilities.invokeLater(new Runnable(){
            public void run(){
                update();
            }
        });
        //update();
    }
    
    /** Update which cards are shown.
     * 
     */
    private void update(){
        if(controller.getActiveProject() != null){
            updateWithCards(controller.getActiveProject());
        }
        else{
            //no project; shouldn't happen
        }
    }
    
    /** Updates when you know you have cards.
     * 
     * @param project the active project
     */
    private void updateWithCards(Project project){
        cardHolder.removeAll();
        
        //get cards from project
        ArrayList<Card> cards = project.getCards();
        
        if(cards.isEmpty()){
            //no cards; show some advice
            cardHolder.add(Utils.createAdvicePanel("<html><center><br><b>" + project.getName() + "</b> has no cards."
                    + "<br>You can create some by clicking <b>Create a flashcard</b><br> in the <b>Home tab.</b>"));
            //the extra <br> at the front lets the whole goat be shown in the background
        }
        else{
            GridBagConstraints constraints = new GridBagConstraints();
            constraints.insets = new Insets(3,0,3,0); //top left bottom right
            for(Card card : cards){
                //build card panel and add it
                constraints.gridy++;
                cardHolder.add(new CardPanel(card,project,controller,frame),constraints);
            }
        }
        //refresh view
        repaint();
        validate();
    }
    
    /** Builds the core components.
     * 
     */
    private void buildComponents(){
        cardHolder = new JPanel(new GridBagLayout());
        //cardHolder.setLayout(new GridLayout(0,1,5,5)); //unlimited rows, 5px padding
    }
}

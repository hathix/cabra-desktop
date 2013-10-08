/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cabra;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/** A panel for the question. See StudyTextPanel.
 * @see cabra.abstracts.StudyTextPanel.java
 *
 * @author Neel
 */
public class QuestionPanel extends cabra.abstracts.StudyTextPanel{

    private PicturePanel picturePanel; //picture for flashcard
 
    public QuestionPanel(StudyPanel studyPanel,Controller controller,GUI gui){
        super(studyPanel,controller,gui);
    }
    
    @Override
            public void update(Card card){
                if(card == null){
                    //no card being viewed...
                }
                else{
                    //set the text area's contents
                    textArea.setText( card.getQuestion()); 
                    decideOnPicture(card);
                }                
            }
    
   /** Decides if there should be a picture: if yes, then adds the picture panel; otherwise it removes it.
     * 
     * @param currentCard the new current card
     */
    private void decideOnPicture(Card currentCard){
        if(currentCard.hasPicture()){
            if(picturePanel != null){
                remove(picturePanel);
            }
            //textAreaHolder.remove(picturePanel);
            
            //decrease size of text area
            textArea.setColumns(TEXT_AREA_ROWS_PICTURE);
            //create and add picture panel
            final ImageIcon picture = controller.getActiveProject().getImageIcon(currentCard.getPictureName());
            
            picturePanel = new PicturePanel(picture);
            add(BorderLayout.WEST,picturePanel);
            
            //show full size on click
            picturePanel.setToolTipText("Click to view full size");
            picturePanel.addMouseListener(new MouseAdapter(){
               @Override
               public void mouseClicked(MouseEvent e){
                   ImageManager.showImage(picture, gui.getFrame());
               }
            });
            
            //repaint();
        }
        else{
            //back to normal
            textArea.setColumns(TEXT_AREA_ROWS_NORMAL);
            if(picturePanel != null){
                //remove it
                remove(picturePanel);
                repaint();
            }    
        }
    }    
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cabra.abstracts;

import cabra.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**A panel holding a text area and (possibly) a picture panel. Goes in the study panel.
 * It's in abstracts because we want QuestionPanel to be able to access it, but no one else
 *
 * @author Neel
 */
public abstract class StudyTextPanel extends JPanel{
    
    //references
    protected GUI gui; //for resetting visibility of frame after removing button
    protected Controller controller; //for retrieving pictures from cards
    protected StudyPanel studyPanel; //reacts to the clicking of the show answer button

    //components
    protected JTextArea textArea;
    protected JScrollPane scroller; //holds textArea
    
    //constants
    public static final int TEXT_AREA_COLUMNS = 6;
    public static final int TEXT_AREA_ROWS_NORMAL = 28;
    public static final int TEXT_AREA_ROWS_PICTURE = TEXT_AREA_ROWS_NORMAL - 10;    
    public static final int MY_WIDTH = 320;
    public static final int MY_HEIGHT = 120;
    
    /** Creates a StudyTextPanel. Call <code>update(Card)</code> to stock it with a card.
     * 
     * @param isQuestion True for question area, false for answer area.
     */
    public StudyTextPanel(StudyPanel studyPanel,Controller controller,GUI gui){
        this.studyPanel = studyPanel;
        this.gui = gui;
        this.controller = controller;
        
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(MY_WIDTH,MY_HEIGHT));
        
        //initialize components
        textArea = new JTextArea(TEXT_AREA_ROWS_NORMAL,TEXT_AREA_COLUMNS);
        textArea.setTabSize(CardCreatorPanel.TAB_SIZE); 
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setFont(FontManager.PREFERRED_FONT);
        textArea.setMargin(new Insets(5,5,5,5));
        
        scroller = new JScrollPane(textArea);
        scroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER); 
        
        //add stuff
        add(BorderLayout.CENTER,scroller);
        
    }
    
    /**Updates the components of this panel to fit the current card. Each panel must provide its own implementation
     * 
     * @param card the current card
     */
    public abstract void update(Card card);
    
 
    
    /** Notifies this panel that a card was just skipped. Called by Study Panel when the skip button is called; 
     * Study Panel will immediately call <code>nextCard()</code>. <b>Only Question needs this to be called.</b>
     * 
     */
    /*public void cardSkipped(){
        //it's like 'show answer' was clicked
        remove(showAnswer); //that's the showAnswer JButton
        add(BorderLayout.CENTER,scroller); //add the text area (update() already put text in it)
        gui.makeFrameVisible(); //without this, the JButton doesn't go away
        repaint();        
    }*/
    
    /*public class ShowAnswerListener implements ActionListener{
        public void actionPerformed(ActionEvent e) {
            //empty panel of the button
            remove(showAnswer); //that's the showAnswer JButton
            add(BorderLayout.CENTER,scroller); //add the text area (update() already put text in it)
            gui.makeFrameVisible(); //without this, the JButton doesn't go away
            repaint();
            
            //notify the study panel that the answer has now been viewed
            studyPanel.answerShown();
           
        }
    }*/
        
    //getters and setters
}

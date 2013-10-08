/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cabra;

import javax.swing.*;
import java.awt.event.*;
import java.awt.Color;
import java.awt.Dimension;

/** Where you ask the user if they knew the card. Contains some text and buttons.
 *
 * @author Neel
 */
public class KnowPanel extends JPanel {

    private StudyPanel studyPanel;
    
    private static final String didYouKnowText = "Did you know this?";
    
    public enum Choices{
        YES("00FF00"),
        NO("FF0000"),
        SORT_OF("FFDB2B"),
        SKIPPED("0094FF"),
        ;
        
        private Color color;
        
        public Color getColor(){
            return color;
        }
        
        Choices(Color color){
            this.color = color;
        }
        
        Choices(String hexCode){
            this(ColorManager.createColor(hexCode));
        }
    }
    
    public KnowPanel(StudyPanel studyPanel){
        this.studyPanel = studyPanel;
        
        setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
        
        JLabel label = new JLabel(didYouKnowText);
        
        //create buttons
        /*JButton yes = new JButton(GUI.createImageIcon("check.png"));
            yes.setToolTipText("Yes");
            //yes.setMnemonic(java.awt.event.KeyEvent.VK_Y);
            yes.addActionListener(new ButtonListener(Choices.YES));
        JButton no = new JButton(GUI.createImageIcon("x.png"));
            no.setToolTipText("No");
            //no.setMnemonic(java.awt.event.KeyEvent.VK_N);
            no.addActionListener(new ButtonListener(Choices.NO));
        JButton sortof = new JButton(GUI.createImageIcon("neutral.png"));
            sortof.setToolTipText("Sort of");
            //sortof.setMnemonic(java.awt.event.KeyEvent.VK_N);
            sortof.addActionListener(new ButtonListener(Choices.SORT_OF));   */         
        //add(label);
        add(createButton(Choices.YES,       "Got it",   "check.png",    "I knew the answer"));
        add(createButton(Choices.SORT_OF,   "Sort of",  "neutral.png",  "I sort of knew the answer"));
        add(createButton(Choices.NO,        "Nope",     "x.png",        "I didn't know the answer"));
    }
    
    /**
     * Creates and returns a button that is "yes", "no", or "sort of."
     * @param choice    the choice that the button represents (yes, no, sort of)
     * @param toolTip   the tool tip text for the button, usually same as choice
     * @param imagePath the path to the image used on the button
     * @return  the button
     */
    private JButton createButton(Choices choice, String buttonText,String imagePath, String toolTip){
        JButton button = new JButton(buttonText, GUI.createImageIcon(imagePath));
        button.setToolTipText(toolTip);
        button.addActionListener(new ButtonListener(choice));
        button.setPreferredSize(new Dimension(104, 36));
        return button;
    }
    
    private class ButtonListener implements ActionListener{
        
        private KnowPanel.Choices choice; //Choices.YES if it's the yes button, Choices.NO if it's no
        
        public ButtonListener(KnowPanel.Choices choice){
            this.choice = choice;
        }
        
        public void actionPerformed(ActionEvent e) {
            //alert the study panel that one of the buttons was clicked
            studyPanel.userDecided(choice);
        }   
    }

}

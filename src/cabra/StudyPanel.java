/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cabra;

/**
 *
 * @author Neel
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import static cabra.KnowPanel.Choices;
//import java.util.ArrayList;

public class StudyPanel extends JPanel{
    //the heart and soul of the program, this allows you to see a card's question and answer
   
    
    private TabPane tabPane; //used to handle the nextCard event
    private GUI gui; //just used for the confirm
    private Controller controller;
    
    private JPanel mainPanel; //main contents of this are stored in this panel
    private QuestionPanel question; //question text area panel
    private AnswerPanel answer; //answer text area panel
    private JLabel cardLabel; //shows "card x of y"
    
    private KnowPanel knowPanel; //asks if you knew the card and has buttons
    //private JButton delete; //lets you delete a card
    private JButton forward; //lets you skip this card and go to another
    private JButton quit; //lets you quit the current study session
    private JToolBar toolbar; //forward/delete/etc buttons are on here
    private StackedBarGraph resultGraph;
    
    private Card currentCard; //the card you're viewing right now
    //private Session session; //current study session going on, if any
    
    public static final int MY_WIDTH = 365;
    public static final int MY_HEIGHT = 395;
    
    public StudyPanel(TabPane tabPane,GUI gui,Controller controller){
        this.tabPane = tabPane;
        this.gui = gui;
        this.controller = controller;

        cardLabel = new JLabel();
        question = new QuestionPanel(this,controller,gui);
        answer = new AnswerPanel(this,controller,gui);

        toolbar = createToolbar();     
        knowPanel = new KnowPanel(this);
        
        //addBindings();
    }
    
    /**
     * Called when this study panel is opened and a session needs to be created.
     */
    public void start(){
        newSession();
    }
    
    public void createPanel(){

        addComponents();
        
        //don't add 'next card' button yet; user must click the show answer button to show it
        //same with the know panel
        
        //nextCard();
    }
    
    private void addComponents(){
        setLayout(new BorderLayout());
        mainPanel = new JPanel();

        mainPanel.add(cardLabel);
        
        //add toolbar to left
        //put it in a FlowLayout panel to keep it from stretching
        JPanel toolbarHolder = new JPanel(new FlowLayout());   
        toolbarHolder.add(toolbar);   
        mainPanel.add(toolbarHolder);

        mainPanel.add(question);
        mainPanel.add(answer);  
        
        add(BorderLayout.CENTER, mainPanel);
        //mainPanel.setPreferredSize(new Dimension(MY_WIDTH, MY_HEIGHT - 16));
        
        resultGraph = new StackedBarGraph(0); //for now
        add(BorderLayout.SOUTH, resultGraph);
        
        repaint();
        validate();
    }

    /** Creates the toolbar to go on the side of the flashcard.
     * 
     * @return the toolbar
     */
    private JToolBar createToolbar(){
        //JButton arrow = new JButton(GUI.createImageIcon(direction + ".png"));
        //arrow.setAlignmentY(Component.CENTER_ALIGNMENT); 
        JToolBar bar = new JToolBar(JToolBar.HORIZONTAL);
        bar.setFloatable(false);
        bar.setBorderPainted(false);
        
        //forward button
        forward = new JButton(GUI.createImageIcon("skip.png"));
            forward.addActionListener(new ForwardListener());
            forward.setToolTipText("Skip this card and view another");

        
       quit = new JButton(GUI.createImageIcon("quit.png"));     
            quit.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e){
                    if(gui.confirm("Are you sure you want to quit this studying session?")){
                        endSession();
                    }
                }
            });
            quit.setToolTipText("Quit this studying session");
            
        bar.add(forward);
        bar.add(quit);
        
        bar.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        return bar;
    }
    
    /**Adds bindings to the text areas.
     * 
     */
    /*private void addBindings(){        
        //question area: Tab to show answer
        InputMap inputMap = answer.getInputMap(WHEN_IN_FOCUSED_WINDOW);
        KeyStroke key = KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0);
        Action action = new AbstractAction(){
            public void actionPerformed(ActionEvent e){
                answerShown();
            }
        };
        action.putValue(Action.NAME, "Show answer");
        inputMap.put(key, action);
    }*/    
    
    private void nextCard(){
        //bring on the next card
        currentCard = getSession().getCard();

        if(currentCard == null){
            //all out of cards
            endSession();
            //refresh();
            return;
        }

        reload(); //refreshes GUI
    }
    
    /**Reloads the GUI based on the current card.
     * 
     */
    private void reload(){
        if(getSession() != null && getSession().isFinished()){
            //session is done; end
            endSession();
        }
        else if(currentCard != null){
            //active project with a card
            //update the card for the current project
            currentCard = getSession().reloadCard();
            
            //this updates card contents
            question.update(currentCard);
            answer.update(currentCard);
            //update GUI
            decideOnLabelUpdate();

            forward.setEnabled(true);  
            focusQuestionArea();
            
            //remove the "I didn't know this" area and its buttons
            if(this.isAncestorOf(knowPanel))
                mainPanel.remove(knowPanel);
            
            repaint();           
            validate(); 
            
            focusQuestionArea();
        }
        else if(getSession().getNumCards() != 0){
            //there are cards but they haven't been activated yet
            nextCard();
        }
    }
    
    @Override
    public void validate(){
        focusQuestionArea();
        super.validate();
    }
    
    /** Called by the answer StudyTextPanel when the "show answer" button is clicked
     * 
     */
    public void answerShown(){
        //add the know panel (contains next card buttons)
        mainPanel.add(knowPanel);
        
        forward.setEnabled(false); 

        repaint();
        validate();
    }
    private void decideOnLabelUpdate(){
        //determines what to do - importantCard() or notImportantCard() - based on current card
        
        String text = "<html><center>"; //for the text of the label
        String imageIconText = null;
        
        //question or answer?
        text += "<b>" + "Card ";
        
        //add text like "card 5 of 10"
        //+1 turns makes the number at least 1 (human-readable)
        int current = getSession().getCurrentIndex() + 0;
        int total = getSession().getNumCards();
        text += (current + " of " + total + ":</b><br>");
        text += "Rank " + currentCard.getStatus().toString();
        
        cardLabel.setText(text);
        if(imageIconText != null)
            cardLabel.setIcon(GUI.createImageIcon(imageIconText)); //add image if one was specified
        else
            cardLabel.setIcon(null);
        
    }
    
    /*public void refresh(){
        //called whenever a new project is booted up or a new card is created
        //basically, re-enable anything that was removed by cardlessUpdate
        
        if(controller.getActiveProject() != null && controller.getActiveProject().numCards() == 0){
            //no cards
            cardlessUpdate();
            return;
        }
        
        //just choose an arbitrary component to check
        if(this.isAncestorOf(question) == false){
            //so there's nothing at all in here except the 'no cards' panel
            removeAll();
            addComponents();
        }
        
        validate();
        repaint();
  
        //make a new session
        createSession();
        while(getSession().isEmpty()){
            //no cards due, so skip this one
            //controller.getActiveProject().skipAll();
            createSession();
        }
        //update result graph to match new session
        resultGraph.reset(getSession().getNumCards());
        nextCard();
        reload();
    }*/
    
    
    private void endSession(){
        if(getSession() != null){           
            
            //tell user that they're done
            mainPanel.removeAll();
            //place last session panel in middle of window; move it down a bit
            int shift = 75;
            mainPanel.add(Box.createRigidArea(new Dimension(getWidth(), shift)));
            //remove(resultGraph);
            mainPanel.add(new LastSessionPanel(getSession()));
            //setPreferredSize(new Dimension(LastSessionPanel.MY_WIDTH,LastSessionPanel.MY_HEIGHT));
            
            //earn points if it's a perfect session (must be 10+ cards, to prevent gaming system)
            /*if(getSession().getCardStats()[0] == getSession().getNumCards()){
                int numCards = getSession().getNumCards();
                if(numCards >= 50)
                    controller.gainPoints(PointEnums.Activity.PERFECT_SESS_50);
                else if(numCards >= 25)
                    controller.gainPoints(PointEnums.Activity.PERFECT_SESS_25);
                else if(numCards >= 10)
                    controller.gainPoints(PointEnums.Activity.PERFECT_SESS_10);                
            }*/
            
            //inform the session to have the project delete it
            getSession().end();
            
            //add a button that lets the user start over
            JButton restart = new JButton("Start a new studying session",GUI.createImageIcon("lightbulb.png"));
            restart.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e) {
                    removeAll();
                    addComponents();
                    newSession();
                }
            });
            mainPanel.add(restart);
            
            validate();
            repaint();
        }
    }
    
    /** Makes a new session automatically.
     * 
     */
    private void newSession(){
        getProject().newSession();
        //update bar graph
        this.resultGraph.reset(getSession().getNumCards());
        
        nextCard();
    }
    
    /** Focuses the question text area.
     * 
     */
    public void focusQuestionArea(){
        answer.requestFocus();
    }
    
    /** Called when the dialog containing this is closing. Asks for confirmation if necessary.
     * 
     * @return true if it's OK to close the dialog, false otherwise
     */
    public boolean windowClosing(){
        //setSize(MY_WIDTH,MY_HEIGHT);
        
        if(getSession() == null){
            //no session; it's fine to close
            return true;
        }
        
        //they have a session; now ask them
        boolean ok = InputManager.confirm(
                "<html><center>You are in the middle of a studying session.<br>"
                + "Are you sure you want to quit studying?", 
                gui.getFrame());
        if(ok == false){
            //it's not ok to close; continue as normal
            return false;
        }
        else{
            //end session and clean up
            endSession();
            return true;
        }
    }
    
    private JPanel createNoCardPanel(){
        JPanel panel = new JPanel(false);
        JLabel filler = new JLabel("<html><center>There are <b>no flashcards</b> in this project.<br>Create some in the <b>Home</b> tab.");
        filler.setHorizontalAlignment(JLabel.CENTER);
        panel.setLayout(new GridLayout(1, 1));
        panel.add(filler);
        return panel;
    }    
    
    
    /** Called when someone chooses 'yes' or 'no' from KnowPanel. Reacts accordingly and calls <code>nextCard()</code>.
     * 
     * @param choice 
     */
    public void userDecided(final Choices choice){ 
        Status oldStatus = currentCard.getStatus();
        
        currentCard.study(choice);
        tabPane.getActiveProject().save();
        
        //give points if they were right
        if(choice == Choices.YES){
            controller.gainPoints(PointEnums.Activity.STUDY_CORRECT);
            
            //if the old status was D and it's now E, then you just got the card up to E rank. Good job!
            if(oldStatus == Status.D && this.currentCard.getStatus() == Status.E){
                controller.gainPoints(PointEnums.Activity.RANK_E);
            }
        }
        
        getSession().putResult(choice);
        //update home panel some time
        SwingUtilities.invokeLater(
                new Runnable(){ public void run(){
                    controller.refreshHomePage();
                }});
        //update results bar some time
        SwingUtilities.invokeLater(
                new Runnable(){ public void run(){
                    resultGraph.addResult(choice);
                }});
        
        nextCard();            
   }
    
    private Session getSession(){
        if(getProject() == null)
            return null;
        return getProject().getSession();
    }
    
    private Project getProject(){
        return controller.getActiveProject();
    }
    
    private class ForwardListener implements ActionListener{
        public void actionPerformed(ActionEvent e) {
            //see a new card
            getSession().cardSkipped();   
            //update results bar some time
            SwingUtilities.invokeLater(
                    new Runnable(){ public void run(){
                        resultGraph.addResult(Choices.SKIPPED);
                    }}); 
            nextCard();
        }
    }
     
    @Override
        public void paintComponent(Graphics g){
            super.paintComponent(g);
            
            /*if(getSession() == null){
                //the session setup panel is showing
                Utils.drawEmblem(mainPanel, g);
            }*/
            //if something's being studied, don't draw the emblem cause it would look horrible
        } 
    
    /** A panel with a stacked bar graph that shows the results of each individual card.
     * This is shown at the bottom of the study panel to show the user's progress
     * 
     */
    public static class StackedBarGraph extends JPanel{

        /** Contains the final result of each card (yes, no, sort of, skipped.)
         * 
         */
        private Choices[] results;
        
        /**
         * Points to the last filled item in the results array. Also how many cards have been studied to this point.
         */
        private int lastResult;
        
        /**
         * How tall this StackedBarGraph is.
         */
        private static final int SBG_HEIGHT = 16;

        /**
         * Creates a StackedBarGraph.
         * @param cardsInSession how many cards are in this session.
         */
        public StackedBarGraph(int cardsInSession){
            reset(cardsInSession);
            
            setPreferredSize(new Dimension(MY_WIDTH, SBG_HEIGHT));
        }
        
        /**
         * Adds a result to the list of results. Called when a card is studied; pass the user's "know choice."
         * @param result 
         */
        public void addResult(Choices result){
            results[lastResult] = result;
            lastResult++;
            
            repaint();
        }
        
        /**
         * Resets the contents of this graph to empty. Called when there's a new session.
         * @param newNumCards the number of cards in the new session.
         */
        public void reset(int newNumCards){
            results = new Choices[newNumCards];
            lastResult = 0;            
        }

        @Override
        public void paintComponent(Graphics g){
            super.paintComponent(g);

            //paint a bit of the bar for each card studied
            
            int topX = 0; //marks where to start each bit
            //int bitWidth = (int)(1.0 * getWidth() / results.length);
            int width = getWidth();
            int height = getHeight();
            
            for(int i=0; i<results.length; i++){
                Choices result = results[i];
                if(result == null){
                    //once one's empty, the rest must be because it's filled in order
                    //can exit immediately
                    break;
                }
                
                //set translucent color; copy default color and make it less opaque
                final int opacity = 128;
                g.setColor(ColorManager.translucent(result.getColor(), opacity));
                
                //determine where to place it; dynamically generate new width so we fill up the whole space
                int widthLeft = width - topX;
                int resultsLeft = results.length - i;
                int barWidth = (int)(1.0 * widthLeft / resultsLeft);
                
                g.fillRect(topX, 0, barWidth, height);
                
                topX += barWidth;
            } //end foreach
            
            /*
            //draw text showing what percent done you are
            g.setColor(Color.BLACK);
            g.setFont(FontManager.PREFERRED_FONT);
            g.drawString("SDFSDFSDFSDFS", 10, 0);
            */
        }
    }    
}

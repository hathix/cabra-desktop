/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cabra;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/** The home panel, with project stats on it.
 *
 * @author Neel
 */
public class HomePanel extends JPanel{
    
    //private TabPane tabPane;
    private Controller controller;
    private GUI gui;
    private TabPane tabPane;
    private StudyPanel studyPanel;
    
    private JDialog cardCreatorDialog;
    private CardCreatorPanel cardCreatorPanel;
    private GraphBar[] graphBars;
    private JLabel totalCards; //shows how many cards you have in total
    private JLabel projectName; //name of project
    
    //buttons
    private JButton addCard;
    private JButton resetDeck;
    private JButton study;
    
    public static final int TOP_WIDTH = 320;
    public static final int TOP_HEIGHT = 60;
    
    public HomePanel(TabPane tabPane, StudyPanel studyPanel, GUI gui, Controller controller){
        //this.tabPane = tabPane;        
        this.gui = gui;
        this.controller = controller;
        this.studyPanel = studyPanel;
        this.tabPane = tabPane;
        
        this.setDoubleBuffered(true);
        
        //setLayout(new BoxLayout(this,BoxLayout.PAGE_AXIS));
        //initialization
        cardCreatorPanel = new CardCreatorPanel(tabPane,gui,controller);
        cardCreatorPanel.createPanel();
        //it's in its own function since it'll be called again later
        initializeCardCreatorDialog();
        
        assemblePanel();
    }
    
    private void initializeCardCreatorDialog(){
        //card creator panel is already made
        //the card creator dialog
        cardCreatorDialog = Utils.putPanelInDialog(
                cardCreatorPanel,
                gui.getFrame(),
                "Create a flashcard (use Tab to cycle through fields)",
                "card-add-16.png",
                CardCreatorPanel.MY_WIDTH,
                CardCreatorPanel.MY_HEIGHT
                );          
    }
    
    private JDialog createStudyDialog(){
        String projName = controller.getActiveProject().getName();
        final JDialog studyDialog = Utils.putPanelInDialog(
                studyPanel,
                gui.getFrame(),
                "Studying " + projName,
                "lightbulb.png",
                StudyPanel.MY_WIDTH,
                StudyPanel.MY_HEIGHT
                );
        
        studyDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        studyDialog.addWindowListener(new WindowAdapter(){
            @Override
            public void windowClosing(WindowEvent e){
                //if they close the window early, it's like quitting the study session
                //inform the panel and ask if it's OK to close
                if(studyPanel.windowClosing()){
                    //ok to close
                  studyDialog.setVisible(false);
                  //controller.refresh();
                }
                else{
                    //the user decided not to end this session
                }
            }
        });
        
                
        return studyDialog;
    }
    
    private void assemblePanel(){
        //button to add a card to active project
        addCard = new JButton("Create a flashcard",GUI.createImageIcon("card-add.png"));
        addCard.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                showCardCreator();
            }
        });
        
        //button to reset all cards in project
        resetDeck = new JButton("Reset deck",GUI.createImageIcon("reload.png"));
        resetDeck.setToolTipText("Make all cards in the deck rank A");
        resetDeck.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                if(InputManager.confirm("<html><center>Are you sure you want to reset the deck?<br>This will revert all cards to <b>Rank A.</b>"
                        ,gui.getFrame())){
                    controller.getActiveProject().resetAllCards();
                    controller.refresh();
                }
            }
        });
        
        
        study = new JButton("Study",GUI.createImageIcon("lightbulb.png"));
        study.setToolTipText("Start studying this project's cards");
        study.addActionListener(new ActionListener(){
           public void actionPerformed(ActionEvent e){ 
               controller.getActiveProject().shuffle();
               //build and show dialog for studying
               JDialog studyDialog = createStudyDialog();
               //tell study panel to begin studying
               studyPanel.start();               
               studyDialog.setVisible(true);
           }
        });
        
        //top two labels
        totalCards = new JLabel();
            totalCards.setHorizontalAlignment(SwingConstants.CENTER);
        projectName = new JLabel();
            projectName.setHorizontalAlignment(SwingConstants.CENTER);
        JPanel topHolder = new JPanel(new GridLayout(1,2,5,5)); //for both of the labels
            topHolder.setPreferredSize(new Dimension(TOP_WIDTH, TOP_HEIGHT));
        topHolder.add(projectName);        
        topHolder.add(totalCards);
        
        //bars for how many of each card you have
        JPanel stats = createBarGraphs();
        
        //last few buttons
        JPanel lower = new JPanel(new GridLayout(3,1));
        lower.setPreferredSize(new Dimension(180,40*3));
        lower.add(addCard);
        lower.add(study);        
        lower.add(resetDeck);        
        
        //assemble it
        add(topHolder);
        add(stats);
        add(lower);
    }
    
    private JPanel createBarGraphs(){
        JPanel holder = new JPanel(new GridBagLayout()); //so we can have a grid without giving too much room to labels
        
        GridBagConstraints constraints = new GridBagConstraints();
        //5px padding on all sides of contents
        //constraints.ipadx = 5;
        //constraints.ipady = 5;
        constraints.insets = new Insets(5,3,5,3);
        
        graphBars = new GraphBar[] {new GraphBar(Status.A), //red
                                    new GraphBar(Status.B), //orange
                                    new GraphBar(Status.C), //gold
                                    new GraphBar(Status.D), //green
                                    new GraphBar(Status.E), //blue
                                   };
            //first one is learned, second not learned, third not studied
        
        String[] phrases = {"Rank A","Rank B","Rank C","Rank D","Rank E"};
        
        //1st row
        constraints.gridy = 0;
        for(int i=0;i<phrases.length;i++){
            constraints.gridx = i;
            holder.add(graphBars[i],constraints);            
        }
        
        //2nd row
        constraints.gridy = 1;
        for(int i=0;i<phrases.length;i++){
            JLabel label = new JLabel(phrases[i]);
            label.setVerticalAlignment(SwingConstants.NORTH);
            constraints.gridx = i;
            holder.add(label,constraints);           
        }
                    
        return holder;
    }
    
    
    public void showCardCreator(){
        //the card creator panel's there, just recreate the dialog
        //this causes a new dialog to pop up when you hit create card (otherwise it would just regain visibility)
        initializeCardCreatorDialog();
        cardCreatorDialog.setVisible(true);
        
        //no need to do this, the dialog is recreated in initializeCardCreatorDialog
        //when it closes, destroy the card creator dialog
        /*cardCreatorDialog.addWindowListener(new WindowAdapter(){
            @Override
            public void windowClosed(WindowEvent e){
                cardCreatorDialog = null;
            }
        });*/
    }
    
    public void refresh(){
        cardCreatorPanel.refresh();
        
        Project activeProject = controller.getActiveProject();
        int numCards = activeProject.numCards();
        int[] stats = activeProject.cardStatuses();
        
        final int FONT_SIZE = 16;
        
        //update headers
        projectName.setText("<html><b><center>" + HomePanel.wrapText(activeProject.getName())); 
        String text = activeProject.numCards() == 1 ? " card" : " cards";
        totalCards.setText("<html><center><b><span style='font-size:" + FONT_SIZE + "'>" + numCards + text);

        //update graphs
        for(int i=0;i<graphBars.length;i++){
            graphBars[i].setPercent(stats[i],numCards);
        }
        
        //update buttons: if there are no cards in the active project, don't let them reset/shuffle
        resetDeck.setEnabled(numCards != 0);
        study.setEnabled(numCards != 0); //only requirement here is that there are cards
        //shuffleDeck.setEnabled(numCards != 0);
    }
    
    private final class GraphBar extends JPanel{
        private double percent; //how far this should fill up... 0 to 1
        private int numCards; //how many cards there are in our category (rank A, B, C...)
        private Color drawColor; //color used to draw graph
        
        private final static int BAR_WIDTH = 60;
        private final static int BAR_HEIGHT = 120;
        
        /**
         * Creates a bar graph for a certain status/rank.
         * @param status the status for which to draw the graph.
         */
        public GraphBar(Status status){
            setPercent(0,0);
            setPreferredSize(new Dimension(BAR_WIDTH,BAR_HEIGHT));
            setToolTipText(status.getToolTipText());
            this.drawColor = status.getColor();
        }
        
        public double getPercent(){ return percent; }
        public void setPercent(int howManyCards, int totalCards){
            if(totalCards == 0){
                //we'd divide by 0 if we continued, so let's hard-code it
                percent = 0;
                numCards = 0;
            }
            else{
                percent = howManyCards / (totalCards + 0.0);
                numCards = howManyCards;
            }
            this.repaint();
        }        
        
        @Override
        public void repaint(){
            //just run this in a new thread
            new Thread(new Runnable(){
                public void run(){
                        paintGraphs();
                    }
            }).start();
        }
        
        public void paintGraphs(){
            super.repaint();
        }
        
        @Override
        //called when the bar needs to get updated
        public void paintComponent(Graphics g){
            super.paintComponent(g);
            //Graphics2D g2d = (Graphics2D)g;
            g.setColor(drawColor);
            //g.setFont(FontManager.createFont(14,"Lao UI","Calibri","Ubuntu"));
            
            //paint rectangle
            //make it as tall as the panel but not as wide
            int width = getWidth();
            int height = (int)(percent * getHeight());

            //start drawing at top left corner of rectangle
            g.fillRoundRect(0,getHeight() - height,width,height,10,10); //last 2 args are border radius

            //draw the number of cards
            g.setColor(Color.black);
            g.setFont(FontManager.SMALLER_PREFERRED_FONT);
            String cardText = numCards == 1 ? " card" : " cards";
            String percentString = Math.round(percent * 100) + "";
            //draw and put in center of bar
            g.drawString(numCards + cardText,7,getHeight() / 2 + 0);
            g.drawString("(" + percentString + "%)",14,getHeight() / 2 + 15);
        }
    }
    
    /**If a string is too long, it is wrapped, with <code><br></code>s in between
     * 
     * @param text the text to be wrapped
     * @return the wrapped string
     */
    private static String wrapText(String text){
        String finalText = text; //what we'll return
        
        int fontSize = (int)(18 - ((text.length() - 5) / 5.0) * 2);
        if(fontSize > 15)
            fontSize = 15;
        
        /* length - lines - size - on a line
         * 15 - 2 (12) - 15
         * 20 - 3 (10) - 20
         * 10 - 1 (14) - 10
         */
        
        // here's a change: we just shrink, we don't line break
        //only break if text is too small
        if(fontSize <= 10){
            finalText = "";
            int numLines = 1; //number of lines after wrapping
            //get an array of each word in the title
            String[] words = text.split(" ");

            fontSize = 11;
            int charsOnLine = 0;
            
            for(String word : words){
                int length = word.length();
                if(charsOnLine + length > 18){
                    //if the word causes too many letters to be on the line, wrap it
                    //that is, tack on a <br> after the previous word
                    finalText += "<br>" + word + " ";
                    charsOnLine = 0; //it's a new line, after all
                    numLines++;
                }
                else{
                    //there are enough chars left
                    finalText += word + " ";
                    charsOnLine += length;
                }
            }
        }

        finalText = "<span style='font-size:" + fontSize + "px;'>" + finalText;
        
        return finalText;
    }  
    
    
    @Override
        public void paintComponent(Graphics g){
            super.paintComponent(g);

            Utils.drawEmblem(this, g);
        }    
}

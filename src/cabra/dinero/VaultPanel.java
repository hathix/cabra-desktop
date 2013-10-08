package cabra.dinero;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Shows the prizes the user has bought.
 */
public class VaultPanel extends JPanel{
   private VaultManager vaultManager; //the controller
   private JPanel prizePanel;
   private JScrollPane prizeScroller;
   
   public VaultPanel(VaultManager vaultManager){
      this.vaultManager = vaultManager;
      
      //init view
      setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
      //add(pointsLabel);
      
      prizePanel = createPrizeGridPanel();
      //wrap prize panel in the prizeScroller
      prizeScroller = wrapInScrollPane(prizePanel);
      add(prizeScroller);
      
      setSize(VaultTabPane.MY_WIDTH, VaultTabPane.MY_HEIGHT);
   }
   
   /**
    * Called when a prize(s) is purchased. This updates the list of prizes.
    */
   public void prizePurchased(){
      //remove scroller, recreate scroller and contained panel, re add
      remove(prizeScroller);
      prizePanel = createPrizeGridPanel();
      prizeScroller = wrapInScrollPane(prizePanel);
      add(prizeScroller);
   }
   
   private JScrollPane wrapInScrollPane(JPanel panel){
      JScrollPane scroller = new JScrollPane(panel);
      scroller.setPreferredSize(new Dimension(VaultTabPane.MY_WIDTH, VaultTabPane.MY_HEIGHT));
      scroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
      scroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
      return scroller;
   }
   
   /**
    * Returns a JPanel with a GridLayout containing each of the user's prizes.
    */
   private JPanel createPrizeGridPanel(){
      final int NUM_PRIZES_ROW = 4; //num prizes in a row; also num cols
      final int MIN_NUM_ROWS = 2; //minimum number of rows of prizes
      final int PADDING = 5; //px
      
      JPanel panel = new JPanel();
      GridLayout layout = new GridLayout(0, NUM_PRIZES_ROW, PADDING, PADDING);
      panel.setLayout(layout);
      
      //put panels in the grid
      /*
      //ALT METHOD: show grid of what you have and don't have
      ArrayList<Prize> allPrizes = Prize.getAllPrizes();
      ArrayList<Prize> purchasedPrizes = vaultManager.getPurchasedPrizes();
      for(Prize prize : allPrizes){
         //do you have it?
         if(purchasedPrizes.contains(prize)){
            //yup, add the panel
            panel.add(new PrizePanel(prize));
         }
         else{
            //don't have it, add a question mark
            panel.add(new PrizePanel(null));
         }
      }
      */
      
      //get list of prizes the user has
      ArrayList<Prize> purchasedPrizes = vaultManager.getPurchasedPrizes();
      //sort them - Prize class implements how we should compare them, this just wraps it
      Collections.sort(purchasedPrizes, new Comparator<Prize>(){
         public int compare(Prize a, Prize b){
            //ask prize how to compare them
            return Prize.compare(a, b);
         }
      });
      
      //determine how many rows are necessary; fill up these rows and add blank placeholders to fill them
      int rows = Math.max(MIN_NUM_ROWS, purchasedPrizes.size() / NUM_PRIZES_ROW + 1);
//10 prizes -> 2 full rows (/4), 1 not full); 12 prizes -> 3 full rows, 1 totally empty (for motivation)
      //fill with as many prizes as we can
      for(Prize prize : purchasedPrizes){
         panel.add(new PrizePanel(prize));
      }
      //fill remainder of row with placeholders (or the entire rest of the panel)
      int totalSquares = rows * NUM_PRIZES_ROW;
      int placeholders = totalSquares - purchasedPrizes.size(); //20 squares, 4 prizes -> 16 blank placeholders
      for(int i=0; i<placeholders; i++){
         panel.add(new PrizePanel(null));
      }
      
      return panel;
   }
   
   /**
    * Creates a table containing all the prizes that the player has purchased. 
    * NOT WORKING NOW
    */
   /*private JTable createPrizeTable(){
    JTable table;
    
    //fill each cell with the prizes
    ArrayList<Prize> prizes = vaultManager.getPurchasedPrizes();
    int numPrizes = prizes.size();
    
    final int NUM_PRIZES_ROW = 3; //number of prizes in a row; also num cols
    int numRows = numPrizes % 3 + 1; //4 prizes -> 2 rows, for example
    Object[][] data = new Object[numRows][NUM_PRIZES_ROW]; //rows x cols
    //for(
    
    Object[][] data = new Object[numActivities][columnNames.length];
    for(int i=0; i<values.size(); i++){
    //the row contains information from the values array of the same index
    data[i] = createActivityRow(values.get(i));
    }
    
    table = new JTable(data, columnNames){
    //make table read-only
    @Override
    public boolean isCellEditable(int row, int column){
    return false;
    }
    };
    //
    table.getColumnModel().getColumn(0).setCellRenderer(new ImageRenderer());
    
    
    table.setRowHeight(40);
    table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    table.setFillsViewportHeight(true);
    return table;
    }*/
}
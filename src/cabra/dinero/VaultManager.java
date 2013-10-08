package cabra.dinero;

import cabra.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Arrays;
import sun.awt.WindowClosingListener;

/**
 * Controller class for the Vault.
 * It receives input from the view (purchasePrize()), changes the model, and updates the view.
 * 
 * Most-commonly-used public methods:
 * int getPoints()
 * boolean purchasePrize()
 */
public class VaultManager {

    private PointManager pointManager;
    private ArrayList<Prize> purchasedPrizes = new ArrayList<Prize>();
    
    private GUI gui;
    private VaultTabPane tabPane;
    private JLabel pointLabel;
    
    private final static int MAX_POINTS_DIGITS = 5; //points label will guaranteed have this many digits (00055 for example)

    /**
     * Usage:
     * Create a VaultManager when you want to start up the vault.
     */
    public VaultManager(PointManager pointManager, final GUI gui, JLabel pointLabel) {
        this.gui = gui;
        this.pointManager = pointManager;
        pointManager.giveVaultManager(this);
        this.pointLabel = pointLabel;
        
        //stock points/prizes with the data stored
        String[] prizeArray = Utils.arrayFromString(UserData.getString("PrizesBought"));
        for (String prizeString : prizeArray) {
            Prize prize = Prize.getPrizeByID(prizeString); //prizeString is unique ID of prize
            if (prize != null) {
                purchasedPrizes.add(prize);
            }
        }

        //init tabPane
        tabPane = new VaultTabPane(this);

        //init view
        /*frame = new JFrame("Cabra Vault");
        frame.setIconImages(ImageManager.createImages("safe.png", "safe-16.png"));
        frame.add(tabPane);
        frame.setSize(new Dimension(WIDTH, HEIGHT));
        frame.setLocationRelativeTo(null); //center on screen
        frame.addWindowListener(new WindowAdapter(){
            @Override
            public void windowClosing(WindowEvent e){
                //update the main gui
                //gui.refresh(); //buggy
            }
        });*/
        
        
        //<TODO>: PUT IN MAIN PANEL
    }
    
    /**
     * Returns a panel containing stuff related to THIS vault manager (the tab pane.)
     * @return 
     */
    public JPanel getPanel(){
        JPanel holder = new JPanel();
        holder.add(tabPane);
        return holder;
    }    
    
    /**
     * Returns the frame this is contained in.
     * @return the frame that the vault stuff is in.
     */
    public JFrame getFrame(){ return gui.getFrame(); }

    public int getPoints() {
        return pointManager.getPoints();
    }
    


    /**
     * Decreases the number of points the user has by the given amount.
     * This just redirects to 
     */
    private void decreasePoints(int pointsToLose) {
        pointManager.decreasePoints(pointsToLose);
        update();
    }
 
    /**
     * Updates based on how many points the user has.
     */
    public void update(){
        updatePointLabel(pointManager.getPoints());
        tabPane.getVaultPanel().prizePurchased();
        tabPane.getStorePanel().pointsChanged(pointManager.getPoints());
    }    
    
    /***
     * Refreshes the points label so it reflects the number of points the user has.
     * @param points how many points the user has.
     */
    public void updatePointLabel(int points){
        //put zeroes in front so it looks like an old-style arcade display; can be disabled
        //String pointText = Utils.padWithLeadingZeroes(points, MAX_POINTS_DIGITS);
        String pointText = points + "";
        //string pointText = points + ""; //for the usual representation
        String text = "<html><center><b>" + pointText + "</b>";
        pointLabel.setText(text);
        pointLabel.setToolTipText("You've earned " + points + " coins while studying using Cabra!");   
    }

    /**
     * Purchases the prize pack. The user's points are decreased by the pack's cost,
     * and the prizes contained in the pack is added to the list of purchased prizes.
     * 
     * @param prize the prize pack to purchase
     * @return true if the user can afford the prize pack (and bought it), false if ther user can't afford it
     */
    public boolean purchasePrizePack(PrizePack pack) {
        if (canUserAfford(pack) == false) {
            return false; //user can't afford
        }
        //buy it
        decreasePoints(pack.getCost());
        for (Prize prize : pack.retrievePrizes()) {
            this.purchasedPrizes.add(prize);
            //tabPane.prizePurchased(prize);

            System.out.println(prize.getName() + " - " + prize.getRarity());
        }

        /*
        //remove duplicates; duplicates will be adjacent to each other
        for(int i=purchasedPrizes.size()-1; i>0; i--){
        if(purchasedPrizes.get(i).equals(purchasedPrizes.get(i-1)))
        purchasedPrizes.remove(i);
        }
         */

        //Save the list of prizes; it's overwritten so write ALL that we have
        //convert each to string first; use its ID
        int numPrizes = purchasedPrizes.size();
        String[] ids = new String[numPrizes];
        for (int i = 0; i < numPrizes; i++) {
            ids[i] = purchasedPrizes.get(i).getID();
        }
        String stringifiedIDs = Utils.stringFromArray(ids);
        UserData.setString("PrizesBought", stringifiedIDs);
        System.out.println("Saved prize data: " + stringifiedIDs);

        update();
        
        return true;
    }

    /**
     * Convenience method. Returns true if the user can afford the given prize pack (has enough points), false otherwise.
     */
    public boolean canUserAfford(PrizePack pack) {
        return pack.canUserAfford(getPoints());
    }

    /**
     * Returns a list of all prizes that have NOT been purchased.
     * They may or may not be affordable.
     */
    public ArrayList<Prize> getPurchaseablePrizes() {
        ArrayList<Prize> purchaseable = new ArrayList<Prize>();
        //for each of the prizes, check if it's in the purchased prizes list
        for (Prize prize : Prize.getAllPrizes()) {
            if (purchasedPrizes.contains(prize) == false) {
                //not purchased
                purchaseable.add(prize);
            }
        }

        return purchaseable;
    }

    /**
     * Returns a list of all prizes that the user has bought. These are, therefore, not available for purchase.
     */
    public ArrayList<Prize> getPurchasedPrizes() {
        return purchasedPrizes;
    }
}
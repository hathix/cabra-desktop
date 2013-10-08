package cabra.dinero;

import cabra.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

/**
 * Displays a list of all prizes available for purchase.
 */
public class StorePanel extends JPanel {

    private VaultManager vaultManager; //the controller
    private ArrayList<PrizePackPanel> prizePackPanels = new ArrayList<PrizePackPanel>();

    public StorePanel(VaultManager vaultManager) {
        this.vaultManager = vaultManager;

        //init view
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

        JPanel prizePackPanel = createPrizePackPanel();
        //wrap in scroll pane
        JScrollPane scrollPane = new JScrollPane(prizePackPanel);
        scrollPane.setPreferredSize(new Dimension(VaultTabPane.MY_WIDTH, VaultTabPane.MY_HEIGHT));
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        add(scrollPane);
    }

    /**
     * Called when a prize(s) is purchased.
     * This updates the points label and the point pack grid based on what the user can afford.
     * @param newPoints the new number of points the user has.
     */
    public void pointsChanged(int newPoints) {
        //tell vault manager
        vaultManager.updatePointLabel(newPoints);

        //tell each prize pack panel to update - it may have become unaffordable
        for (PrizePackPanel panel : prizePackPanels) {
            panel.updateBuyButton();
        }
    }

    /**
     * Creates a panel containing stack of JPanels, each containing one prize pack available for purchase.
     */
    private JPanel createPrizePackPanel() {
        final int PADDING = 0;
        final int NUM_PACKS_ROW = 2; //also num cols

        JPanel panel = new JPanel(new GridLayout(0, NUM_PACKS_ROW, PADDING, PADDING)); //as many rows as necessary
        //make a panel for each prize pack you can buy
        for (PrizePack pack : PrizePack.getAllPrizePacks()) {
            PrizePackPanel packPanel = new PrizePackPanel(pack);
            prizePackPanels.add(packPanel); //for future reference
            panel.add(packPanel);
        }

        return panel;
    }

    /***
     * A panel used to display one prize pack in the store. It shows its rarity and cost, and has a button to buy it.
     */
    private class PrizePackPanel extends JPanel {

        private PrizePack prizePack;
        private JButton buyButton;
        private static final int PADDING = 5; //between elements
        private static final int MAX_STARS = 5; //rarest pack can have this many stars

        public PrizePackPanel(PrizePack prizePack) {
            this.prizePack = prizePack;

            //init
            setLayout(new GridBagLayout());
            GridBagConstraints c = new GridBagConstraints();
            c.anchor = GridBagConstraints.CENTER;
            c.insets = new Insets(PADDING, PADDING, PADDING, PADDING);
            c.gridx = 0;
            c.gridy = 0;
            c.gridwidth = 1;
            c.gridheight = 1;

            //image of pack
            //c.weighty = 1;
            //put in label
            JLabel packLabel = new JLabel(ImageManager.createImageIcon("box-close.png"));
            //packLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
            add(packLabel, c);

            //stars representing the rarity
            //c.weighty = 0;
            c.gridx++;
            JPanel starPanel = new JPanel(new GridLayout(1, 0)); //as many cols as necessary, no padding
            int numStars = prizePack.getStars(MAX_STARS);
            starPanel.setToolTipText("This pack has a rarity of " + numStars + " star(s).");
            //put labels in here, one per star
            for (int i = 0; i < numStars; i++) {
                //put in a label
                JLabel label = new JLabel(ImageManager.createImageIcon("star.png"));
                starPanel.add(label);
            }
            add(starPanel, c);

            //label representing the rarity
            c.gridx = 0;
            c.gridy++;
            JLabel pointLabel = new JLabel(ImageManager.createImageIcon("coins.png"));
            pointLabel.setText(prizePack.getCost() + "");
            pointLabel.setToolTipText("This pack costs " + prizePack.getCost() + " points.");
            //pointLabel.setAlignment
            add(pointLabel, c);

            //button to buy it
            c.gridx++;
            buyButton = new JButton();
            buyButton.addActionListener(new BuyListener(prizePack));
            updateBuyButton();
            add(buyButton, c);
        }

        /**
         * Updates the buy button (which lets the user buy the pack) based on whether or not the user can afford it.
         */
        public void updateBuyButton() {
            if (vaultManager.canUserAfford(prizePack)) {
                //user can afford, make it clickable and say buy
                buyButton.setText("Buy pack");
                buyButton.setIcon(ImageManager.createImageIcon("coin-gold.png"));
                buyButton.setToolTipText("Contains 3 prizes");
            } else {
                //use can't afford, disable it
                buyButton.setText("Can't afford");
                //buyButton.setIcon(ImageManager.createImageIcon("x.png"));
                buyButton.setIcon(null);
                buyButton.setToolTipText(null);
                buyButton.setEnabled(false);
            }
        }

        private class BuyListener implements ActionListener {

            private PrizePack prizePack;

            public BuyListener(PrizePack prizePack) {
                this.prizePack = prizePack;
            }

            public void actionPerformed(ActionEvent e) {
                //are they sure?
                String confirmText = "Are you sure you want to spend " + prizePack.getCost() + " points buying this prize pack?";
                if (InputManager.confirm(confirmText, null)) {
                    //show dialog containing the pack opening and the prizes they won coming out
                    JPanel panel = new PackOpenPanel(prizePack);
                    //wrap in dialog
                    JDialog dialog = Utils.putPanelInDialog(panel, null, 
                            "Opening the prize pack... here's what you found!",
                            "box-open.png", PackOpenPanel.MY_WIDTH, PackOpenPanel.MY_HEIGHT);
                    dialog.setVisible(true);

                    //tell vault manager to buy this
                    vaultManager.purchasePrizePack(prizePack);
                }
            }
        }

        /**
         * A panel to show a prize pack being opened - showing the prizes that came out of it
         */
        private class PackOpenPanel extends JPanel {

            private PrizePack prizePack;
            public static final int MY_WIDTH = 400;
            public static final int MY_HEIGHT = 150;
            private static final int PADDING = 25; //between edges and panels
            private static final int IPADDING = 40; //internal padding; between panels

            public PackOpenPanel(PrizePack prizePack) {
                this.prizePack = prizePack;

                //setInsets(new Insets(PADDING, PADDING, PADDING, PADDING));
                setLayout(new GridBagLayout());
                GridBagConstraints c = new GridBagConstraints();
                c.insets = new Insets(PADDING, PADDING, PADDING, PADDING);
                c.ipadx = IPADDING;
                c.ipady = IPADDING;

                /*
                //add opening box icon
                JPanel imagePanel = new JPanel(new GridLayout(1,1));
                imagePanel.add(new JLabel(ImageManager.createImageIcon("box-open.png")));
                add(imagePanel, c);
                 */

                //add prize panels for each of the prizes contained in the pack
                for (Prize prize : prizePack.retrievePrizes()) {
                    PrizePanel panel = new PrizePanel(prize);
                    add(panel, c);
                }
            }
        }
    }
}
package cabra;

import cabra.dinero.VaultManager;
import cabra.PointEnums.Activity;
import java.awt.event.ActionEvent;
import java.util.EventObject;
import javax.swing.*;
import java.awt.*;
import javax.swing.event.CellEditorListener;
import javax.swing.table.DefaultTableCellRenderer;
import java.util.ArrayList;
import java.awt.event.ActionListener;

/** A panel to show and keep track of how many points the user has.
 *
 * @author Neel
 */
public class PointPanel extends JPanel{
    private Controller controller;
    private VaultManager vaultManager; //controls everything relating to prizes etc
    private GUI gui;
    private TabPane tabPane;
    private JLabel pointLabel;
    
    private final static int TABLE_WIDTH = 330;
    private final static int TABLE_HEIGHT = 290;
    
    public PointPanel(TabPane tabPane, final GUI gui, Controller controller){
        this.tabPane = tabPane;
        this.gui = gui;
        this.controller = controller;
        
        //must build point label first so we can make vault manager, then add other stuff
        pointLabel = new JLabel(ImageManager.createImageIcon("coins-large.png"), SwingConstants.CENTER);
        //label.setFont(FontManager.LARGER_PREFERRED_FONT);
        pointLabel.setFont(FontManager.createFont(28,"Agency FB", "Georgia")); //choose cool fonts that look like LCD displays     
        add(pointLabel);      
        
        this.vaultManager = controller.createVaultManager(pointLabel);
        vaultManager.updatePointLabel(controller.getPoints());
        
        //add button that opens achievements table
        JButton achievementButton = new JButton("How to earn points", ImageManager.createImageIcon("coins-plus.png"));
        achievementButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                JPanel panel = new JPanel();
                JScrollPane tablePane = new JScrollPane(createAchievementTable());
                tablePane.setPreferredSize(new Dimension(TABLE_WIDTH, TABLE_HEIGHT));
                panel.add(tablePane);
                JDialog dialog = Utils.putPanelInDialog(panel, gui.getFrame(), "How to earn points", "coins-plus.png", -1, -1);
                dialog.setVisible(true);
            }
        });
        add(achievementButton);
        
        add(vaultManager.getPanel());              

        //refresh();
    }
    
    /**
     * Resets the contents of this panel.
     */
    public void refresh(){
        //this.removeAll(); 
        

        
        //add achievements table
        /*JScrollPane tablePane = new JScrollPane(createAchievementTable());
        tablePane.setPreferredSize(new Dimension(TABLE_WIDTH, TABLE_HEIGHT));
        add(tablePane);    */    
    }
    
    /**
     * Creates a JTable where you can see what you can do to earn points.
     * @return a JTable
     */
    private JTable createAchievementTable(){
        JTable table;
        
        String[] columnNames = {
            "",
            "Coins",
            "How you earn the coins"
        };
        
        //fill each row with achievement information
        ArrayList<Activity> values = Activity.getNonSecretActivities();
        //determine how many non-secret activities there are
        int numActivities = 0;
        for(Activity activity : values){
            numActivities++;
        }
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
        table.getColumnModel().getColumn(0).setCellRenderer(new ImageRenderer());
        
        //specialize width of column
        javax.swing.table.TableColumn column = null;
        for (int i = 0; i < columnNames.length; i++) {
            column = table.getColumnModel().getColumn(i);
            switch(i){
                case 0:
                    column.setPreferredWidth(20);
                    break;
                case 1:
                    column.setPreferredWidth(50);
                    break;
                case 2:
                    column.setPreferredWidth(300);
                    break;
            }
        }
        
        table.setRowHeight(25);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.setFillsViewportHeight(true);
        return table;
    }
    
    /**
     * Convenience method for creating an achievement for the achievement table.
     * @param activity the Activity that we will make a row for
     * @return the Object[] representing the row.
     */
    private Object[] createActivityRow(Activity activity){
        PointEnums.Coin coin = PointEnums.Coin.getCoin(activity.getPoints());
        return new Object[]{
            ImageManager.createImageIcon(coin.getImageName()),
            activity.getPoints(),
            activity.getDescription()
        };
    }
    
    /**
     * For rendering images in a table.
     */
    class ImageRenderer extends DefaultTableCellRenderer {
      JLabel label = new JLabel();

      @Override
      public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
        boolean hasFocus, int row, int column) {
        label.setIcon((ImageIcon)value);
        return label;
      }    
    }
    
    @Override
        public void paintComponent(Graphics g){
            super.paintComponent(g);

            Utils.drawEmblem(this, g);
        }        
}

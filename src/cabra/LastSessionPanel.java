/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cabra;

import javax.swing.*;
import java.awt.*;
import javax.swing.table.*;
import static cabra.KnowPanel.Choices;

    /**Shows stats on the previous session.
     *
     */
public class LastSessionPanel extends JPanel{  
    
    public static final int MY_WIDTH = 300;
    public static final int MY_HEIGHT =200;
    
    private Session lastSession;

    public LastSessionPanel(Session lastSession){
        this.lastSession = lastSession;

        setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
        createPanel();
        //setPreferredSize(new java.awt.Dimension(MY_WIDTH,MY_HEIGHT));
    }

    private void createPanel(){
        //get the session's data and unpack it
        int[] data = lastSession.getCardStats();
        int learned = data[0];
        int notLearned = data[1];
        int sortOf = data[2];
        int skipped = data[3];
        int total = Utils.arraySum(data);

        JTable stats = createResultTable();
        JScrollPane scroller = new JScrollPane(stats);
        scroller.setPreferredSize(new Dimension(MY_WIDTH - 10, 128));
        add(scroller);

        //this bar graph gives a visual representation of how they did
        StackedBarGraph graph = new StackedBarGraph(Choices.YES.getColor(),
                                                    Choices.NO.getColor(),
                                                    Choices.SORT_OF.getColor(),
                                                    Choices.SKIPPED.getColor());
        graph.setValues(learned,notLearned,sortOf,skipped,total);
        add(graph);

        setBorder(BorderFactory.createLineBorder(Color.black));
    }
    
    /** Makes some text for the results (you knew x cards).
     * 
     * @param number the number of cards you knew/didn't know/whatever
     * @param total the total number of cards in the deck
     * @param verb what to say (knew, didn't know, etc)
     * @return the text to add to the panel
     */
    private static String makeResultText(int number, int total, String verb){
        //return verb + ": " + number + " cards (" + Utils.toPercent(number,total) + ")<br>";
        return "<li>You " + verb + " <b>" + number + "</b> flashcards (<b>"
                + Utils.toPercent(number,total) + "</b>.)";
    }

    /**
     * Creates a JTable where the user sees how many cards were right, wrong, etc.
     * @return a JTable
     */
    private JTable createResultTable(){
        JTable table;
        
        String[] columnNames = {
            "Result",
            "# cards",
            "Percent"
        };
        
        final int numResults = Choices.values().length;
        int[] results = lastSession.getCardStats();
        int total = Utils.arraySum(results);        
        
        //lookup "table" corresponding to results array
        String[] statDescriptions = {
            "You knew",
            "You didn't know",
            "You sort of knew",
            "You skipped"
        };
        
        Object[][] data = new Object[numResults + 1][columnNames.length]; //total row as well
        for(int i=0; i<results.length; i++){
            //the row contains information from the results array of the same index
            Object[] row = new Object[]{
                statDescriptions[i],
                results[i],
                Utils.toPercent(results[i], total)
            };
            data[i] = row;
        }
        
        //last row is total row
        data[numResults] = new Object[]{
            "<html><b>Total",
            "<html><b>" + total,
            ""
        };
        
        table = new JTable(data, columnNames){
            //make table read-only
            @Override
              public boolean isCellEditable(int row, int column){
                return false;
              }
            };
        table.setDefaultRenderer(Object.class, new RowColorer());
        
        //specialize width of column
        javax.swing.table.TableColumn column = null;
        for (int i = 0; i < columnNames.length; i++) {
            column = table.getColumnModel().getColumn(i);
            switch(i){
                case 0:
                    column.setPreferredWidth(120);
                    break;
                case 1:
                    column.setPreferredWidth(83);
                    break;
                case 2:
                    column.setPreferredWidth(80);
                    break;
            }
        }
        
        table.setRowHeight(20);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.setFillsViewportHeight(true);
        return table;
    }    
    
    private class RowColorer extends DefaultTableCellRenderer{
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column){
            Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            
            //prevent text from becoming white on click
            comp.setForeground(Color.black);
            
            //set background color
            Color color = null;
            switch(row){
                case 0:
                    //knew
                    color = Choices.YES.getColor();
                    break;
                case 1:
                    //did not know
                    color = Choices.NO.getColor();
                    break;
                case 2:
                    //sort of knew
                    color = Choices.SORT_OF.getColor();
                    break;
                case 3:
                    //skipped
                    color = Choices.SKIPPED.getColor();
                    break;
                case 4:
                    //total
                    color = Color.WHITE;
                    break;
            }
            final int opacity = 64;
            color = ColorManager.translucent(color, opacity);
            comp.setBackground(color);
            
            return comp;
        }
    }
    
    /** A panel with a stacked bar graph that shows the amounts of cards that had each status.
     * 
     */
    public static class StackedBarGraph extends JPanel{

        /** The 4 colors used in the bars
         * 
         */
        private Color[] colors;
        private static int NUM_COLORS;

        /** The percentages taken up by each bar bit.
         * 
         */
        private int[] values;

        public StackedBarGraph(Color learned,Color notLearned,Color sortOf, Color skipped){
            this.colors = new Color[] {learned,notLearned,sortOf,skipped};
            this.NUM_COLORS = colors.length;
            //setPreferredSize();
        }

        /** Takes some raw data and sets the values with that.
         * 
         * @param learned
         * @param notLearned
         * @param sortOf
         * @param skipped
         * @param total the total number of cards
         */
        public void setValues(int learned, int notLearned, int sortOf, int skipped, int total){
            this.values = new int[] {
                Utils.percent(learned,total), 
                Utils.percent(notLearned,total),
                Utils.percent(sortOf,total),
                Utils.percent(skipped,total)
            };
        }

        @Override
        public void paintComponent(java.awt.Graphics g){
            super.paintComponent(g);

            int beginHere = 0; //pixels to start painting from, from the top left (horizontally)
            //draw each bit of the bar
            //3 is the number of values/colors
            for(int i=0;i<NUM_COLORS;i++){
                g.setColor(colors[i]);
                //using the value (a percent), draw some of the bar
                int pixelsToDraw = (int)((values[i] / 100.0) * this.getWidth()); //the total width to draw
                if((i == NUM_COLORS - 1 && values[i] > 0) || 
                   (i == NUM_COLORS - 2 && values[i] > 0 && values[i+1] == 0) ||
                   (i == NUM_COLORS - 3 && values[i] > 0 && values[i+1] == 0 && values[i+2] == 0)){
                    //make sure we fill all the way, since this is the last bar
                    pixelsToDraw = this.getWidth();
                }

                g.fillRect(beginHere,0,pixelsToDraw,this.getHeight());

                beginHere += pixelsToDraw; //so now you start at the end of where we left off
            }
        }
    }
}
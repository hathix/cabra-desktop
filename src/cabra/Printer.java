/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cabra;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.ArrayList;
import java.text.MessageFormat;
import java.awt.print.PrinterException;

/** Provides the ability to print cards or notes of a project.
 *
 * @author Neel
 */
public class Printer extends Object{
    
        //constants
        public static final int ROW_HEIGHT = 90; //height of each row
        public static final int IMAGE_WIDTH = 60; //for now the image is resized square    
    
    private Printer(){}
    
    /** Prints all the notes of a given project.
     * 
     * @param project the active project.
     */
    public static void printNotes(final Project project){
        
        //a table to hold the text panes
        JTable table = new JTable();
        
        //get each note
        for(Note note : project.getNotes()){
            //create a text pane and put the note in it
            JTextPane textPane = new JTextPane();
            textPane.setDocument(note.getDocument());
            
            table.add(textPane);
        }
        
        //do print
        try {
            boolean complete = table.print( JTable.PrintMode.FIT_WIDTH,
                                            new MessageFormat(project.getName()), //MessageFormat header
                                            new MessageFormat("Cabra - Page {0}"), //MessageFormat footer
                                            true, //show printing dialog
                                            null, //PrintRequestAttributeSet
                                            true, //interactive
                                            null  //PrintService
                                            );
            if (complete) {
               //done

            } else {
                // printing was cancelled 

            }
        } catch (PrinterException pe) {
            // Printing failed, report to the user
            System.out.println(pe);
        }        
    }
    
    /** Prints the given list of cards (from a project, maybe?)
     * 
     * @param project the project that owns the cards
     * @param cards the cards to print //we're not taking directly from the project since we want filters possible
     */
    public static void print(final Project project, final ArrayList<Card> cards){
        SwingUtilities.invokeLater(new Runnable(){
            public void run(){
                Printer.realPrint(project,cards);
            }
        });
    }
    
    /** This does the real work, but print() lets this run in its own thread.
     * 
     * @param project the project that owns the cards
     * @param cards the cards to print
     */
    private static void realPrint(final Project project, final ArrayList<Card> cards){
        
        //this contains each and every card with its contents
        Object[][] contents = new Object[cards.size()][3]; //cards down, image/question/answer acrs
        for(int i=0;i<cards.size();i++){
            Card card = cards.get(i); 
            //extract card's contents and add to the contents
            if(card.getPictureName().equals(Card.NO_PICTURE_STRING)){
                //no picture, so leave the image box empty
                contents[i][0] = null;
            }
              else{
                //there IS a picture, so put it in here
                contents[i][0] = project.getPathTo(card.getPictureName());
            }
            
            //<html> forces line wrap
            final String inFront = "<html>"; 
            contents[i][1] = inFront + java.util.regex.Matcher.quoteReplacement(card.getQuestion()).
                    replaceAll(Card.NEWLINE,"<br>");
            contents[i][2] = inFront + java.util.regex.Matcher.quoteReplacement(card.getAnswer()).
                    replaceAll(Card.NEWLINE,"<br>");
        }
        
        //create table
        final JTable table = new JTable(new CardModel(contents));       
        //table = new JTable(contents, new Object[]{"Image","Question","Answer"}); 
       
        table.setShowGrid(true);
        table.setRowHeight(ROW_HEIGHT);
        //table.setFillsViewportHeight(true);

        //edit image cols 
        table.getColumn("Image").setMinWidth(IMAGE_WIDTH); //first column is image column; make it as big as an image
        table.getColumn("Image").setMaxWidth(IMAGE_WIDTH); 
        table.getColumn("Image").setCellRenderer(new ImageRenderer()); 
        
        table.getColumn("Question").setCellRenderer(new CenterRenderer());
        table.getColumn("Answer").setCellRenderer(new CenterRenderer());
                     
        //print preview
        final JDialog dialog = new JDialog((JFrame)null,"Preview of " + project.getName());
        dialog.setIconImage(GUI.createImageIcon("printer.png").getImage());
        dialog.setContentPane(new JScrollPane(table));
        dialog.setSize(500,500);
        Utils.changeFrameLocation(dialog,150,150);
        dialog.setVisible(true);
        
        //these don't work
        //dialog.repaint();
        //dialog.validate();

        SwingUtilities.invokeLater(new Runnable(){public void run(){
        //do print
        try {
            boolean complete = table.print( JTable.PrintMode.FIT_WIDTH, //printing style
                                            new MessageFormat(project.getName()), //MessageFormat header
                                            new MessageFormat("Cabra - Page {0}"), //MessageFormat footer
                                            true, //show printing dialog
                                            null, //PrintRequestAttributeSet
                                            true, //interactive
                                            null  //PrintService
                                            );
            //boolean complete = table.print();
            if (complete) {
               //done

            } else {
                // printing was cancelled

            }
        } catch (PrinterException pe) {
            // Printing failed, report to the user
            System.out.println(pe);
        }
        finally{
            dialog.setVisible(false);
        }
        }});
    }
    
    private static class CardModel implements TableModel {
        private Object[][] DATA; //card information here
        
        public CardModel(Object[][] data){
            this.DATA = data;
        }

        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {}
        public void addTableModelListener(javax.swing.event.TableModelListener l) {}
        public void removeTableModelListener(javax.swing.event.TableModelListener l) {}

        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return false;
        }

        public Class<?> getColumnClass(int col) {
            switch(col) {
                case 0:
                    return ImageIcon.class;
                case 1:
                case 2:
                    return String.class;
            }

            throw new AssertionError("invalid column");
        }
        
        public int getRowCount() {
            return DATA.length;
        }
        
        public int getColumnCount() {
            return 3;
        }
        
        public String getColumnName(int col) {
            switch(col) {
                case 0: return "Image";
                case 1: return "Question";
                case 2: return "Answer";
            }
            
            throw new AssertionError("invalid column");
        }
        
        public Object getValueAt(int row, int col) {
            switch(col) {
                case 0:
                case 1:
                case 2:
                    return DATA[row][col];
            }

            throw new AssertionError("invalid column");
        }
    }    
    
    /**
     * A custom cell renderer for rendering images.
     */
    private static class ImageRenderer extends DefaultTableCellRenderer {
            public Component getTableCellRendererComponent(JTable table,
                                                           Object value,
                                                           boolean isSelected,
                                                           boolean hasFocus,
                                                           int row,
                                                           int column) {

            super.getTableCellRendererComponent(table, value, isSelected,
                                                hasFocus, row, column);
            
            if(getText().equals("") == false){
                //only do this if there is an image here
                setHorizontalAlignment(SwingConstants.CENTER);

                //use the text (which is full path to image) to make image
                ImageIcon fullSizeIcon = GUI.createImageIconFromFullPath(this.getText());
                ImageIcon resizedIcon = ImageManager.scaleImage(fullSizeIcon, IMAGE_WIDTH,IMAGE_WIDTH); 
                setIcon(resizedIcon);
            }
            else{
                //no image
                setIcon(null);
            }
            
            //clean up
            setText("");
            
            return this;
        }
    }
    
    /**
     * A custom cell renderer that centers text.
     */
    private static class CenterRenderer extends DefaultTableCellRenderer {
            public Component getTableCellRendererComponent(JTable table,
                                                           Object value,
                                                           boolean isSelected,
                                                           boolean hasFocus,
                                                           int row,
                                                           int column) {

            super.getTableCellRendererComponent(table, value, isSelected,
                                                hasFocus, row, column);
            
            //setHorizontalAlignment(SwingConstants.CENTER);
            //setVerticalAlignment(SwingConstants.CENTER);
            
            //while we're at it, set a nicer font
            setFont(FontManager.SMALLER_PREFERRED_FONT);
            
            return this;
        }
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cabra;

import java.awt.*;
import java.awt.event.*;
//import java.util.HashMap;

import javax.swing.*;
import javax.swing.text.*;
import javax.swing.event.*;
import javax.swing.undo.*;

/** A class embedded in the NotePanel for editing of notes
 *
 * @author Neel
 */
public final class NoteTextPane extends JTextPane{
    private StyledDocument doc;
    public static final String newline = "\n";
    //private HashMap<Object, Action> actions;

    //undo/redo helpers
    protected UndoAction undoAction;
    protected RedoAction redoAction;
    protected UndoManager undo = new UndoManager();
    
    //the panel containing this is stored here
    private NotePanel panel;
    
    //buttons added to the notePanel's toolbar... managed from in here
    private JButton undoButton;
    private JButton redoButton;
    
    //for sizing... these don't work at the moment
    public static final int PANE_WIDTH = 340;
    public static final int PANE_HEIGHT = 255;
    
    /** Call this when creating a note for the first time.
     * 
     */
    public NoteTextPane(NotePanel panel){
        this(new DefaultStyledDocument(),panel);
    }

    public NoteTextPane(StyledDocument noteDocument, NotePanel panel) {
        this.panel = panel;
        //Create the text pane and configure it.        
        this.setDocument(noteDocument); //load the document
        doc = (StyledDocument) this.getDocument();        
        setCaretPosition(0);
        setMargin(new Insets(5,5,5,5));
        setFont(FontManager.PREFERRED_FONT);
        
        //undoAction = new UndoAction();
        //redoAction = new RedoAction();    
        
        //add key shortcuts.
        addBindings();

        //Set up the menu bar.
        /*actions=createActionTable(textPane);
        JMenu editMenu = createEditMenu();
        JMenu styleMenu = createStyleMenu();
        JMenuBar mb = new JMenuBar();
        mb.add(editMenu);
        mb.add(styleMenu);
        setJMenuBar(mb);*/         

        //Put the initial text into the text pane.
        //initDocument();
        //setCaretPosition(0);

        //Start watching for undoable edits and caret changes.
        doc.addUndoableEditListener(new MyUndoableEditListener());
        //addCaretListener(new CaretListenerLabel);
        doc.addDocumentListener(new MyDocumentListener());
    }

    
    public UndoAction getUndoAction(){ return undoAction; }
    public RedoAction getRedoAction(){ return redoAction; }
    
    public void addUndoRedoButtons(NotePanel panel){
        //add undo/redo
        redoButton = panel.addToolbarButton(redoAction,"redo.png","Redo");
        undoButton = panel.addToolbarButton(undoAction,"undo.png","Undo");     
        
        //now that undo/redo are added, add bindings.
        //addBindings();        
        
        //set undo/redo buttons disabled by default
        undoAction.setEnabled(false);
        redoAction.setEnabled(false);
    }

    public void insertImageIcon(ImageIcon icon){   
        //how to insert a picture
        //define the style
        Style regular = doc.addStyle("regular", StyleContext.getDefaultStyleContext().
                        getStyle(StyleContext.DEFAULT_STYLE));
        Style style = doc.addStyle("icon", regular);
        StyleConstants.setAlignment(style, StyleConstants.ALIGN_CENTER);
        if (icon != null) {
            //resize it if necessary
            ImageIcon resizedIcon = ImageManager.scaleImage(icon, 250, 250);
            StyleConstants.setIcon(style, resizedIcon);
        }
        //use the style to add a picture
        try{
            doc.insertString(getCaretPosition(), " ",style);     
        }
        catch(BadLocationException ble){
            System.err.println("Error loading icon!");
        }        
    }
    
    //This one listens for edits that can be undone.
    protected class MyUndoableEditListener
                    implements UndoableEditListener {
        public void undoableEditHappened(UndoableEditEvent e) {
            //Remember the edit and update the menus.
            undo.addEdit(e.getEdit());
            undoAction.updateUndoState();
            redoAction.updateRedoState();
        }
    }

    //And this one listens for any changes to the document.
    protected class MyDocumentListener
                    implements DocumentListener {
        public void insertUpdate(DocumentEvent e) {
            displayEditInfo(e);
        }
        public void removeUpdate(DocumentEvent e) {
            displayEditInfo(e);
        }
        public void changedUpdate(DocumentEvent e) {
            displayEditInfo(e);
        }
        private void displayEditInfo(DocumentEvent e) {
            Document document = e.getDocument();
            /*int changeLength = e.getLength();
            changeLog.append(e.getType().toString() + ": " +
                changeLength + " character" +
                ((changeLength == 1) ? ". " : "s. ") +
                " Text length = " + document.getLength() +
                "." + newline);*/
        }
    }

    //Add a couple of standard key bindings
    protected void addBindings() {
        InputMap inputMap = getInputMap();

        //Ctrl-B to bold text
        KeyStroke key = KeyStroke.getKeyStroke(KeyEvent.VK_B, Event.CTRL_MASK);
        Action action = new StyledEditorKit.BoldAction();
        action.putValue(Action.NAME, "Bold");        
        inputMap.put(key, action);
        
        //Ctrl-I to italicize text
        key = KeyStroke.getKeyStroke(KeyEvent.VK_I, Event.CTRL_MASK);
        action = new StyledEditorKit.ItalicAction();
        action.putValue(Action.NAME, "Italic");        
        inputMap.put(key, action);      
        
        //Ctrl-U to underline text
        key = KeyStroke.getKeyStroke(KeyEvent.VK_U, Event.CTRL_MASK);
        action = new StyledEditorKit.UnderlineAction();
        action.putValue(Action.NAME, "Underline");        
        inputMap.put(key, action);
        
        //Ctrl-R to make text red
        key = KeyStroke.getKeyStroke(KeyEvent.VK_R, Event.CTRL_MASK);
        action = new StyledEditorKit.ForegroundAction("Red", Color.RED);
        action.putValue(Action.NAME, "Red");        
        inputMap.put(key, action);       
        
        //Ctrl-Up to increase size of text
        key = KeyStroke.getKeyStroke(KeyEvent.VK_UP, Event.CTRL_MASK); 
        action = new StyledEditorKit.FontSizeAction("14", 14);
        action.putValue(Action.NAME, "Big");        
        inputMap.put(key, action);        
        
        //Ctrl-Down to decrease size of text
        key = KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, Event.CTRL_MASK); 
        action = new StyledEditorKit.FontSizeAction("12", 12);
        action.putValue(Action.NAME, "Small");        
        inputMap.put(key, action);       
        
        //Ctrl-Z to undo
        key = KeyStroke.getKeyStroke(KeyEvent.VK_Z, Event.CTRL_MASK);
        undoAction = new UndoAction();
        undoAction.putValue(Action.NAME, "Undo");        
        inputMap.put(key, undoAction);
        
        //Ctrl-Y to redo
        key = KeyStroke.getKeyStroke(KeyEvent.VK_Y, Event.CTRL_MASK);
        redoAction = new RedoAction();
        redoAction.putValue(Action.NAME, "Redo");        
        inputMap.put(key, redoAction);
        
        //Ctrl-S to save
        key = KeyStroke.getKeyStroke(KeyEvent.VK_S, Event.CTRL_MASK);
        SaveAction saveAction = new SaveAction();
        redoAction.putValue(Action.NAME, "Save");        
        inputMap.put(key, saveAction);
    }
    
    class SaveAction extends AbstractAction{
        public SaveAction(){
            super("Save");
        }
        
        @Override
                public void actionPerformed(ActionEvent e){
                    //save the note
                    panel.saveNotes();
                }
    }

    class UndoAction extends AbstractAction {
        public UndoAction() {
            super("Undo");
            //setEnabled(false); call this once the buttons are made
        }
        
        @Override
            public void setEnabled(boolean b){
                //set enabled, but change the button's enabledness too
                super.setEnabled(b);
                //set button's enabledness
                undoButton.setEnabled(b);
                //change tool tip text
                if(undoButton.isEnabled()){
                    undoButton.setToolTipText("Undo");
                } else{
                    undoButton.setToolTipText("Can't undo");
                }
            }

        public void actionPerformed(ActionEvent e) {
            try {
                undo.undo();
            } catch (CannotUndoException ex) {
                System.out.println("Unable to undo: " + ex);
                ex.printStackTrace();
            }
            updateUndoState();
            redoAction.updateRedoState();
        }

        protected void updateUndoState() {
            if (undo.canUndo()) {
                setEnabled(true);
                putValue(Action.NAME, undo.getUndoPresentationName());
            } else {
                setEnabled(false);
                putValue(Action.NAME, "Undo");
            }
        }
    }

    class RedoAction extends AbstractAction {
        public RedoAction() {
            super("Redo");
            //setEnabled(false); call this once buttons are made
        }
       
        @Override
            public void setEnabled(boolean b){
                //set enabled, but change the button's enabledness too
                super.setEnabled(b);
                //set button's enabledness
                redoButton.setEnabled(b);
                //change tool tip text
                if(redoButton.isEnabled()){
                    redoButton.setToolTipText("Redo");
                } else{
                    redoButton.setToolTipText("Can't redo");
                }                
            }        

        public void actionPerformed(ActionEvent e) {
            try {
                undo.redo();
            } catch (CannotRedoException ex) {
                System.out.println("Unable to redo: " + ex);
                ex.printStackTrace();
            }
            updateRedoState();
            undoAction.updateUndoState();
        }

        protected void updateRedoState() {
            if (undo.canRedo()) {
                setEnabled(true);
                putValue(Action.NAME, undo.getRedoPresentationName());
            } else {
                setEnabled(false);
                putValue(Action.NAME, "Redo");
            }
        }
    } //end RedoAction
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cabra;

/**
 *
 * @author Neel
 */

import java.awt.*;
//import java.awt.font.*;
import java.util.ArrayList;
import java.util.Arrays;

public abstract class FontManager {
    //a convenience class for getting and managing fonts
    
    /**The font that most of Cabra tries to use. 14pt.
     * 
     */
    public static Font PREFERRED_FONT;
    
    /**The same as PREFERRED_FONT, except it's 2pt smaller.
     * 
     */
    public static Font SMALLER_PREFERRED_FONT;
    
    /**
     * The same as PREFERRED_FONT, except it's 2pt bigger.
     */
    public static Font LARGER_PREFERRED_FONT;
    
    /**
     * The array of preferred font sizes; one of these should be used.
     */
    public static final int[] GOOD_FONT_SIZES = { 12, 13, 14, 15, 16 };
    
    /**
     * The best and default font size.
     */
    public static final int DEFAULT_FONT_SIZE = 14;
    
    /**
     * The array of preferred font names; use createFont(GOOD_FONT_NAMES).
     */
    public static final String[] GOOD_FONT_NAMES = {
                "Ubuntu", //Ubuntu
                "Geneva", //Mac OS X
                "Calibri","Lao UI","Trebuchet MS", //Windows
                "Georgia","Arial","Verdana", //Everyone
                "Courier New" //Monospace
        };
    
    private static ArrayList<String> allFontNames;
    
    static{
        //set all font names available here
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        String[] fontNames = ge.getAvailableFontFamilyNames();

        allFontNames = new ArrayList<String>();
        allFontNames.addAll(Arrays.asList(fontNames));
        
        //set preferred font
        PREFERRED_FONT = createFont(
                DEFAULT_FONT_SIZE,
                GOOD_FONT_NAMES
                );
        SMALLER_PREFERRED_FONT = PREFERRED_FONT.deriveFont(12f); 
        LARGER_PREFERRED_FONT = PREFERRED_FONT.deriveFont(16f);
        
        //javax.swing.UIManager.put("defaultFont", PREFERRED_FONT);
    }
    
    /**
     * Updates the PREFERRED_FONT to the given parameters. You should only pass one. NOTE: you have to validate the frame after this
     * @param fontName the new font name/family. pass null if you don't want to change it.
     * @param fontSize the new size of the font. pass 0 if you don't want to change it.
     */
    public static void updatePreferredFont(String fontName, int fontSize){
        if(fontSize != 0){
            //update size
            PREFERRED_FONT = createFont(
                    fontSize,
                    PREFERRED_FONT.getFontName()
                );
            
            //change prefs
            UserData.setPref("FontSize", fontSize + "");
        }
        if(fontName != null){
            //update font name
            PREFERRED_FONT = createFont(
                    PREFERRED_FONT.getSize(),
                    fontName
                );            
            //change prefs
            UserData.setPref("FontName", fontName);
        }
        
        //change default font on look and feel
        //javax.swing.UIManager.getLookAndFeelDefaults().put("defaultFont", PREFERRED_FONT);
        
        //update smaller font; it's 2pt smaller than the preferred
        SMALLER_PREFERRED_FONT = PREFERRED_FONT.deriveFont(
                PREFERRED_FONT.getSize() - 2f);
        //update larger font; it's 2pt bigger than the preferred
        LARGER_PREFERRED_FONT = PREFERRED_FONT.deriveFont(
                PREFERRED_FONT.getSize() + 2f);        
    }
    
    /** Sees if the given font is available on this machine.
     * 
     * @param fontName the name of the font
     * @return true if the font is available, false if it isn't
     */
    
    public static boolean hasFontName(String fontName){
        return allFontNames.contains(fontName);
    }    
    
    /**
     * Returns an array containing all the available font names that are in the preferred list.
     * @return an array of Strings with the names of the fonts.
     */
    public static String[] getAvailablePreferredFontNames(){
        ArrayList<String> available = new ArrayList<String>();
        for(String fontName : GOOD_FONT_NAMES){
            //if it's available, add it to the list
            if(hasFontName(fontName))
                available.add(fontName);
        }
        //convert to String[]
        return available.toArray(new String[]{});
    }
    
    /**
     * Returns an array containing all the available font names.
     * @return an array of Strings with the names of the fonts.
     * @return 
     */
    public static String[] getAllAvailableFontNames(){
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        String[] fontNames = ge.getAvailableFontFamilyNames();
        return fontNames;
    }
    
    /**Attempts to get a suitable font name as specified by the caller.
     * 
     * @param fontNames the list of requested font names. If the first one isn't there, try the next.
     * @return the first font name that works
     */
    
    private static String getFirstAvailableFontName(String... fontNames){
        for(String fontName : fontNames){
            if(hasFontName(fontName)){
                //we're in luck
                return fontName;
            }
        }
        
        //no font works; use a good default (preferred works cause we know it's available)
        return PREFERRED_FONT.getFamily();
    }
    
    /**
     * Creates a font.
     * @param size the size, in pt, of the font.
     * @param style Font.NORMAL, Font.BOLD, Font.ITALIC, etc.
     * @param fontNames a list of font family names to try. The first available one will be used, so put preferred names first.
     * @return the generated font.
     */
    public static Font createFont(int size, int style, String... fontNames){
        String firstName = getFirstAvailableFontName(fontNames); //the first font name that works
        return new Font(firstName, style, size);
    }
    
    /** Same as createFont, except it defaults to plain style
     * 
     * @param size the size preferred
     * @param fontNames the names of the fonts you want
     * @return the created font
     */
    
    public static Font createFont(int size, String... fontNames){
        return createFont(size, Font.PLAIN, fontNames);
    }
    
    /** Finds a random installed font and returns its name
     * 
     * @return a random font name
     */
    
    public static String randomFontName(){
        int index = (int)(Math.random() * allFontNames.size()); //a random index in there
        return allFontNames.get(index);
    }   
    
}

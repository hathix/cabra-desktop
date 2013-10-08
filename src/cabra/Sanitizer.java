/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cabra;

/**Allows for sanitation of file names (certain characters are disallowed)
 *
 * @author Neel
 */
public abstract class Sanitizer {
    
    private Sanitizer(){}
    
    /**Marks which characters cannot be allowed in file names
     * 
     */
    public static final String DISALLOWED_CHARS = "\\/:?*.<>|\""; 
    
    /** Determines if a certain String has any disallowed chars in it
     * 
     * @param string the String to test
     * @return true if it has a disallowed char, false if not
     */
    
    public static boolean hasDisallowedChar(String string){
        //go through each char and see if it's disallowed
        for(char letter : string.toCharArray()){
            if(DISALLOWED_CHARS.indexOf(letter + "") != -1){
                //if the disallowed chars string contains this char, it's bad
                return true;
            }
        }
        
        return false;
    }
    
    /**Finds any disallowed chars in the string and removes them.
     * 
     * @param string the string to test
     * @return the sanitized string
     */
    
    public static String sanitize(String string){
        if(hasDisallowedChar(string) == false)
            return string; //nothing to change
        StringBuilder builder = new StringBuilder(string);
        for(int i=0;i<builder.length();i++){
            char letter = builder.charAt(i);
            if(DISALLOWED_CHARS.indexOf(letter + "") != -1){
                //this char is disallowed
                builder = builder.deleteCharAt(i);
                i--;
            }
        }
        
        return builder.toString();
    }
    
    
    /**Replaces any spaces in the string with underscores, i.e. "Forty two" becomes "Forty_two"
     * 
     * @param string the string to remove the spaces from
     * @return the new string
     */
    
    public static String removeSpaces(String string){
        return string.replaceAll(" ","_");
    }
    
    /** Replaces underscores in the string with spaces, i.e. "Forty_two" becomes "Forty two"
     * 
     * @param string the string to remove the underscores from
     * @return the new string
     */
    
    public static String removeUnderscores(String string){
        return string.replaceAll("_"," ");
    }
}

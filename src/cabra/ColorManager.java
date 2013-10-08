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

public abstract class ColorManager extends Object{
    //a class with a bunch of static methods for making colors less of a hassle
    
    //public static final int EXTREMELY_DARK_THRESHOLD = 512;
    
     /** Takes a hex char like 'F' or '3' and turns it into an int like 15 or 3
     * 
     * @param hex the char to turn into an int
     * @return the char's int value
     */
     private static int hexCharToInt(char hex){
            switch(hex){
                case 'A':
                    return 10;
                case 'B':
                    return 11;
                case 'C':
                    return 12;
                case 'D':
                    return 13;
                case 'E':
                    return 14;
                case 'F':
                    return 15;
                default:
                    return Integer.parseInt(String.valueOf(hex)); //if it's '8', make it into "8" and return 8
            }
        }
     
     /** Converts an integer like 15 to 'F' or '3' to 3
      * 
      * @param decimal the number, less than 16
      * @return the hex, like 'F' or '3'
      */
     private static char intToHexChar(int decimal){
         switch(decimal){
             case 15:
                 return 'F';
             case 14:
                 return 'E';
             case 13:
                 return 'D';
             case 12:
                 return 'C';
             case 11:
                 return 'B';
             case 10:
                 return 'A';
             default:
                 //turn the number into a string and get the first char
                 return (decimal + "").charAt(0);
         }
     }
     
     /** turns the given string into a color: 000000 returns the color black
      * 
      * @param hex the 6-char-long string to turn into a color
      * @return the created color
      */
     public static Color createColor(String hex){
         //check for the right kind of string
         if(hex.length() != 6){
             throw new IllegalArgumentException("Wrong length hex value!");
         }
         
         hex = hex.toUpperCase(java.util.Locale.ENGLISH); //makes the string uppercase, but in english to prevent weird symbols from showing up
         
         int[] rgb = new int[3]; //the values for the color will be stored here
         
         for(int i=0;i<3;i++){
             char leftHexChar = hex.charAt(i*2); //the left hex char could be 0,2, or 4
             char rightHexChar = hex.charAt(i*2+1); //the right hex char could be 1,3, or 5
             int intValue = 16 * hexCharToInt(leftHexChar) + 1 * hexCharToInt(rightHexChar); //turns the two hex chars into an int; e.g. FF -> 16*15 + 1*15 = 255
             rgb[i] = intValue;
         }
         
         //now the int values for the colors are all there
         
         return new Color(rgb[0],rgb[1],rgb[2]);
     }
     
     /**
      * Returns a translucent version of the original color.
      * @param original the original Color
      * @param alpha a value from 0 (transparent) - 255 (opaque)
      * @return the translucent color.
      */
     public static Color translucent(Color original, int alpha){
         int[] rgb = rgbValues(original);
         /*for(int i=0; i<rgb.length; i++){
             int newValue = rgb[i] + alpha;
             if(newValue > 255) newValue = 255;
             rgb[i] = newValue;
         }*/
         return new Color(rgb[0], rgb[1], rgb[2], alpha);   
     }
     
     /** Converts the given color into its hex equivalent.
      * 
      * @param color the color, like Color. red
      * @return the hex equivalent, like FF0000
      */
     public static String toHex(Color color){
         //get the decimal amounts of each color
         int[] rgbValues = rgbValues(color);
         
         //since rgbValues can have up to 255, split that up into 16 and 15 and make that into hex
         String red = new String(new char[]{intToHexChar(rgbValues[0]/16),intToHexChar(rgbValues[0]%16)});
         String green = new String(new char[]{intToHexChar(rgbValues[1]/16),intToHexChar(rgbValues[1]%16)});
         String blue = new String(new char[]{intToHexChar(rgbValues[2]/16),intToHexChar(rgbValues[2]%16)});
         
         return red + green + blue;
     }
     
     /** Finds the amount of red, green, and blue in a given color.
      * 
      * @param color the color to find the rgb of
      * @return an array of ints: [red,green,blue]
      */
     public static int[] rgbValues(Color color){
         int[] values = {color.getRed(),color.getGreen(),color.getBlue()};
         return values;
     }
     
     /** Finds the sum of all the RGB values of a color
      * 
      * @param color the color to find the value sum of
      * @return the sum of the values
      */
     
     public static int sumOfrgbValues(Color color){
         int[] values = rgbValues(color);
         int total = 0;
         for(int rgb : values)
             total += rgb;
         return total;
     }
     
     /** creates a completely random color
      * 
      * @return the random color
      */
     public static Color randomColor(){
         int r = (int)(Math.random() * 256);
         int g = (int)(Math.random() * 256);
         int b = (int)(Math.random() * 256);
         return new Color(r,g,b);
     }
     
     
     /** Determines if the given color is too extreme (too bright or too skewed.)
      * 
      * @param color the color to test
      * @return true if the color is too extreme, false otherwise
      */
    /* public static boolean isColorTooDark(Color color){
         return sumOfrgbValues(color) <= EXTREMELY_DARK_THRESHOLD; //if the color is white, you get 256 * 3 which returns true.
     }*/
     
     //public static boolean i
     
     /** Creates a random color but ensures that it isn't extreme (too bright/skewed.)
      * 
      * @return the created color.
      */
     
     /*public static Color randomColorNoExtremes(){
         Color color = Color.white; //this will fail the isColorTooExtreme test
         while(isColorToo(color)){
             color = randomColor();
         }
         return color;
     }*/
}

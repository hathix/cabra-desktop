/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cabra;

import java.awt.Font;
import java.io.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.LinkedList;

/**An uninstantiable class for user info
 * 
 * @author Neel
 */
public final class UserData {
    
    private UserData(){} //can't be instantiated
    
    private static LinkedHashMap<String, Datum<String>> Data = new LinkedHashMap<String, Datum<String>>();
    static{
        //initialize Data map
        //Current theme
        Data.put("Theme",new Datum<String>(
                "Theme",
                null,
                "Default"
                ));
        //Active project
        Data.put("Project",new Datum<String>(
                "Project",
                null,
                "*none*"
                ));
        //Points
        Data.put("Points",new Datum<String>(
                "Points",
                null,
                "0"
                )); 
        //Prizes they've bought from the store
        Data.put("PrizesBought",new Datum<String>(
                "PrizesBought",
                null,
                "[]"
                ));
        //Redeemed codes
        Data.put("Codes",new Datum<String>(
                "RedeemedCodes",
                null,
                " "
                ));
        //Last version used
        Data.put("Version",new Datum<String>(
                "LastVersion",
                null,
                "archaic"
                )); 
        //last time they checked for updates
        Data.put("LastUpdateCheck",new Datum<String>(
                "LastUpdateCheck",
                null,
                Calendar.getInstance().getTimeInMillis() + ""
                ));
        
        /* PREFS */
        putPref("ProjectFolder", SaveLoad.DEF_PROJECT_FOLDER); //where projects are loaded rom
        putPref("FontSize", FontManager.DEFAULT_FONT_SIZE + ""); //14pt; font size
        putPref("FontName", FontManager.PREFERRED_FONT.getName()); //font name
        putPref("MaxSession", "50"); //max cards in a session
        putPref("UpdateInterval", "7"); //check for updates each this many days
        
        /*
        Data.put("Name",new Datum<String>(
                "Description",
                null,
                "Default"
                )); 
         */
    }
    
    /**
     * Puts a preference.
     * @param name  the raw name of the preference, such as FontSize
     * @param def   the default value of the preference.
     */
    private static void putPref(String name, String def){
        Data.put("Prefs." + name, new Datum<String>(
                "Prefs." + name,
                null,
                def
                ));
    }
    
    /**
     * UserData file has to be in the same place, else we don't know where to find it
     * So it'll always be in the same place
     */
    private static final File USER_DATA_FILE = new File(javax.swing.filechooser.FileSystemView.getFileSystemView().getDefaultDirectory().getAbsolutePath() + "/CabraProjects/UserData.txt");
    private static final String SEPARATOR = ":";
    
    /**
     * Returns the String data stored with the given key.
     * @param key the key associated with the data.
     * @return the text of the datum associated with the key.
     */
    public static String getString(String key){
        return Data.get(key).getData();
    }
    
    /**
     * Same as getString(), except it converts the string to an int for you.
     * @param key the key associated with the data
     * @return the int of the datum associated with the key
     */
    public static int getInt(String key){
        return Integer.parseInt(getString(key));
    }
    
    /**
     * Sets the data for the datum at the given key.
     * @param key the key with which to find the datum
     * @param value the value to give the new datum.
     * @throws IllegalArgumentException if it can't find the key you passed
     */
    public static void setString(String key, String value){
        if(Data.containsKey(key)){
            Data.get(key).setData(value,true);
        }
        else{
            throw new IllegalArgumentException();
        }
    }

    /**
     * Makes the datum at the given key revert to its default data value.
     * @param key the key with which to get a datum and set its data to default.
     */
    public static void makeDefault(String key){
        Data.get(key).makeDefault();
    }
    
    /**
     * Gets the String data from the given preference. Just a convenience wrapper around getString().
     * @param pref the preference's name, such as FontSize. Technically the Datum is Prefs.FontSize; this is a wrapper.
     */
    public static String getPref(String pref){
        return getString("Prefs." + pref);
    }
    
    /**
     * Returns the int data from the given preference.
     * @param pref the preference's name, such as FontSize.
     * @return the preference's value as an int
     */
    public static int getIntPref(String pref){
        return Integer.parseInt(getPref(pref));
    }
  
    /**
     * Sets the data for the preference at the given key.
     * @param pref the pref's key; "Prefs." is added to the front
     * @param value the value for the preference
     */    
    public static void setPref(String pref, String value){
        setString("Prefs." + pref, value);
    }
    
    /**
     * Resets all user data to its default settings. Only use this if all data is corrupted.
     */
    public static void makeAllDefault(){
        for(Datum datum : Data.values()){
            //reset it
            datum.makeDefault();
        }
    }
    
    /** Loads user data.
     * 
     */
    public static void load(){
        BufferedReader reader = null;
        try{
            if(USER_DATA_FILE.exists() == false){
                //System.out.println("USER DATA DOES NOT EXIST");
                USER_DATA_FILE.createNewFile();
                
                makeAllDefault();
                
                return;
            }
            reader = new BufferedReader(new FileReader(USER_DATA_FILE));
            
            //get all data
            ArrayList<StringPair> stringPairs = new ArrayList<StringPair>();
            StringPair readPair = null;
            while((readPair = readLine(reader)) != null){
                stringPairs.add(readPair);
            }
            
            //determine whose data is whose
            for(StringPair pair : stringPairs){
                for(Datum<String> datum : Data.values()){
                    if(datum.description.equals(pair.description)){
                        //descriptions match; data belongs to this datum
                        datum.setData(pair.data, false);
                    }
                }
            }
            
            //set values for each datum
            /*for(Datum<String> datum : Data.values()){
                datum.setData(readLine(reader),true);
            }*/            
        }
        catch(IOException io){
            Utils.debug(io, "Error reading user data");
            //set default values for all
            makeAllDefault();
        }
        finally{
            if(reader != null){
                try{
                    reader.close();
                }
                catch(IOException io){
                    Utils.debug(io, "Error closing input stream in file reader");
                }
            }
            save();
        }
    } 
    
    /** Saves the user data to the text file.
     * 
     */
    private static void save(){
        //save in background... the info stored here doesn't matter to us until we restart the program
        new Thread(new Runnable(){
            public void run(){
                try{
                    if(USER_DATA_FILE.exists() == false){
                        //make the file
                        USER_DATA_FILE.createNewFile();
                    }
                    BufferedWriter writer = new BufferedWriter(new FileWriter(USER_DATA_FILE));

                    //write each datum
                    for(Datum<String> datum : Data.values()){
                        writeLine(writer,datum);
                    }

                    //clean up
                    writer.close();
                }
                catch(IOException io){
                    Utils.debug(io, "Error saving user data");
                }
            }
        }).start();
    }
    
    /**
     * Returns the user data text at the given line (not the whole line.)
     * @param reader a BufferedReader object that will be doing the reading
     * @return the StringPair read: description and data. 
     */
    private static StringPair readLine(BufferedReader reader){
        String line = "";
        try{
            line = reader.readLine();
        }
        catch(IOException io){

        }
        if(line == null){
            return null;
        }
        else{
            return new StringPair(line.split(SEPARATOR, 2)); //split it into max 2 segments
        }
    }
    
    /**
     * Writes a line of user data.
     * @param writer a BufferedWriter that will be writing
     * @param datum a datum of any kind
     */
    public static <E> void writeLine(BufferedWriter writer, Datum<E> datum){
        try{
            writer.write(datum.toString());
            writer.newLine();
        }
        catch(IOException io){

        }
    }
   
    /**
     * Represents one piece of user data. It's generic, too!
     */
    static final class Datum<E>{
        
        private String description;
        private E data;
        private E defaultData;
        
        /**
         * Creates a new piece of data.
         * @param description a description of the data
         * @param data any piece of data. May be null.
         * @param defaultData the data that will be set as "data" if the passed data is null.
         */
        public Datum(String description, E data, E defaultData){
            this.description = description;
            this.defaultData = defaultData;
            this.setData(data, false); //saving prevents reading later
        }
        
        public void setData(E e, boolean shouldSave){
            if(e == null){
                //no data, use default
                this.data = defaultData;
            }
            else{
                this.data = e;
            }
            
            if(shouldSave)
                UserData.save();
        }
        
        /**
         * Sets the default value for "data".
         */
        public void makeDefault(){
            setData(null, true);
        }
                        
        public E getData(){
            return data;
        }
        
        public String getDescription(){
            return description;
        }
        
        @Override
        public String toString(){
            return description + SEPARATOR + data.toString();
        }
    }
  
    /**
     * Represents the pair of strings read from the data file: description and data.
     */
    static final class StringPair{
        
        public String description;
        public String data;
        
        public StringPair(String description, String data){
            this.description = description;
            this.data = data;
        }
        
        public StringPair(String[] array){
            this(array[0], array[1]);
        }
    }
        
}

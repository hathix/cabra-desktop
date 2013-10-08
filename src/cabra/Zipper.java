/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cabra;

import java.io.*;
import java.util.zip.*;
//import java.util.ArrayList;

/**Provides utility methods for zipping and unzipping files.
 *
 * @author Neel
 */
public final class Zipper extends Object{
    
    private Zipper(){} //can't be instantiated
    
   public static final int BUFFER_SIZE = 2048; //size of buffer, in bytes
   public static final String EXTENSION = "cproj";
   
   /** Unzips the given .zip file into a folder.
    * 
    * @param fileToUnzip the zipped file
    * @param folderToWriteTo the location to unzip to (must be a folder.) Contents of the zipped file are placed inside this folder.
    */
   
   public static void unzip(File fileToUnzip,File folderToWriteTo) {
      //ArrayList<File> unzipped = new ArrayList<File>(); //holds the paths to the files that get written
       if(folderToWriteTo.isFile()){
           throw new IllegalArgumentException("Can't unzip into a file!");
       }
       if(folderToWriteTo.exists() == false){
           folderToWriteTo.mkdir();
       }
      try {
         BufferedOutputStream dest = null;
         ZipInputStream zis = new ZipInputStream(new BufferedInputStream(new FileInputStream(fileToUnzip)));
         ZipEntry entry = null;
         while((entry = zis.getNextEntry()) != null) {
            //System.out.println("Extracting: " +entry);
            int count;
            byte[] data = new byte[BUFFER_SIZE];
            // add files to folder
            File content = new File(folderToWriteTo.getAbsolutePath() + "/" + entry.getName());
            dest = new BufferedOutputStream(new FileOutputStream(content), BUFFER_SIZE);
            while ((count = zis.read(data, 0, BUFFER_SIZE)) 
              != -1) {
               //read each byte
               dest.write(data, 0, count);
            }
            
            //unzipped.add(content);
            
            dest.flush();
            dest.close();
         }
         zis.close();
      } catch(Exception e) {
         e.printStackTrace();
      }
      
      //return unzipped;
   }
   
   /** Zips up the given folder's contents into the given .zip file.
    * 
    * @param folderToZip the folder containing the files to be zipped. Its contents will be zipped into zipTo
    * @param zipTo the .zip file that the files should be zipped into 
    * @param gui the GUI (used for its frame)
    * @return the zipped file
    */
   
  public static File zip(File folderToZip, File zipTo,GUI gui){
       if(folderToZip.isFile()){
           throw new IllegalArgumentException("Can only zip folders!");
       }
      
      try {
         BufferedInputStream origin = null;
         
         //create a .zip file inside the zipTo folder
         String projectName = folderToZip.getName();
         File zip = new File(zipTo.getAbsolutePath() + "/" + projectName + "." + EXTENSION);
         
         //does it exist? if so, ask for confimation since we could overwrite
         if(zip.exists()){
             if(!InputManager.confirm("<html><center>A file named <i>" + zip.getAbsolutePath() + "</i><br />already exists. Do you want to replace it?",gui.getFrame()))
                 return null;
         }
         
         //and have the output stream write to the zip file
         ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(zip)));
         //out.setMethod(ZipOutputStream.DEFLATED);
         byte[] data = new byte[BUFFER_SIZE];
         
         // get a list of files from given folder
         String[] files = folderToZip.list();

         for (int i=0; i<files.length; i++) {
             File file = new File(files[i]);
             String absolutePath = folderToZip.getAbsolutePath() + "/" + file.getName();
             File newLocation = new File(absolutePath);
            origin = new BufferedInputStream(new FileInputStream(newLocation), BUFFER_SIZE);
            ZipEntry entry = new ZipEntry(files[i]);
            out.putNextEntry(entry);
            int count;
            while((count = origin.read(data, 0, 
              BUFFER_SIZE)) != -1) {
               out.write(data, 0, count);
            }
            origin.close();
         }
         out.close();
         return zip;
      } catch(Exception e) {
         System.err.println("Error zipping project! Details: ");
         e.printStackTrace();
         return null;
      }       
  }
}

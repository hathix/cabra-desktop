/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cabra;

import java.util.ArrayList;

/** Contains enums for points.
 *
 * @author Neel
 */
public class PointEnums {
    
    public enum Coin{
        BRONZE      ("coin-bronze.png",     1,  9),
        SILVER      ("coin-silver.png",     10, 24),
        GOLD        ("coin-gold.png",       25, 99),
        GOLDSTACK   ("coin-goldstack.png",  100,9999),
        ;
        
        private String imageName;
        private int minPoints;
        private int maxPoints;
        
        /** A coin is used to represent coin earnings.
         * Earnings of a certain range are associated with a certain coin.
         * 
         * @param imageName
         * @param minPoints
         * @param maxPoints 
         */
        Coin(String imageName, int minPoints, int maxPoints){
            this.imageName = imageName;
            this.minPoints = minPoints;
            this.maxPoints = maxPoints;
        }
        
        public String getImageName(){
            return imageName;
        }
        
        public javax.swing.ImageIcon getImageIcon(){
            return ImageManager.createImageIcon(imageName);
        }
        
        /**
         * Determines if earnings of a certain amount fall in this coin's range.
         * @param points how many points are earned
         * @return true if the earnings fall in this coin's range, false otherwise
         */
        public boolean isInRange(int points){
            return points >= minPoints && points <= maxPoints;
        }
        
        /**
         * Returns the coin appropriate for the given number of points.
         * @param points how many points are earned
         * @return the coin that fits for that point value.
         */
        public static Coin getCoin(int points){
            //just check if it's in the range of any coin
            for(Coin coin : Coin.values()){
                if(coin.isInRange(points))
                    return coin;
            }
            
            return Coin.GOLDSTACK;
        }
    }
    
    public enum Activity{
        STUDY_CORRECT   (1,     "Study a card and get it right"),
        CREATE_CARD     (2,     "Create a flashcard"),
        ADD_IMAGE       (3,     "Add an image to a flashcard"),
        CREATE_NOTE     (3,     "Create a note"),
        EXPORT_PROJECT  (5,     "Export a project"),
        RANK_E          (5,     "Get a card to rank E"),
        PRINT_CARDS     (10,    "Print out a project's flashcards"),
        CREATE_PROJECT  (10,    "Create a project"),
        //PERFECT_SESS_10 (15,    "Get all cards correct in a study session (min. 10)"),
        IMPORT_PROJECT  (20,    "Import a project"),
        //PERFECT_SESS_25 (40,    "Get all cards correct in a study session (min. 25)"),
        USE_NEW_VERSION (50,    "Use a new version of Cabra"),
        //PERFECT_SESS_50 (75,    "Get all cards correct in a study session (min. 50)"),
        USE_BETA        (100,   "Use a beta version of Cabra"), 
        GET_LUCKY       (100,   "Get lucky"),
     
        /* secret */
        CODE_FIRSTRUN   (20,    "???", true),
        CODE_HELP       (50,    "???", true),        
        CODE_SOCIAL     (100,   "???", true),        
        CODE_SECRET     (50,    "???", true),
        
        /* testing */
        ZERO            (0,     "0", true),
        ;
        
        private int points;
        private String description;
        private boolean secret;
        
        /**
         * An activity has a certain number of points allotted to it.
         * @param points how many points the activity is worth.
         * @param description a description of the achievement
         * @param secret true if the activity should not be shown on the "Points" tab.
         */
        Activity(int points, String description, boolean secret){
            this.points = points;
            this.description = description;
            this.secret = secret;
        }
        
        /**
         * Overload for Activity(int, String, boolean.) This activity is NOT secret.
         * @param points how many points the activity is worth.
         * @param description a description of the achievement
         */
        Activity(int points, String description){
            this(points, description, false);
        }
        
        public int getPoints(){
            return points;
        }
        
        public String getDescription(){
            return description;
        }
        
        public boolean isSecret(){
            return secret;
        }
        
        /**
         * Returns a list of all the activities that are not secret.
         * @return the list of activities
         */
        public static ArrayList<Activity> getNonSecretActivities(){
            ArrayList<Activity> list = new ArrayList<Activity>();
            for(Activity activity : values()){
                if(activity.isSecret() == false)
                    list.add(activity);
            }
            
            return list;
        }
    }
}

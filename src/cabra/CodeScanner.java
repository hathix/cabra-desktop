package cabra;

import cabra.PointEnums.Activity;

/** Takes codes entered by the user and takes action if they are valid.
 *
 * @author Neel
 */
public class CodeScanner {
    private CodeScanner(){}
    
    public static enum Codes{
       POINTS_FIRSTRUN("6FH3VR", Activity.CODE_FIRSTRUN),
       POINTS_HELP("3FB98N", Activity.CODE_HELP),
       POINTS_SOCIAL("G2CV18", Activity.CODE_SOCIAL),
       POINTS_SECRET("5L90YC", Activity.CODE_SECRET),
       POINTS_TEST("AAAAAA", Activity.ZERO),     
        
        /*
       TEMPLATE("AAAAAA", Activity.X),
        */  
        ;
        
        
        private String code;
        private Activity activity;
        Codes(String code, Activity activity){
            this.code = code;
            this.activity = activity;
        }
        
        /**
         * Checks the given code with this code; 
         * if the code matches the action associated with the code is activated.
         * 
         * @param toCheck the user's input string, all uppercase (preferably)
         * @param controller the controller object
         * @return the result of the check
         */
        public Result check(String toCheck, Controller controller){
            javax.swing.JFrame frame = controller.getGUI().getFrame();
            
            if(toCheck.equals(this.code) == false){
                //codes don't match
                //don't show dialog here; if we did, it would show even for a legit code
                return Result.WRONG_CODE;
            } 
            else if(this.isRedeemed()){
                //already redeemed code
                Utils.showDialog(frame,
                        "<html><center>Sorry, you already redeemed the code <i>" + toCheck + "</i>.",
                        "Already Redeemed Code");     
                return Result.ALREADY_REDEEMED;
            }
            else{
                //alert user
                int pointsEarned = this.activity.getPoints();
                Utils.showDialog(frame,
                        "<html><center>Congrats! Your code <i>" + toCheck + "</i> was successfully redeemed "
                        + "for <b>" + pointsEarned + "</b> points!",
                        "Code Redeemed");
                
                //earn the points
                controller.gainPoints(this.activity);
                
                //note that this code has already been used
                String existingData = UserData.getString("Codes");
                String dataToAdd = toCheck;
                UserData.setString("Codes", existingData + " " + dataToAdd);
                
                //load it in the web database (anonymous usage statistics)
                Utils.openURLinDialog("http://cabra.hathix.com/coderedeemed.php?code=" + toCheck, 
                        "", frame, false);
                
                return Result.SUCCESS;
            }
        }
        
        /**
         * Returns if this code has already been redeemed.
         * @return true if the code has been redeemed, false if it can be redeemed
         */
        public boolean isRedeemed(){
            String codesRedeemed = UserData.getString("Codes");
            if(codesRedeemed.contains(this.code))
                return true;
            else
                return false;
        }
        
        /**
         * Checks the given code against all codes to check if it matches any.
         * @param toCheck the user's entered code
         * @param controller the controller code
         */
        public static void checkAgainstAll(String toCheck, Controller controller){
            boolean alreadyRedeemed = false; //check if it has been redeemed
            
            for(Codes code : Codes.values()){
                Result result = code.check(toCheck, controller);
                if(result == Result.ALREADY_REDEEMED)
                    alreadyRedeemed = true;
                else if(result == Result.SUCCESS)
                    return;
                else if (result == Result.WRONG_CODE)
                    continue;
            }
            
            //nothing worked
            if(alreadyRedeemed == false){
                //it hasn't been redeemed; it was wrong across the board
                Utils.showDialog(controller.getGUI().getFrame(),
                    "<html><center>Sorry, your code <i>" + toCheck + "</i> was invalid.",
                    "Code Invalid");           
            }
        }
    }
    
    public static enum Result{
        SUCCESS,
        ALREADY_REDEEMED,
        WRONG_CODE
    }
    
    /**
     * Scans a given code.
     * @param code a 6-character code entered by the user
     * @param controller the controller object
     */
    public static void scan(String code, Controller controller){
        code = code.trim().toUpperCase();
        //System.out.println("\"" + code + "\"");
        Codes.checkAgainstAll(code, controller);
    }
}

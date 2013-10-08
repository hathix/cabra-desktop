package cabra;

import java.util.Calendar;



/**
 * Use this class to check for updates.
 * This shows a webpage to the user alerting them if there's an update.
 * If there's no update it just shows a tip.
 * @author Neel
 */
public abstract class Updates {
    private Updates(){}
    
    /**
     * Checks for updates. See the class's description.
     * @param gui   the GUI object.
     */
    public static void checkForUpdates(GUI gui){
        //update the time of last update to right now
        UserData.setString("LastUpdateCheck", Calendar.getInstance().getTimeInMillis() + "");
        
        //open up the page
        String url = "http://cabra.hathix.com/cabra/updates.php";
        url += "?version=" + Utils.sanitizeURL(About.VERSION);
        url += "&prerelease=" + About.PRERELEASE;
        Utils.openURLinDialog(url, "Check for updates", "update-16.png", gui.getFrame(), true);
    }
}

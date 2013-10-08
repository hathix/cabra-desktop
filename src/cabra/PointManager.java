package cabra;

import cabra.dinero.VaultManager;
import java.util.ArrayList;

/** Manages "Points", which are earned by doing stuff in Cabra.
 *
 * @author Neel
 */
public class PointManager {

    private int points;
    private VaultManager vaultManager;
    
    public PointManager(){
        //load points from UserData
        this.points = UserData.getInt("Points");
    }
    
    public int getPoints(){
        return points;
    }

    private void setPoints(int points){
        this.points = points;
        //save it
        UserData.setString("Points", points + "");
        vaultManager.update();
    }
    
    /**
     * Decreases the number of points the user has by the given amount.
     * @param pointsToLose the points to lose.
     * @return the number of points the user has left.
     */
    public int decreasePoints(int pointsToLose){
        int pointsLeft = getPoints() - pointsToLose;
        setPoints(pointsLeft);
        return pointsLeft;
    }
    
    /**
     * Adds points.
     * @param activity an activity that the user just did.
     */
    public void gainPoints(PointEnums.Activity activity){
        int pointsGained = activity.getPoints();
        
        setPoints(points + pointsGained);
    }
    
    public void giveVaultManager(VaultManager vaultManager){
        this.vaultManager = vaultManager;
    }
}

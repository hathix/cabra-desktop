package cabra.dinero;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Contains a list of Prize Packs, which contain prizes of varying rarity.
 * When you buy a Prize Pack, it opens up and a certain number of prizes pop up - you've just won them.
 */
public class PrizePack{
  private static final int PRIZES_PER_PACK = 3;
  private static final int RARITY_DEVIATION = 3; //prizes in this pack have rarities within this.rarity +- deviation
    
  /**
   * A list of every prize pack. Ensure this is sorted.
   */
  public static final PrizePack[] PRIZEPACKS = new PrizePack[]{
    new PrizePack  (2, 50),
      new PrizePack(4, 100),
      new PrizePack(6, 250),
      new PrizePack(8, 500),
      new PrizePack(10, 1000),
      new PrizePack(12, 2000),
      new PrizePack(14, 3000),
      new PrizePack(16, 4000),
      new PrizePack(18, 5000),
      new PrizePack(20, 6000),
      new PrizePack(22, 8000),
      new PrizePack(25, 10000)
  };
  
  private int rarity;
  private int cost;
  
  /**
   * Creates a PrizePack with the given rarity and cost.
   * @param rarity how rare, on average, are the prizes given by the prize pack.
   * @param cost how much it costs to purchase the prize pack.
   */
  private PrizePack(int rarity, int cost){
    this.rarity = rarity;
    this.cost = cost;
  }
  
  public int getRarity(){ return rarity; }
  public int getCost(){ return cost; }
  
  /**
   * Opens up this prize pack, returning a few random prizes of varying rarity.
   */
  public Prize[] retrievePrizes(){
    Prize[] prizes = new Prize[PRIZES_PER_PACK];
    //<TODO>: prevent duplicates
    for(int i=0; i<PRIZES_PER_PACK; i++){
      prizes[i] = Prize.getSuitablePrize(this.rarity, RARITY_DEVIATION);
    }
    
    return prizes;
  }
  
  /**
   * Returns the number of stars this pack has, based on its rarity.
   * The number of stars is in the range [1, maxStars] (inclusive.)
   * @param maxStars the number of stars this should have if it has maximum rarity. At other rarities this will be used as a scale.
   * @return int the number of stars, between 1 and maxStars.
   */
  public int getStars(int maxStars){
    //rarest pack will have most stars, so scale accordingly
    int maxRarity = getRarestPack().getRarity();
    //proportion: this rarity / max rarity = this stars / max stars
    //or this stars = this rarity * max stars / max rarity
    double rawStars = 1.0 * this.rarity * maxStars / maxRarity;
    //round accordingly, but ensure it's at least 1
    int stars = (int)Math.round(rawStars);
    if(stars < 1) stars = 1;
    return stars;
  }
  
  /**
   * @param points the number of points the user has/feels like spending.
   * @return true if you can buy this prize pack with that number of points, false otherwise.
   */
  public boolean canUserAfford(int points){
    return cost <= points;
  }  
  
  /**
   * Returns a list of each Prize Pack available.
   */
  public static ArrayList<PrizePack> getAllPrizePacks(){
    ArrayList<PrizePack> packs = new ArrayList<PrizePack>();
    packs.addAll(Arrays.asList(PRIZEPACKS));
    return packs;
  }
  
  private static PrizePack getRarestPack(){
    //because it's already sorted, just return the last one in the list
    return PRIZEPACKS[PRIZEPACKS.length - 1];
  }
}
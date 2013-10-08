package cabra.dinero;

import cabra.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Model for a prize, or an stuffed animal the user can buy.
 */
public enum Prize {

    SQUIRREL("Squirrel", 1),
    DOG("Dog", 2),
    CAT("Cat", 3),
    SHEEP("Sheep", 4),
    COW("Cow", 5),
    PIG("Pig", 6),
    CHICKEN("Chicken", 7),
    
    DOG2("Dog", 8),
    CAT2("Cat", 9),
    SHEEP2("Sheep", 10),
    COW2("Cow", 11),
    PIG2("Pig", 12),
    CHICKEN2("Chicken", 13),
    PENGUIN("Penguin", 14),
    ELEPHANT("Elephant", 15),
    
    SHEEP3("Sheep", 16),
    COW3("Cow", 17),
    PIG3("Pig", 18),
    CHICKEN3("Chicken", 19),
    PENGUIN2("Penguin", 20),
    ELEPHANT2("Elephant", 21),
    LION("Lion", 22),
    TIGER("Tiger", 23),
    
    GOAT("Goat", 24),
    EVIL_GOAT("Evil Goat", 25);
    
    
    
    private final int rarity;
    private final ImageIcon image;
    private final String name;

    /**
     * Full constructor. DON'T CALL THIS DIRECTLY. Use the constructor sans image.
     */
    Prize(String name, int rarity, ImageIcon image) {
        this.name = name;
        this.rarity = rarity;
        this.image = image;
    }

    /**
     * Convenience overload for Prize(int, ImageIcon, name). Pass the name of the image isntead of the imageicon itself.
     * DON'T CALL THIS DIRECTLY EITHER. Use the constructor sans image.
     */
    Prize(String name, int rarity, String imageURL) {
        this(name, rarity, ImageManager.createImageIcon(imageURL));
    }

    /**
     * Super-convenient overload for Prize constructor. The image URL will be the lowercased version of the name + ".png".
     */
    Prize(String name, int rarity) {
        this(name, rarity, "prizes/" + name.replace(' ', '-').toLowerCase() + ".png");
    }

    public ImageIcon getImage() {
        return image;
    }

    public String getName() {
        return name;
    }

    /**
     * Returns the unique identification string of the Prize.
     */
    public String getID() {
        return name();
    }

    public int getRarity() {
        return rarity;
    }

    /**
     * Returns the prize type that fits this prize.
     */
    public PrizeType getPrizeType() {
        return PrizeType.getPrizeType(this);
    }

    public static ArrayList<Prize> getAllPrizes() {
        ArrayList<Prize> prizes = new ArrayList<Prize>();
        prizes.addAll(Arrays.asList(Prize.values()));
        return prizes;
    }

    /**
     * Returns the prize with the given prize ID, or null if there is none.
     * The prize ID must be unique to the prize; use getID() to find a prize's ID.
     */
    public static Prize getPrizeByID(String prizeID) {
        try {
            Prize prize = Prize.valueOf(prizeID);
            return prize;
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    /**
     * Returns a random suitable prize. The prize's rarity will be in the range [rarity-deviation, rarity+deviation].
     * This is guaranteed to return at least one prize.
     */
    public static Prize getSuitablePrize(int rarity, int deviation) {
        //create list of suitable prizes
        ArrayList<Prize> suitablePrizes = new ArrayList<Prize>();
        for (Prize prize : getAllPrizes()) {
            if (prize.rarity >= rarity - deviation && prize.rarity <= rarity + deviation) {
                suitablePrizes.add(prize);
            }
        }

        //choose random one from the list
        int random = (int) (suitablePrizes.size() * Math.random());
        return suitablePrizes.get(random);
    }

    /**
     * Prize is an enum and hence can't use compareTo(), so we'll use this.
     */
    public static int compare(Prize a, Prize b) {
        return a.getRarity() - b.getRarity();
    }

    /**
     * Based on its rarity, each prize has a type, or color, and a rarity name associated with it.
     * This way the same kind of prize (same animal) can have souped-up versions.
     */
    public enum PrizeType {

        BLUE(7, "0094FF", "Common"),
        RED(15, "FF0000", "Uncommon"),
        GREEN(23, "00E500", "Rare"),
        GOLD(30, "FFC800", "Super Rare");
        /*
        
        //New point values?
        4 
        7
        10
        13
        15
        18
        21
        23
        25
        
         */
        private int maxRarity;
        private Color color;
        private String rarityName;

        /**
         * Creates a prize type.
         * The color text is a 6-letter hex string (FF0000) which will map to a color.
         * The rarity name is like "Common" or "Uncommon"; will be displayed on the prize's display.
         */
        PrizeType(int maxRarity, String colorText, String rarityName) {
            this.maxRarity = maxRarity;
            this.color = ColorManager.createColor(colorText);
            this.rarityName = rarityName;
        }

        public int getMaxRarity() {
            return maxRarity;
        }

        public Color getColor() {
            return color;
        }

        public String getRarityName() {
            return rarityName;
        }

        /**
         * Returns the prize type which should fit the given prize.
         * The prize type is based on the prize's rarity and which range it falls in.
         */
        public static PrizeType getPrizeType(Prize prize) {
            PrizeType[] prizeTypes = PrizeType.values();
            for (int i = 0; i < prizeTypes.length; i++) {
                if (prize.getRarity() <= prizeTypes[i].getMaxRarity()) {
                    return prizeTypes[i];
                }
            }

            //it's above the max rarity of any prize type; just give the best one
            return prizeTypes[prizeTypes.length - 1];
        }
    }
}
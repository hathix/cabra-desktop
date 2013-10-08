package cabra.dinero;

import cabra.*;
import javax.swing.*;
import java.awt.*;

/**
 * A panel that contains a prize and draws the prize on it.
 */
public class PrizePanel extends JPanel {

    private ImageIcon image;
    private Color bgColor;
    private static final String PLACEHOLDER_TEXT = "Buy some more prizes to fill up your vault!";
    private static final int BORDER_RADIUS = 10; //rounded corners of the background

    /**
     * Creates a Prize Panel that contains and shows a prize.
     * Pass null to show a question mark (an empty placeholder.)
     */
    public PrizePanel(Prize prize) {
        setLayout(new GridLayout(1, 1));
        //setMinimumSize(new Dimension(MIN_WIDTH, MIN_HEIGHT));

        if (prize == null) {
            //this is a placeholder; show a question mark
            this.image = ImageManager.createImageIcon("prizes/question.png");
            JLabel imageLabel = new JLabel(image);
            imageLabel.setToolTipText(PLACEHOLDER_TEXT);
            add(imageLabel);
        } else {
            //add label containing the image
            this.image = prize.getImage();
            JLabel imageLabel = new JLabel(image);
            imageLabel.setToolTipText(prize.getName() + " - " + prize.getPrizeType().getRarityName());
            add(imageLabel);

            //draw bg later based on the type of prize
            this.bgColor = prize.getPrizeType().getColor();
        }
    }

    public void paintComponent(Graphics g) {
        if (bgColor != null) {
            //draw the background in a rounded rectangle; take up the whole space
            g.setColor(bgColor);
            g.fillRoundRect(0, 0, getWidth(), getHeight(), BORDER_RADIUS, BORDER_RADIUS);
        }
    }
}
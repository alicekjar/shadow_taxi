import bagel.Font;
import bagel.Image;
import bagel.Window;

/** Abstract class representing all non-playable screens displaying game information
 *
 */
public abstract class Screen implements Displayable{
    private Image BACKGROUND;
    private double WIDTH = Window.getWidth();
    private double HEIGHT = Window.getHeight();

    /** Constructs Screen object (must be called in constructor of subclass)
     * @param background Background image of screen
     */
    public Screen(String background) {
        BACKGROUND = new Image(background);
    }

    /** Finds x-coordinate needed to centre text
     * @param text Text we want to centre
     * @param font Font that text will be displayed in
     * @return X-coordinate of centred text
     */
    protected double centre(String text, Font font) {
        return (WIDTH - font.getWidth(text))/2.0;
    }

    // Draw background image

    /** Draw background image in middle of screen
     */
    protected void drawBackground() {
        // Division by 2.0 done to get centre
        BACKGROUND.draw(WIDTH/2.0, HEIGHT/2.0);
    }
}

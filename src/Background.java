import bagel.Image;
import bagel.Window;

import java.util.Properties;

/** A class representing the current background of the game which may revolve
 * to give the appearance of character movement
 */
public class Background implements Displayable{
    private final Image BACKGROUND;
    private final Image RAIN_BACKGROUND;

    private static final int WINDOW_WIDTH = Window.getWidth();
    private static final int WINDOW_HEIGHT = Window.getHeight();

    private static final double BACKGROUND_MIN = -0.5;
    private static final double BACKGROUND_MAX = 1.5;

    private double backgroundPos1 = WINDOW_HEIGHT / 2.0;
    private double backgroundPos2 = - backgroundPos1;

    private Image currentBackground;


    /** Constructs a Background object
     * @param gameProps An object containing all game values and graphics
     * @param w The initial weather at the start of the game
     */
    public Background(Properties gameProps, Weather w) {
        BACKGROUND = new Image(gameProps.getProperty("backgroundImage.sunny"));
        RAIN_BACKGROUND = new Image(gameProps.getProperty("backgroundImage.raining"));

        changeWeather(w.WEATHER.equals("RAINING"));
    }


    /** Changes the position of the background to make it look like the
     * player is moving
     * @param speed the rate at which the background moves per frame
     */
    public void moveY(int speed) {
        backgroundPos1 += speed;
        backgroundPos2 += speed;

        // update background when moving forwards
        if (backgroundPos1 >= BACKGROUND_MAX * WINDOW_HEIGHT) {
            backgroundPos1 = backgroundPos2 - Window.getHeight();
        }
        if (backgroundPos2 >= BACKGROUND_MAX * WINDOW_HEIGHT) {
            backgroundPos2 = backgroundPos1 - Window.getHeight();
        }

        // update background when moving background
        if (backgroundPos1 <= BACKGROUND_MIN * WINDOW_HEIGHT) {
            backgroundPos1 = backgroundPos2 + Window.getHeight();
        }
        if (backgroundPos2 <= BACKGROUND_MIN * WINDOW_HEIGHT) {
            backgroundPos2 = backgroundPos1 + Window.getHeight();
        }

    }


    /** Displays the background images
     */
    @Override
    public void display() {
        // division by 2.0 gives x-coord of centre of screen
        currentBackground.draw(WINDOW_WIDTH/2.0, backgroundPos1);
        currentBackground.draw(WINDOW_WIDTH/2.0, backgroundPos2);
    }


    /** Changes the weather to either raining or sunny
     * @param isRaining flags if weather should be switched to raining or sunny
     */
    public void changeWeather(boolean isRaining) {
        if (isRaining)
            currentBackground = RAIN_BACKGROUND;
        else
            currentBackground = BACKGROUND;
    }
}

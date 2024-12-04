import bagel.Image;
import bagel.util.Point;

import java.util.Properties;

/** Class representing a flag used to mark the end position of a trip
 */
public class EndFlag extends GameEntity {

    /** Constructs an EndFlag object
     * @param gameProps An object containing all game values and graphics
     * @param position The initial position of the flag
     */
    public EndFlag(Properties gameProps, Point position) {
        super(new Image(gameProps.getProperty("gameObjects.tripEndFlag.image")),
                Double.parseDouble(gameProps.getProperty("gameObjects.tripEndFlag.radius")),
                0, position);
        setVisible(false);
    }
}

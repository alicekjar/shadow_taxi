import bagel.Image;
import bagel.util.Point;

import java.util.Properties;

/** Class representing a powerup which grants invulnerability when held
 */
public class InvinciblePower extends Powerup {

    /** Constructs an InvinciblePower object
     * @param gameProps An object containing all game values and graphics
     * @param position Initial position of the coin
     */
    public InvinciblePower(Properties gameProps, Point position) {
        super(new Image(gameProps.getProperty("gameObjects.invinciblePower.image")),
                Double.parseDouble(gameProps.getProperty("gameObjects.invinciblePower.radius")),
                Integer.parseInt(gameProps.getProperty("gameObjects.invinciblePower.maxFrames")),
                position);
    }


}

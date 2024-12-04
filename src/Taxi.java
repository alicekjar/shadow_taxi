import bagel.Font;
import bagel.Image;
import bagel.util.Point;

import java.util.Properties;
import java.util.Random;


/** Class representing game object which player is able to control
 * through driver
 */
public class Taxi extends Car {
    private final Image DEAD_CAR;
    private final static Random RAND = new Random();

    /** Distance that taxi can move horizontally per frame
     */
    public final int SPEED_X;

    private final String HEALTH;
    private final int HEALTH_X;
    private final int HEALTH_Y;
    private final Font FONT;


    private final int[] LANES;
    private final int Y_POS_1;
    private final int Y_POS_2;


    private boolean vulnerable;
    private Driver driver;

    /** Constructs Taxi object
     * @param gameProps An object containing all game values and graphics
     * @param msgProps An object containing all text used in the game
     * @param position Initial position of taxi
     * @param driver Driver controlling taxi's movement
     */
    public Taxi(Properties gameProps, Properties msgProps, Point position, Driver driver) {
        super(gameProps, new Image(gameProps.getProperty("gameObjects.taxi.image")),
                Integer.parseInt(gameProps.getProperty("gameObjects.taxi.speedY")),
            Double.parseDouble(gameProps.getProperty("gameObjects.taxi.radius")),
                Double.parseDouble(gameProps.getProperty("gameObjects.taxi.damage")),
                position,
                Double.parseDouble(gameProps.getProperty("gameObjects.taxi.health")));

        DEAD_CAR = new Image(gameProps.getProperty("gameObjects.taxi.damagedImage"));

        SPEED_X = Integer.parseInt(gameProps.getProperty("gameObjects.taxi.speedX"));

        HEALTH = msgProps.getProperty("gamePlay.taxiHealth");
        HEALTH_X = Integer.parseInt(gameProps.getProperty("gamePlay.taxiHealth.x"));
        HEALTH_Y = Integer.parseInt(gameProps.getProperty("gamePlay.taxiHealth.y"));
        FONT = new Font(gameProps.getProperty("font"),
                Integer.parseInt(gameProps.getProperty("gamePlay.info.fontSize")));

        LANES = new int[] {Integer.parseInt(gameProps.getProperty("roadLaneCenter1")),
                Integer.parseInt(gameProps.getProperty("roadLaneCenter3"))};
        Y_POS_1 = Integer.parseInt(gameProps.getProperty("gameObjects.taxi.nextSpawnMinY"));
        Y_POS_2 = Integer.parseInt(gameProps.getProperty("gameObjects.taxi.nextSpawnMaxY"));

        this.driver = driver;

        if (position == null) {
            // need to generate a new random positon
            int x = LANES[RAND.nextInt(LANES.length)];
            int y = Y_POS_1 + RAND.nextInt(Y_POS_2 - Y_POS_1);
            setPosition(new Point(x, y));
        }
    }


    /** Displays taxi based on its current status
     */
    @Override
    public void display() {
        if (isDead()) {
            DEAD_CAR.draw(getPosition().x, getPosition().y);
            displayEffect(FIRE);
            return;
        }

        super.display();
        // show taxi health
        FONT.drawString(String.format(HEALTH + "%.2f", getHealth()), HEALTH_X, HEALTH_Y);
    }


    /** Moves the taxi in the Y direction
     * @param increase The amount by which the player has moved forward
     */
    @Override
    public void moveY(int increase) {
        if(getCollisionMovement() > 0) {
            // taxi moving away after collision
            setCollisionMovement(getCollisionMovement() - 1);
            setPosition(new Point(getPosition().x, getPosition().y + increase + getDirection()));
        }
        else {
            // taxi stationary with respect to background
            setPosition(new Point(getPosition().x, getPosition().y + increase));
        }

        if (getPosOfEffect() != null) {
            // make effect remain at site of collision
            setPosOfEffect(new Point(getPosOfEffect().x, getPosOfEffect().y + increase));
        }
    }


    /** Causes death of taxi but passes on immunity to driver
     */
    @Override
    public void die() {
        super.die();
        driver.setImmuneFor();
    }


    /** Checks if Taxi may be collided with
     * @return Flag representing vulnerability status of Taxi
     */
    @Override
    public boolean isInvulnerable() {
        return (super.isInvulnerable() || !driver.isVisible() && driver.getInvincible() != null);
    }

    public void setDriver(Driver driver) { this.driver = driver; }
}

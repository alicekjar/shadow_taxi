import bagel.Image;
import bagel.Input;
import bagel.Keys;
import bagel.util.Point;

import java.util.ArrayList;
import java.util.Properties;

/** Class which implements the driver game entity through which the game is played
 */
public class Driver extends Person {
    private final Properties GAME_PROPS;
    private final Properties MSG_PROPS;

    private Coin coin;
    private InvinciblePower invincible;
    private Passenger passenger;
    private Taxi taxi;
    private Trip lastTrip;

    /**
     * The amount by which the x-coordinate of the driver is changed
     * after driver is ejected from taxi
     */
    public static final int EJECT_OFFSET= 50;


    /** Constructs a new Driver object
     * @param gameProps An object containing all game values and graphics
     * @param msgProps An object containing all text used in the game
     * @param position The initial position of the driver
     * @param taxi The taxi entity that the driver may get in and drive
     */
    public Driver(Properties gameProps, Properties msgProps, Point position, Taxi taxi) {
        super(gameProps,
                msgProps.getProperty("gamePlay.driverHealth"),
                Integer.parseInt(gameProps.getProperty("gamePlay.driverHealth.x")),
                Integer.parseInt(gameProps.getProperty("gamePlay.driverHealth.y")),
                Integer.parseInt(gameProps.getProperty("gameObjects.driver.walkSpeedX")),
                new Image(gameProps.getProperty("gameObjects.driver.image")),
                Double.parseDouble(gameProps.getProperty("gameObjects.driver.radius")),
                Integer.parseInt(gameProps.getProperty("gameObjects.driver.walkSpeedY")),
                position,
                Double.parseDouble(gameProps.getProperty("gameObjects.driver.health")));

        GAME_PROPS = gameProps;
        MSG_PROPS = msgProps;
        this.taxi = taxi;
        this.taxi.setDriver(this);
        setVisible(true);
    }


    /** Moves driver horizontally based on arrow keys
     * @param input The current status of the keyboard being used to control the game
     */
    void moveX(Input input) {
        if (!isVisible() && taxi.getPosOfEffect() != null)
            // can't move after being hit
            return;

        int speed;
        // set speed depending if driver is driving or walking
        if (isVisible()) {
            speed = SPEED_X;
        }
        else
            speed = taxi.SPEED_X;

        if (input.isDown(Keys.RIGHT))
            addX(speed);
        if (input.isDown(Keys.LEFT))
            addX(-speed);

        if (!isVisible()) {
            // move taxi with driver
            taxi.setPosition(getPosition());
        }

        if (passenger != null) {
            if (isVisible()) {
                // passenger follows driver while walking
                passenger.approach(new Point(getPosition().x - EJECT_OFFSET, getPosition().y));
            }
            else if (passenger.isCollected() && !passenger.isApproachTaxi()) {
                // move travelling passenger with taxi
                passenger.setPosition(getPosition());
            }
        }
    }


    /** Updates the horizontal position of driver
     * @param dist Amount by which x-coordinate of driver changes
     */
    public void addX(int dist) {
        setPosition(new Point(getPosition().x + dist, getPosition().y));
    }


    /** Checks if the driver has collided with any powerups
     * @param powerups all possible powerups to collide with
     */
    public void checkPowerups(ArrayList<Powerup> powerups) {
        for (Powerup p: powerups) {
            double range = RADIUS + p.RADIUS;
            // check if in pickup range
            if (getPosition().distanceTo(p.getPosition()) < range && !p.isCollected()) {
                // we can pick up this powerup
                p.collect();
                if (p instanceof Coin) {
                    coin = (Coin) p;
                }
                else {
                    invincible = (InvinciblePower) p;
                }
            }
        }
    }


    /** Displays the driver and/or taxi and updates remaining powerup frames
     */
    @Override
    public void display() {
        super.display();
        taxi.display();

        // reduce remaining frames of powerups
        if (coin != null) {
            coin = (Coin) coin.reduceRemFrames();
        }
        if (invincible != null) {
            invincible = (InvinciblePower) invincible.reduceRemFrames();
        }

        // display driver health value
        FONT.drawString(String.format(HEALTH + "%.2f", getHealth()), HEALTH_X, HEALTH_Y);
    }


    /** Check if any passengers can be collected
     * @param passengers list of all possible passengers to collide with
     */
    public void checkPassengers(ArrayList<Passenger> passengers) {
        for (Passenger p: passengers) {
            if (!isVisible() && !p.isCollected() && p.getPosition().distanceTo(getPosition()) <= p.TAXI_DETECT_RAD) {
                // valid pick up position for passenger
                boolean collected = p.approach(getPosition());
                if (collected) {
                    // passenger in taxi
                    passenger = p;
                    p.collect();
                    lastTrip = p.getTrip();
                }
            }
        }
    }




    /** Ejects people in taxi and generates a new one somewhere on screen
     */
    public void newTaxi() {
        taxi = new Taxi(GAME_PROPS, MSG_PROPS, null, this);
        if (!isVisible()) {
            // eject driver
            setVisible(true);
            setPosition(new Point(getPosition().x - EJECT_OFFSET, getPosition().y));
            if (passenger != null) {
                // eject passenger
                passenger.setVisible(true);
                passenger.setPosition(new Point(getPosition().x - EJECT_OFFSET, getPosition().y));
            }
        }
    }


    /** Checks if driver/taxi is in collision timeout or has invincible power
     * @return Flag representing vulnerability status of car
     */
    @Override
    public boolean isInvulnerable() {
        return (super.isInvulnerable() || invincible != null);
    }


    public Coin getCoin() { return coin; }
    public Taxi getTaxi() { return taxi; }
    public Passenger getPassenger() { return passenger; }
    public Trip getLastTrip() { return lastTrip; }
    public InvinciblePower getInvincible() { return invincible; }

    public void setPassenger(Passenger passenger) { this.passenger = passenger; }
}

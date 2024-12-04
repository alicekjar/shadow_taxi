import bagel.Font;
import bagel.Image;
import bagel.util.Point;

import java.util.Properties;

/** Class representing a game entity which the player may collect and
 * drop off to earn money
 */
public class Passenger extends Person {
    private final Font FONT;

    /** Max distance between a passenger and taxi to allow pick up
     */
    public final int TAXI_DETECT_RAD;

    private static final int PRIORITY_OFFSET = 30;
    private static final int FEE_OFFSET = 100;

    private boolean collected = false;
    private boolean droppingOff = false;
    private boolean approachTaxi = false;
    private boolean arrived = false;
    private Trip trip;


    /** Constructs a Passenger object
     * @param gameProps An object containing all game values and graphics
     * @param msgProps An object containing all text used in the game
     * @param position The initial position of the passenger
     * @param trip The trip details associates with that passenger
     */
    public Passenger(Properties gameProps, Properties msgProps, Point position, Trip trip) {
        super(gameProps,
                msgProps.getProperty("gamePlay.passengerHealth"),
                Integer.parseInt(gameProps.getProperty("gamePlay.passengerHealth.x")),
                Integer.parseInt(gameProps.getProperty("gamePlay.passengerHealth.y")),
                Integer.parseInt(gameProps.getProperty("gameObjects.passenger.walkSpeedX")),
                new Image(gameProps.getProperty("gameObjects.passenger.image")),
                Double.parseDouble(gameProps.getProperty("gameObjects.passenger.radius")),
                Integer.parseInt(gameProps.getProperty("gameObjects.passenger.walkSpeedY")),
                position,
                Double.parseDouble(gameProps.getProperty("gameObjects.passenger.health")));

        FONT = new Font(gameProps.getProperty("font"),
                Integer.parseInt(gameProps.getProperty("gameObjects.passenger.fontSize")));
        TAXI_DETECT_RAD = Integer.parseInt(gameProps.getProperty("gameObjects.passenger.taxiDetectRadius"));

        this.trip = trip;
    }


    // move passenger and end position in the y-direction

    /** Moves the passenger and trip end in the y direction
     * @param increase The amount the passenger moves vertically
     */
    @Override
    public void moveY(int increase) {
        super.moveY(increase);
        trip.moveY(increase);
    }


    /** Updates passenger and displays passenger and trip info on screen
     */
    @Override
    public void display() {
        if(getCollisionMovement() > 0) {
            // move away from site of collision
            setCollisionMovement(getCollisionMovement() - 1);
            setPosition(new Point(getPosition().x + getDirection(), getPosition().y + getDirection()));
        }
        if (droppingOff) {
            // move towards end flag
            droppingOff = !approach(trip.getEnd().getPosition());
            if (!droppingOff) {
                arrived = true;
                trip.getEnd().setVisible(false);
            }
        }

        super.display();

        if (!collected) {
            // display trip information
            FONT.drawString(String.format("%d", trip.getPriority()),
                    getPosition().x - PRIORITY_OFFSET, getPosition().y);
            FONT.drawString(String.format("%.1f", trip.getEarnings()),
                    getPosition().x - FEE_OFFSET, getPosition().y);
        }
        trip.getEnd().display();
    }


    /** Puts passenger visually in taxi and shows end flag
     */
    public void collect() {
        this.collected = true;
        setVisible(false);
        trip.getEnd().setVisible(true);
    }


    /** Moves passenger towards destination
     * @param destination The place the passenger is moving towards
     * @return Flag indicating if passenger has reached destination yet
     */
    public boolean approach(Point destination) {
        // move in x direction
        int step = SPEED_X;
        if (destination.x > getPosition().x) {
            setPosition(new Point(getPosition().x + step, getPosition().y));
        }
        else if (destination.x < getPosition().x) {
            setPosition(new Point(getPosition().x - step, getPosition().y));
        }

        // move in y direction
        step = getSpeedY();
        if (destination.y > getPosition().y) {
            setPosition(new Point(getPosition().x, getPosition().y + step));
        }
        else if (destination.y < getPosition().y) {
            setPosition(new Point(getPosition().x, getPosition().y - step));
        }

        // check if reached destination
        return destination.equals(getPosition());
    }


    /** Frees up taxi and initiates walk towards end flag
     * @param driver the driver that had picked up the passenger
     */
    public void dropOff(Driver driver) {
        driver.setPassenger(null);
        droppingOff = true;
        setVisible(true);
    }


    public Trip getTrip() { return trip; }
    public boolean isCollected() { return collected; }
    public boolean isApproachTaxi() { return approachTaxi; }
    public boolean hasArrived() { return arrived; }

    public void setApproachTaxi(boolean approachTaxi) {this.approachTaxi = approachTaxi; }
}

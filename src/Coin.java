import bagel.Image;
import bagel.util.Point;

import java.util.ArrayList;
import java.util.Properties;

/** Class representing a coin game object which will increase the
 * priority of a passenger when held
 */
public class Coin extends Powerup {
    private ArrayList<Passenger> passengers;

    /** Constructs a Coin object
     * @param gameProps An object containing all game values and graphics
     * @param position Initial position of the coin
     */
    public Coin(Properties gameProps, Point position) {
        super(new Image(gameProps.getProperty("gameObjects.coin.image")),
                Double.parseDouble(gameProps.getProperty("gameObjects.coin.radius")),
                Integer.parseInt(gameProps.getProperty("gameObjects.coin.maxFrames")),
                position);
    }


    /** Collects coin and updates passenger priorities
     */
    @Override
    public void collect() {
        super.collect();
        for(Passenger p: passengers) {
            p.getTrip().updatePriority(false, true);
        }
    }


    /** Reduce the remaining frames that the power-up is active for
     * @return Itself if still active, else null
     */
    @Override
    public Powerup reduceRemFrames() {
        Powerup coin = super.reduceRemFrames();
        if (coin == null) {
            // power-up no longer active
            for(Passenger p: passengers) {
                p.getTrip().updatePriority(false, false);
            }
        }
        return coin;
    }

    public void setPassengers(ArrayList<Passenger> passengers) { this.passengers = passengers; }
}

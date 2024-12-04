import bagel.Image;
import bagel.util.Point;

import java.util.ArrayList;
import java.util.Properties;
import java.util.Random;

/** Class representing a specific type of Car which has its own graphic
 * and ability to shoot fireballs
 */
public class EnemyCar extends Car {
    private final Properties GAME_PROPS;

    private ArrayList<Fireball> fireballs = new ArrayList<Fireball>();

    private static final Random RAND = new Random();
    private static final int FIREBALL_DIVISOR = 300;
    private static final int MAX_RAND = 1000;


    /** Constructs an EnemyCar object
     * @param gameProps An object containing all game values and graphics
     * @param speed_Y The speed at which the car travels forward
     * @param position The initial position of the car on screen
     */
    public EnemyCar(Properties gameProps, int speed_Y, Point position) {
        super(gameProps,
                new Image(gameProps.getProperty("gameObjects.enemyCar.image")), speed_Y,
                Double.parseDouble(gameProps.getProperty("gameObjects.enemyCar.radius")),
                Double.parseDouble(gameProps.getProperty("gameObjects.enemyCar.damage")),
                position,
                Double.parseDouble(gameProps.getProperty("gameObjects.enemyCar.health")));
        GAME_PROPS = gameProps;
    }

    /** Displays enemy car and associated fireballs
     */
    @Override
    public void display() {
        if (isVisible())
            // try to generate fireball
            generateFireball();

        //display all fireballs
        for (Fireball f: fireballs) {
            f.display();
        }

        super.display();
    }


    /** Moves enemy car and fireballs in y-direction
     * @param increase The amount by which the player has moved forward
     */
    @Override
    public void moveY(int increase) {
        super.moveY(increase);

        for (Fireball f: fireballs) {
            f.moveY(increase - f.getSpeedY());
        }
    }


    /** Randomly tries to generate new fireball
     */
    private void generateFireball() {
        int rand = RAND.nextInt(MAX_RAND) + 1;
        if (rand % FIREBALL_DIVISOR == 0) {
            // generate new fireball
            fireballs.add(new Fireball(GAME_PROPS, getPosition()));
        }
    }


    /** Prompts collision between entity and enemy car/its fireballs if value
     * @param entity The entity that the car might collide with
     */
    @Override
    public void collide(Damagable entity) {
        // check if fireballs have collided with any entities
        for (Fireball f: fireballs) {
            if (entity != this)
                // fireballs should not damage the car that shot them
                f.collide(entity);
        }
        super.collide(entity);
    }
}

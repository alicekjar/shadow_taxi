import bagel.Image;
import bagel.util.Point;

import java.util.Properties;
import java.util.Random;


/** Singleton class which randomly generates different type of cars
 * and adds then to the game
 */
public class CarGenerator {
    private final Properties GAME_PROPS;
    private final Random RAND = new Random();

    private final int LANES[];
    private final int MAX_SPEED;
    private final int MIN_SPEED;
    private final int ENEMY_MAX_SPEED;
    private final int ENEMY_MIN_SPEED;

    private static final int CAR_DIVISOR = 200;
    private static final int ENEMY_DIVISOR = 400;
    private static final int CAR_TYPES = 2;
    private static final int Y_POS[] = new int[] {-50, 768};
    private static final int MAX_RAND = 1000;

    private static CarGenerator _instance = null;

    /** Generates a single instance of CarGenerator
     * @param gameProps An object containing all game values and graphics
     */
    private CarGenerator(Properties gameProps) {
        this.GAME_PROPS = gameProps;

        LANES = new int[] {Integer.parseInt(GAME_PROPS.getProperty("roadLaneCenter1")),
                Integer.parseInt(GAME_PROPS.getProperty("roadLaneCenter2")),
                Integer.parseInt(GAME_PROPS.getProperty("roadLaneCenter3"))};


        MAX_SPEED = Integer.parseInt(GAME_PROPS.getProperty("gameObjects.otherCar.maxSpeedY"));
        MIN_SPEED = Integer.parseInt(GAME_PROPS.getProperty("gameObjects.otherCar.minSpeedY"));

        ENEMY_MAX_SPEED = Integer.parseInt(GAME_PROPS.getProperty("gameObjects.enemyCar.maxSpeedY"));
        ENEMY_MIN_SPEED = Integer.parseInt(GAME_PROPS.getProperty("gameObjects.enemyCar.minSpeedY"));
    }


    /** Generates a random type of normal car in a random position with random speed
     * @return The newly generated car
     */
    public Car generateCar() {
        int rand = RAND.nextInt(MAX_RAND) + 1;
        if (rand % CAR_DIVISOR != 0)
            // do not generate new car
            return null;

        // get position and car type
        int x_pos = LANES[RAND.nextInt(LANES.length)];
        int y_pos = Y_POS[RAND.nextInt(Y_POS.length)];
        int car_type = RAND.nextInt(CAR_TYPES) + 1;

            return new Car(GAME_PROPS,
                    new Image(String.format(GAME_PROPS.getProperty("gameObjects.otherCar.image"), car_type)),
                    getSpeed(false),
                    Double.parseDouble(GAME_PROPS.getProperty("gameObjects.otherCar.radius")),
                    Double.parseDouble(GAME_PROPS.getProperty("gameObjects.otherCar.damage")),
                    new Point(x_pos, y_pos),
                    Double.parseDouble(GAME_PROPS.getProperty("gameObjects.otherCar.health")));
    }


    /** Generates an enemy car in a random position with random speed
     * @return The newly generated enemy car
     */
    public Car generateEnemy() {
        int rand = RAND.nextInt(MAX_RAND) + 1;
        if (rand % ENEMY_DIVISOR != 0)
            // do not generate new enemy car
            return null;

        // get position
        int x_pos = LANES[RAND.nextInt(LANES.length)];
        int y_pos = Y_POS[RAND.nextInt(Y_POS.length)];

        return new EnemyCar(GAME_PROPS, getSpeed(true), new Point(x_pos, y_pos));
    }


    /** Generate random new speed for car from given range
     * @param isEnemy Flag representing if we want to generate a speed for an enemy car
     * @return The new speed
     */
    public int getSpeed(boolean isEnemy) {
        if (isEnemy) {
            // allows for extensibility in the case that enemy cars have different speed range
            return RAND.nextInt(ENEMY_MAX_SPEED - ENEMY_MIN_SPEED + 1) + ENEMY_MIN_SPEED;
        }
        return RAND.nextInt(MAX_SPEED - MIN_SPEED + 1) + MIN_SPEED;
    }


    /** Gets single instance of car generator, creating a new one if none exists     *
     * @param gameProps An object containing all game values and graphics
     * @return The singular instance of CarGenerator
     */
    public static CarGenerator getInstance(Properties gameProps) {
        if (_instance == null) {
            _instance = new CarGenerator(gameProps);
        }
        return _instance;
    }
}

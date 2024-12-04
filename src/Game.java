import bagel.*;
import bagel.util.Point;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Properties;

/** Class allowing for the implementation of the playable component of game
 */
public class Game {
    private final Properties GAME_PROPS;
    private final Properties MSG_PROPS;

    private final CarGenerator CAR_GEN;

    private final Font FONT;
    private final double TARGET;
    private final int MAX_FRAMES;
    private final int COIN_MAX;

    private final int EARNINGS_X;
    private final int EARNINGS_Y;
    private final int COIN_X;
    private final int COIN_Y;
    private final int TARGET_X;
    private final int TARGET_Y;
    private final int FRAMES_X;
    private final int FRAMES_Y;

    private Background background;
    private ArrayList<Weather> weather;

    // game status
    private final int WIN = 1;
    private final int LOSE = -1;
    private final int PLAY = 0;

    private int framesLeft;
    private double totalEarnings = 0;

    // game objects
    private Driver driver;
    private ArrayList<Powerup> powerups;
    private ArrayList<Passenger> passengers;
    private ArrayList<Car> cars = new ArrayList<Car>();

    // trip details to be shown on screen
    private Trip trip = null;


    /** Constructs a Game object
     * @param gameProps An object containing all game values and graphics
     * @param msgProps An object containing all text used in the game
     */
    public Game(Properties gameProps, Properties msgProps) {
        GAME_PROPS = gameProps;
        MSG_PROPS = msgProps;

        CAR_GEN = CarGenerator.getInstance(GAME_PROPS);

        FONT = new Font(GAME_PROPS.getProperty("font"),
                Integer.parseInt(GAME_PROPS.getProperty("gamePlay.info.fontSize")));

        TARGET = Double.parseDouble(GAME_PROPS.getProperty("gamePlay.target"));
        MAX_FRAMES = Integer.parseInt(GAME_PROPS.getProperty("gamePlay.maxFrames"));
        COIN_MAX = Integer.parseInt(GAME_PROPS.getProperty("gameObjects.coin.maxFrames"));

        EARNINGS_X = Integer.parseInt(GAME_PROPS.getProperty("gamePlay.earnings.x"));
        EARNINGS_Y = Integer.parseInt(GAME_PROPS.getProperty("gamePlay.earnings.y"));
        COIN_X = Integer.parseInt(GAME_PROPS.getProperty("gameplay.coin.x"));
        COIN_Y = Integer.parseInt(GAME_PROPS.getProperty("gameplay.coin.y"));
        TARGET_X = Integer.parseInt(GAME_PROPS.getProperty("gamePlay.target.x"));
        TARGET_Y = Integer.parseInt(GAME_PROPS.getProperty("gamePlay.target.y"));
        FRAMES_X = Integer.parseInt(GAME_PROPS.getProperty("gamePlay.maxFrames.x"));
        FRAMES_Y = Integer.parseInt(GAME_PROPS.getProperty("gamePlay.maxFrames.y"));

        framesLeft = MAX_FRAMES;

        // read in game data
        parseWeather();
        parseObjects();
    }


    /** Controls the flow of the game through keyboard input
     * @param input The current status of the keyboard being used to control the game
     * @param name The user-entered name of the current player
     * @return The status of gameplay dependent on if a win, loss or neither has occured
     */
    public int playGame(Input input, String name) {
        framesLeft--;
        addCars();

        // check for a change in weather
        if (!weather.isEmpty() && MAX_FRAMES - framesLeft >= weather.get(0).START) {
            String next = weather.remove(0).WEATHER;
            background.changeWeather(next.equals("RAINING"));
            for(Passenger p: passengers) {
                // change priority of passengers without umbrella
                p.getTrip().updatePriority(true, next.equals("RAINING"));
            }
        }

        // use keys to move player
        moveForward(input);
        driver.moveX(input);

        // check if game objects can interact
        checkPassenger(input);
        driver.checkPowerups(powerups);
        checkAllCollisions();


        // display all game elements
        background.display();
        displayInfo();
        displayObjects();


        int status = checkStatus();
        if (status != PLAY) {
            // game over, write name and score to file
            IOUtils.writeScoreToFile(GAME_PROPS.getProperty("gameEnd.scoresFile"),
                    String.format("%s, %f", name, totalEarnings));
        }
        return status;
    }


    /** Reads in game objects from CSV
     */
    private void parseObjects() {
        String[][] objects = IOUtils.readCommaSeparatedFile(GAME_PROPS.getProperty("gamePlay.objectsFile"));
        int objectCount = objects.length;

        powerups = new ArrayList<Powerup>();
        passengers = new ArrayList<Passenger>();

        Taxi taxi = null;
        Point driverPos = null;

        for (int i = 0; i < objectCount; i++) {
            if (objects[i][0].equals("DRIVER")) {
                // get driver details -  to be added after taxi added
                driverPos = new Point(Double.parseDouble(objects[i][1]), Double.parseDouble(objects[i][2]));
            }

            if (objects[i][0].equals("TAXI")) {
                // add taxi
                taxi = new Taxi(GAME_PROPS, MSG_PROPS,
                        new Point(Double.parseDouble(objects[i][1]), Double.parseDouble(objects[i][2])),
                        null);
            }

            if (objects[i][0].equals("COIN")) {
                // add coins
                powerups.add(new Coin(GAME_PROPS,
                        new Point(Double.parseDouble(objects[i][1]), Double.parseDouble(objects[i][2]))));
            }
            else if (objects[i][0].equals("INVINCIBLE_POWER")) {
                // add invincible powers
                powerups.add(new InvinciblePower(GAME_PROPS,
                        new Point(Double.parseDouble(objects[i][1]), Double.parseDouble(objects[i][2]))));
            }
            else if (objects[i][0].equals("PASSENGER")) {
                // add passengers
                Point pos = new Point(Double.parseDouble(objects[i][1]), Double.parseDouble(objects[i][2]));
                Trip trip = new Trip(GAME_PROPS, MSG_PROPS, pos, Integer.parseInt(objects[i][3]),
                        Integer.parseInt(objects[i][4]), Integer.parseInt(objects[i][5]),
                        Boolean.parseBoolean(objects[i][6]));
                passengers.add(new Passenger(GAME_PROPS, MSG_PROPS, pos, trip));
            }
        }

        // add driver
        driver = new Driver(GAME_PROPS, MSG_PROPS, driverPos, taxi);

        // give coins information about passengers
        for (Powerup p: powerups) {
            if (p instanceof Coin)
                ((Coin) p).setPassengers(passengers);
        }
    }


    /** Read in weather as usable Weather objects from csv
     */
    private void parseWeather() {
        String[][] strWeather = IOUtils.readCommaSeparatedFile(GAME_PROPS.getProperty("gamePlay.weatherFile"));

        weather = new ArrayList<Weather>();

        for (String[] w: strWeather) {
            // add weather event
            weather.add(new Weather(w[0], Integer.parseInt(w[1]), Integer.parseInt(w[2])));
        }

        // sort by start in ascending order
        Collections.sort(weather);

        background = new Background(GAME_PROPS, weather.remove(0));
    }


    /** Render game objects on screen
     */
    private void displayObjects() {

        for (Powerup p: powerups) {
            p.display();
        }

        for (Passenger p: passengers) {
            p.display();
        }

        driver.display();

        for (Car c: cars) {
            c.display();
        }
    }


    /** Update positions of backgrounds, coins and passengers make it look like player
     * is moving in the y-direction
     * @param input The current status of the keyboard being used to control the game
     */
    private void moveForward(Input input) {
        int increase = 0;
        int collRem;
        if (driver.isVisible()) {
            // not in taxi
            collRem = driver.getCollisionMovement();
            if (collRem > 0) {
                // in collision timeout, can't move with controls
                driver.setCollisionMovement(--collRem);
                driver.setPosition(
                        new Point(driver.getPosition().x + driver.getDirection(), driver.getPosition().y));
                increase = -driver.getDirection();
            }
            else if (input.isDown(Keys.UP)) {
                // can move forwards
                increase = driver.getTaxi().getSpeedY();
                driver.moveY(-driver.getSpeedY());
//                increase = driver.getSpeedY();
            }
            else if (input.isDown(Keys.DOWN)) {
                // can move backwards
                driver.moveY(driver.getSpeedY());
//                increase = -driver.getSpeedY();
            }
        }
        else {
            // in taxi
            collRem = driver.getTaxi().getCollisionMovement();
            if (collRem > 0) {
                // in collision timeout, can't move with controls
                driver.getTaxi().setCollisionMovement(--collRem);
                increase = -driver.getTaxi().getDirection();
            }
            else if (input.isDown(Keys.UP)) {
                // can move forwards
                increase = driver.getTaxi().getSpeedY();
            }
        }

        // move background and all entities
        background.moveY(increase);
        for (Powerup p: powerups) {
            p.moveY(increase);
        }
        for (Passenger p: passengers) {
            p.moveY(increase);
        }
        for (Car c: cars) {
            c.moveY(increase);
        }

        // move taxi if not being controlled by driver
        if (driver.isVisible()) {
            driver.getTaxi().moveY(increase);
            if (driver.getPosition().distanceTo(driver.getTaxi().getPosition()) < driver.RADIUS) {
                // get in taxi
                driver.setVisible(false);

                if (driver.getPassenger() != null) {
                    // passenger gets in taxi
                    driver.getPassenger().setApproachTaxi(true);
                }
            }

        }

        // update position of effect sprites
        if (driver.isDead()) {
            driver.incPosOfDeath(increase);
        }
        if (!driver.isVisible() && (driver.getTaxi().getEffectFor() > 0)) {
            Taxi t = driver.getTaxi();
            t.setPosOfEffect(new Point(t.getPosOfEffect().x, t.getPosOfEffect().y + increase));
        }
    }


    /** Display current statistics about game on screen
     */
    private void displayInfo() {
        // get last/current trip information
        trip = driver.getLastTrip();
        if (trip != null) {
            trip.showTripDetails();
        }

        // show on screen text
        FONT.drawString(MSG_PROPS.getProperty("gamePlay.earnings") + String.format("%.2f", totalEarnings),
                EARNINGS_X, EARNINGS_Y);
        FONT.drawString(MSG_PROPS.getProperty("gamePlay.target") + TARGET, TARGET_X, TARGET_Y);
        FONT.drawString(MSG_PROPS.getProperty("gamePlay.remFrames") + (framesLeft), FRAMES_X, FRAMES_Y);

        // show number of frames coin has been active for
        if (driver.getCoin() != null && driver.getCoin().getRemFrames() != 0) {
            FONT.drawString(String.valueOf(COIN_MAX - driver.getCoin().getRemFrames()), COIN_X, COIN_Y);
        }


        if (driver.getPassenger() != null) {
            // display health of passenger
            FONT.drawString(String.format(driver.getPassenger().HEALTH + "%.1f", driver.getPassenger().getHealth()),
                    driver.getPassenger().HEALTH_X, driver.getPassenger().HEALTH_Y);
        }
        else if (!passengers.isEmpty()) {
            // display minimum health of all passengers that have not completed journey
            Passenger minPass = passengers.get(0);
            for (Passenger p: passengers) {
                if (p.getHealth() < minPass.getHealth() && !p.hasArrived()) {
                    minPass = p;
                }
            }
            FONT.drawString(String.format(minPass.HEALTH + "%.1f", minPass.getHealth()),
                    minPass.HEALTH_X, minPass.HEALTH_Y);
        }
    }


    /** Check for collisions between all entities which can take damage
     * and all that can give damage and prompt collision if valid
     */
    private void checkAllCollisions() {
        // cycle through all entities which can damage others
        for (Car attacker: cars) {
            // cycle through all entities which can be damaged
            for (Car victimC: cars) {
                attacker.collide(victimC);
            }
            for (Passenger victimP: passengers) {
                attacker.collide(victimP);
            }

            attacker.collide(driver.getTaxi());

            if (driver.isVisible())
                attacker.collide(driver);
        }
    }


    /** Check if the game has been won or lost or if player can keep playing
     * @return Status of game (won, lost, continue)
     */
    private int checkStatus() {
        for (Passenger p: passengers) {
            if (p.isDead() && p.getBleedingFor() <= 0)
                // automatic loss if passenger dies
                return LOSE;
        }

        if (driver.getTaxi().getPosition().y > Window.getHeight())
            // automatic loss if driver walks out of bounds
            return LOSE;

        else if (driver.isDead() && driver.getBleedingFor() <= 0)
            // automatic loss if driver dies
            return LOSE;

        else if (framesLeft <= 0)
            // automatic loss if game runs for too long
            return LOSE;

        else if (totalEarnings >= TARGET)
            // game is won if points exceed target
            return WIN;

        // can continue playing
        return PLAY;
    }


    /** Adds any new cars (enemy, taxi, other) to game
     */
    private void addCars() {
        // randomly try to generate new cars
        Car newCar = CAR_GEN.generateCar();
        Car newEnemy = CAR_GEN.generateEnemy();
        if (newCar != null) {
            cars.add(newCar);
        }
        if (newEnemy != null) {
            cars.add(newEnemy);
        }

        if (driver.getTaxi().isDead()) {
            // add dead taxi to list of normal cars and then generate a new one
            cars.add(driver.getTaxi());
            driver.newTaxi();
        }
    }


    /** Check if taxi is able ot pick up or drop off a passenger
     * @param input The current status of the keyboard being used to control the game
     */
    private void checkPassenger(Input input) {
        if (input.isUp(Keys.UP) && input.isUp(Keys.DOWN)
            && input.isUp(Keys.RIGHT) && input.isUp(Keys.LEFT)) {
            // taxi/driver is stationary
            Passenger currPass = driver.getPassenger();
            if (currPass == null) {
                // taxi empty and not moving, might be able to pick someone up
                driver.checkPassengers(passengers);
            }

            else if (currPass.isCollected()) {
                // taxi full and not moving
                EndFlag end = trip.getEnd();

                if (!driver.isVisible() && (currPass.getPosition().y <= end.getPosition().y
                        || currPass.getPosition().distanceTo(end.getPosition()) <= end.RADIUS)) {
                    // we can drop off this passenger!
                    trip.updatePenalty(currPass.getPosition());
                    currPass.dropOff(driver);
                    totalEarnings += (trip.getEarnings() - trip.getPenalty());
                }
                else if (currPass.isApproachTaxi()) {
                    // Passenger needs to get back into taxi after being ejected (not yet at end of trip)
                    currPass.setApproachTaxi(!currPass.approach(driver.getTaxi().getPosition()));
                    currPass.setVisible(currPass.isApproachTaxi());
                }
            }
        }
    }
}
import bagel.Font;
import bagel.Image;
import bagel.util.Point;

import java.util.Properties;


/** Abstract class representing all human characters (driver, passenger) in game
 * Persons have a health and may die
 */
public abstract class Person extends GameEntity implements Damagable {
    /** Text used to display health of person on screen
     */
    public final String HEALTH;
    /** X-coord of person's health displayed on screen
     */
    public final int HEALTH_X;
    /** Y-coord of person's health displayed on screen
     */
    public final int HEALTH_Y;
    /** Font used to display health of person on screen
     */
    public final Font FONT;

    /** Speed that Person may move horizontally
     */
    public final int SPEED_X;

    private final Image BLOOD;
    private final int MAX_BLEED;
    private final static int COLLISION_TIMEOUT = 200;
    private static final int MOVE_TIMEOUT = 10;
    private static final int MOVE_SPEED = 2;
    private static final int DOUBLE_MULT = 100;

    private int bleedingFor = 0;
    private double health;
    private int immuneFor = 0;
    private boolean dead;
    private Point posOfDeath = null;
    private int collisionMovement = 0;
    private int direction;

    /** Constructs a Person object (must be called in constructor of subclass)
     * @param gameProps An object containing all game values and graphics
     * @param HEALTH Text used to display health of person on screen
     * @param HEALTH_X X-coord of person's health displayed on screen
     * @param HEALTH_Y Y-coord of person's health displayed on screen
     * @param SPEED_X Speed that Person may move horizontally
     * @param SPRITE The graphic used to represent the Person
     * @param RADIUS The valid collision radius of the Person
     * @param speedY The initial vertical speed of the Person
     * @param position The initial position speed of the Person
     * @param health The initial health points of the Person
     */
    public Person(Properties gameProps, String HEALTH, int HEALTH_X, int HEALTH_Y, int SPEED_X,
                  Image SPRITE, double RADIUS, int speedY, Point position, double health) {
        super(SPRITE, RADIUS, speedY, position);

        this.HEALTH = HEALTH;
        this.HEALTH_X = HEALTH_X;
        this.HEALTH_Y = HEALTH_Y;
        this.SPEED_X = SPEED_X;

        FONT = new Font(gameProps.getProperty("font"),
                Integer.parseInt(gameProps.getProperty("gamePlay.info.fontSize")));
        BLOOD = new Image(gameProps.getProperty("gameObjects.blood.image"));
        MAX_BLEED = Integer.parseInt(gameProps.getProperty("gameObjects.blood.ttl"));

        this.health = health * DOUBLE_MULT;
    }


    /** Displays Person and their blood if dead
     */
    @Override
    public void display() {
        super.display();
        if (immuneFor > 0) {
            immuneFor--;
        }
        if (dead) {
            // display blood
            bleedingFor--;
            BLOOD.draw(posOfDeath.x, posOfDeath.y);
        }
    }


    /** Health is reduced and checked to see if person is still alive
     * @param damage The amount by which the Person's health goes down
     */
    @Override
    public void takeDamage(double damage) {
        health-=damage;
        if (health <= 0) {
            die();
        }
    }


    /** Sets status to dead and initiates bleeding
     */
    @Override
    public void die() {
        dead = true;
        bleedingFor = MAX_BLEED;
        posOfDeath = new Point(getPosition().x, getPosition().y);
    }


    /** Prompts the Person to move away from the entity it collided with
     * @param c The entity that the Person collided with
     */
    @Override
    public void moveAway(Collidable c) {
        collisionMovement = MOVE_TIMEOUT;
        if (c.getPosition().y > getPosition().y) {
            direction = -MOVE_SPEED;
        }
        else {
            direction = MOVE_SPEED;
        }
    }


    /** Updates position of blood spot
     * @param increase Amount by which position of blood changes vertically
     */
    public void incPosOfDeath(int increase) {
        posOfDeath = new Point(posOfDeath.x, posOfDeath.y + increase);
    }


    /** Checks if Person is in collision timeout or in taxi
     * @return Flag representing vulnerability status of Person
     */
    @Override
    public boolean isInvulnerable() {
        return (immuneFor > 0 || !isVisible());
    }


    /** Resets collision timeout to max value
     */
    @Override
    public void setImmuneFor() {
        immuneFor = COLLISION_TIMEOUT;
    }

    public boolean isDead() { return dead; }
    public int getBleedingFor() { return bleedingFor; }
    public double getHealth()  { return health; }
    public double getRadius() { return RADIUS; }
    public int getDirection() { return direction; }
    public int getCollisionMovement() { return collisionMovement; }

    public void setCollisionMovement(int collisionMovement) { this.collisionMovement = collisionMovement; }
}

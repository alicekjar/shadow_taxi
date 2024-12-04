import bagel.Image;
import bagel.util.Point;

import java.util.Properties;

/*
 * Class which implements all cars including taxis, enemy cars
 * and other cars
 */

/** Class representing all cars (enemy cars, taxis and other cars) which may
 * do and take damage
 */
public class Car extends GameEntity implements Damagable, Collidable {
    private final Properties GAME_PROPS;

    private final Image SMOKE;
    public final Image FIRE;

    private final int SMOKE_MAX_FRAMES;
    private final int FIRE_MAX_FRAMES;
    private final double DAMAGE;

    private static final int COLLISION_TIMEOUT = 200;
    private static final int MOVE_TIMEOUT = 10;
    private static final int MOVE_SPEED = 1;
    private static final int DOUBLE_MULT = 100;

    private double health;
    private int effectFor = 0;
    private Image effect;
    private Point posOfEffect = null;
    private boolean dead = false;
    private int immuneFor = 0;
    private int collisionMovement = 0;
    private int direction;


    /** Constructs a Car object
     * @param gameProps An object containing all game values and graphics
     * @param sprite The graphic used to represent the car
     * @param speed_Y The speed at which the car travels forward
     * @param radius The collision radius of the car
     * @param damage How much damage a car will do to an entity it collides with
     * @param position The initial position of the car on screen
     * @param health The initial health points of the car
     */
    public Car(Properties gameProps, Image sprite, int speed_Y, double radius,
               double damage, Point position, double health) {
        super(sprite, radius, speed_Y, position);
        GAME_PROPS = gameProps;

        SMOKE = new Image(gameProps.getProperty("gameObjects.smoke.image"));
        FIRE = new Image(gameProps.getProperty("gameObjects.fire.image"));

        SMOKE_MAX_FRAMES = Integer.parseInt(gameProps.getProperty("gameObjects.smoke.ttl"));
        FIRE_MAX_FRAMES = Integer.parseInt(gameProps.getProperty("gameObjects.fire.ttl"));

        this.health = health * DOUBLE_MULT;
        DAMAGE = damage * DOUBLE_MULT;
    }


    /** Displays car and any of the effects currently applied to it
     */
    @Override
    public void display() {
        super.display();
        if (immuneFor > 0) {
            immuneFor--;
        }

        displayEffect(effect);
    }


    /** Moves the car in the y direction depending on current status
     * @param increase The amount by which the player has moved forward
     */
    @Override
    public void moveY(int increase) {
        if(collisionMovement > 0) {
            // move away from point of collision
            collisionMovement--;
            setPosition(new Point(getPosition().x, getPosition().y + increase + direction));
            if (collisionMovement == 0) {
                // get a new speed
                int newSpeed = CarGenerator.getInstance(GAME_PROPS).getSpeed(this instanceof EnemyCar);
                setSpeedY(newSpeed);
            }
        }
        else if (effectFor > 0){
            // car stalled
            setPosition(new Point(getPosition().x, getPosition().y + increase));
        }
        else {
            // normal forward movement
            setPosition(new Point(getPosition().x, getPosition().y + increase  - getSpeedY()));

        }

        if (posOfEffect != null) {
            posOfEffect = new Point(posOfEffect.x, posOfEffect.y + increase);
        }
    }


    /** Inflicts damage on the car's health, displaying a smoke or fire graphic
     * @param damage The amount by which the car's health decreases
     */
    @Override
    public void takeDamage(double damage) {
        health -= damage;
        if (health <= 0)
            die();
        else {
            effectFor = SMOKE_MAX_FRAMES;
            effect = SMOKE;
            posOfEffect = new Point(getPosition().x, getPosition().y);
        }
    }


    /** When health has been depleted, marks the car as dead and
     * generates a fire graphic
     */
    @Override
    public void die() {
        dead = true;
        effectFor = FIRE_MAX_FRAMES;
        effect = FIRE;
        posOfEffect = new Point(getPosition().x, getPosition().y);
    }


    /** Prompts the car to move away from the entity it collided with
     * @param c The entity that the car collided with
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


    /** Checks if two entities satisfy the requirements to collide
     * @param entity The entity that the car might collide with
     * @return Flag indicating collision is able to occur
     */
    @Override
    public boolean checkCollision(Damagable entity) {
        if (dead || entity.isDead() || entity.isInvulnerable() || entity == this) {
            // entity cannot inflict damage on dead/invulnerable entity, itself or at all if dead
            return false;
        }
        // check if within range
        double collisionDistance = RADIUS + entity.getRadius();
        return getPosition().distanceTo(entity.getPosition()) <= collisionDistance;
    }


    /** If valid, causes two entities to collide with each other
     * @param entity The entity that the car might collide with
     */
    @Override
    public void collide(Damagable entity) {
        if (this.isInvulnerable() && entity.isInvulnerable()) {
            // base case, prevents infinite recursion
            return;
        }

        if (this.checkCollision(entity)) {
            entity.moveAway(this);
            entity.setImmuneFor();

            // inflict damage
            entity.takeDamage(DAMAGE);
        }

        // if victim can also inflict damage (i.e. is a car), do so
        if (entity instanceof Collidable && ((Collidable)entity).checkCollision(this)) {
            ((Collidable)entity).collide(this);
        }
    }


    /** Displays effect car currently has on screen
     * @param effect Image representing which effect (fire/smoke) that is being displayed
     */
    public void displayEffect(Image effect) {
        if (effectFor > 0) {
            effectFor--;
            effect.draw(posOfEffect.x, posOfEffect.y);
            if (effectFor == 0) {
                // stop displaying effect
                posOfEffect = null;
                if (effect.equals(FIRE)) {
                    // stop generating car
                    setVisible(false);
                }
            }
        }
    }

    /** Checks if car is in collision timeout
     * @return Flag representing vulnerability status of car
     */
    @Override
    public boolean isInvulnerable() {
        return (immuneFor > 0);
    }


    /** Resets collision timeout to max value
     */
    @Override
    public void setImmuneFor() {
        immuneFor = COLLISION_TIMEOUT;
    }


    public int getEffectFor() { return effectFor; }
    public boolean isDead() { return dead; }
    public double getHealth() { return health; }
    public double getRadius() { return RADIUS; }
    public Point getPosOfEffect() { return posOfEffect; }
    public int getCollisionMovement() { return collisionMovement; }
    public int getDirection() { return direction; }

    public void setCollisionMovement(int collisionMovement) { this.collisionMovement = collisionMovement; }
    public void setPosOfEffect(Point posOfEffect) {this.posOfEffect = posOfEffect;}
}

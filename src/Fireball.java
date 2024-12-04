import bagel.Image;
import bagel.util.Point;

import java.util.Properties;

/** Class representing a game entity shot by enemy cars which can
 * deal damage
 */
public class Fireball extends GameEntity implements Collidable {
    private final double DAMAGE;
    private static final int DOUBLE_MULT = 100;

    /** Constructs a Fireball object
     * @param gameProps An object containing all game values and graphics
     * @param position The initial position of the fireball on screen
     */
    public Fireball(Properties gameProps, Point position) {
        super(new Image(gameProps.getProperty("gameObjects.fireball.image")),
                Double.parseDouble(gameProps.getProperty("gameObjects.fireball.radius")),
                Integer.parseInt(gameProps.getProperty("gameObjects.fireball.shootSpeedY")), position);

        DAMAGE = Double.parseDouble(gameProps.getProperty("gameObjects.fireball.damage")) * DOUBLE_MULT;
    }


    /** Checks for valid collision between fireball and entity
     * @param entity The entity that the fireball might collide with
     * @return Flag indicating collision is able to occur
     */
    @Override
    public boolean checkCollision(Damagable entity) {
        if (entity.isDead() || entity.isInvulnerable() || !isVisible()) {
            return false;
        }

        // check if close enough to collide
        double collisionDistance = RADIUS + entity.getRadius();
        return getPosition().distanceTo(entity.getPosition()) <= collisionDistance;
    }


    /** If valid, causes fireball and entity to collide
     * @param entity The entity that the fireball might collide with
     */
    @Override
    public void collide(Damagable entity) {
        if (this.checkCollision(entity)) {
            // valid collision
            entity.setImmuneFor();
            entity.moveAway(this);
            entity.takeDamage(DAMAGE);

            // fireballs disappear after collision
            setVisible(false);
        }
    }

}

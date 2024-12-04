import bagel.util.Point;

/** Interface representing the ability to inflict damage onto
 * other entities following a collision
 */
public interface Collidable {
    /** Checks if two entities satisfy the requirements to collide
     * @param entity The entity that the Collidable object might collide with
     * @return Flag indicating collision is able to occur
     */
    boolean checkCollision(Damagable entity);

    /** If valid, causes two entities to collide with each other
     * @param entity The entity that the Collidable might collide with
     */
    void collide(Damagable entity);

    Point getPosition();
}

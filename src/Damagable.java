import bagel.util.Point;

/** Interface representing the ability to take damage and
 * die if health is below zero
 */
public interface Damagable {
    /** Health is reduced and checked to see if Damagable is still alive
     * @param damage The amount by which the Damagable's health goes down
     */
    void takeDamage(double damage);

    /** Prompts the Damagable to move away from the entity it collided with
     * @param c The entity that the Damagable collided with
     */
    void moveAway(Collidable c);

    /** Sets status to dead and initiate displaying of effect
     */
    void die();

    /** Checks if Damagable is able to be damaged
     * @return Flag representing vulnerability status of Damagable
     */
    boolean isInvulnerable();

    /** Resets collision timeout to max value
     */
    void setImmuneFor();


    double getRadius();
    Point getPosition();
    boolean isDead();

}

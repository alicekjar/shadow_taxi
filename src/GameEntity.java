import bagel.Image;
import bagel.util.Point;

/** An abstract class representing physical entities that exist in the game
 * Entities may all be moved in the y-direction and displayed on screen
 */
public abstract class GameEntity implements Displayable {
    private final Image SPRITE;

    /** The valid collision radius of the entity
     */
    public final double RADIUS;

    private int speedY;
    private Point position;
    private boolean visible = true;

    /** Constructs a GameEntity object (must be called in constructor of subclass)
     * @param SPRITE The graphic used to represent the entity
     * @param RADIUS The valid collision radius of the entity
     * @param speedY The initial vertical speed of the entity
     * @param position The initial position of the entity in relation to the screen
     */
    public GameEntity(Image SPRITE, double RADIUS, int speedY, Point position) {
        this.SPRITE = SPRITE;
        this.RADIUS = RADIUS;
        this.speedY = speedY;
        this.position = position;
    }

    /** Default implementation for showing entity on screen
     */
    @Override
    public void display() {
        if (visible)
            SPRITE.draw(position.x, position.y);
    }


    /** Default implementation for changing y-position of entity
     */
    public void moveY(int dist) {
        position = new Point(position.x, position.y + dist);
    }


    public Point getPosition() { return position; }
    public int getSpeedY() { return speedY; }
    public boolean isVisible() { return visible; }

    public void setVisible(boolean visible) { this.visible = visible; }
    public void setPosition(Point position) { this.position = position; }
    public void setSpeedY(int speedY) { this.speedY = speedY; }
}

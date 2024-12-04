import bagel.Image;
import bagel.util.Point;

/** Abstract class giving functionality to game objects which may be
 * collected by character to give special abilities
 */
public abstract class Powerup extends GameEntity {
    /** The number of frames that the powerup lasts
     */
    public final int MAX_FRAMES;

    private boolean collected = false;
    private int remFrames = 0;

    /** Constructs a Powerup Object (must be called in constructor of subclass)
     * @param SPRITE The graphic used to represent the Powerup
     * @param RADIUS The valid pick-up radius of the Powerup
     * @param MAX_FRAMES The number of frames that the powerup lasts
     * @param position The initial position of the Powerup
     */
    public Powerup(Image SPRITE, double RADIUS, int MAX_FRAMES, Point position) {
        super(SPRITE, RADIUS, 0, position);
        this.MAX_FRAMES = MAX_FRAMES;

        remFrames = MAX_FRAMES;
    }


    /** Stops powerup from being displayed
     */
    public void collect() {
        collected = true;
        setVisible(false);
    }


    /** Reduces the remaining frames that the power-up is active for
     * @return Itself if still active, else null
     */
    public Powerup reduceRemFrames() {
        remFrames--;
        if (remFrames <= 0) {
            return null;
        }
        return this;
    }


    public boolean isCollected() { return collected; }
    public int getRemFrames() { return remFrames; }
}

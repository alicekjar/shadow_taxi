import bagel.Font;
import java.util.Properties;

/** Class representing the opening title screen of the game
 */
public class HomeScreen extends Screen {

    private final String TITLE;
    private final String INSTR;

    private final Font TITLE_FONT;
    private final Font INSTR_FONT;

    private final int TITLE_Y;
    private final int INSTR_Y;

    /** Constructs a HomeScreen object
     * @param gameProps An object containing all game values and graphics
     * @param msgProps An object containing all text used in the game
     */
    public HomeScreen(Properties gameProps, Properties msgProps) {
        super(gameProps.getProperty("backgroundImage.home"));

        TITLE = msgProps.getProperty("home.title");
        INSTR = msgProps.getProperty("home.instruction");

        TITLE_FONT = new Font(gameProps.getProperty("font"),
                Integer.parseInt(gameProps.getProperty("home.title.fontSize")));
        INSTR_FONT = new Font(gameProps.getProperty("font"),
                Integer.parseInt(gameProps.getProperty("home.instruction.fontSize")));

        TITLE_Y = Integer.parseInt(gameProps.getProperty("home.title.y"));
        INSTR_Y = Integer.parseInt(gameProps.getProperty("home.instruction.y"));

    }


    /** Displays the home screen
     */
    @Override
    public void display() {
        drawBackground();
        TITLE_FONT.drawString(TITLE, centre(TITLE, TITLE_FONT), TITLE_Y);
        INSTR_FONT.drawString(INSTR, centre(INSTR, INSTR_FONT), INSTR_Y);
    }
}

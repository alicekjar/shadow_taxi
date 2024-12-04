import bagel.DrawOptions;
import bagel.Font;
import bagel.Input;
import bagel.Keys;

import java.util.Properties;


/** Class representing a pre-game screen that instructs player to
 * input name to be stored for score-keeping purposes
 */
public class InfoScreen extends Screen {

    private final String ENTER_NAME;
    private final String START;

    private final Font FONT;

    private final int ENTER_NAME_Y;
    private final int NAME_Y;
    private final int START_Y;

    private final DrawOptions FONT_STYLE;

    private String name;

    /** Constructs an InfoScreen object
     * @param gameProps An object containing all game values and graphics
     * @param msgProps An object containing all text used in the game
     */
    public InfoScreen(Properties gameProps, Properties msgProps) {
        super(gameProps.getProperty("backgroundImage.playerInfo"));

        ENTER_NAME = msgProps.getProperty("playerInfo.playerName");
        START = msgProps.getProperty("playerInfo.start");

        FONT = new Font(gameProps.getProperty("font"),
                Integer.parseInt(gameProps.getProperty("playerInfo.fontSize")));

        ENTER_NAME_Y = Integer.parseInt(gameProps.getProperty("playerInfo.playerName.y"));
        START_Y = Integer.parseInt(gameProps.getProperty("playerInfo.start.y"));
        NAME_Y = Integer.parseInt(gameProps.getProperty("playerInfo.playerNameInput.y"));

        FONT_STYLE = new DrawOptions();
        FONT_STYLE.setBlendColour(0, 0, 0);

    }


    /** Displays player information screen
     */
    @Override
    public void display() {
        drawBackground();
        FONT.drawString(ENTER_NAME, centre(ENTER_NAME, FONT), ENTER_NAME_Y);
        FONT.drawString(START, centre(START, FONT), START_Y);
        FONT.drawString(name, centre(name, FONT), NAME_Y, FONT_STYLE);
    }


    /** Parses information from keyboard to input player name
     * @param input The current status of the keyboard being used to type
     * @return The name that the user has typed so far
     */
    public String updateName(Input input) {
        if (MiscUtils.getKeyPress(input) != null)
            // add new letters typed
            name += MiscUtils.getKeyPress(input);

        // allow user to delete existing characters
        if (!name.isEmpty() && (input.wasPressed(Keys.DELETE) || input.wasPressed(Keys.BACKSPACE)))
            name = name.substring(0, name.length() - 1);

        return name;
    }

    public void setName(String name) { this.name = name; }
}

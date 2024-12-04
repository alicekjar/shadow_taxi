import bagel.*;
import java.util.Properties;

/**
 * Implementation of SWEN20003 Project 2, Semester 2, 2024
 * NOTE: COMPONENTS OF THIS CLASS WERE WRITTEN AND PROVIDED BY THE SWEN20003 TEACHING TEAM (AS MARKED)
 * @author Alice Kjar
 */
public class ShadowTaxi extends AbstractGame {
    private final Properties GAME_PROPS;
    private final Properties MESSAGE_PROPS;

    // screen being displayed
    private static final int HOME = 1;
    private static final int INFO = 2;
    private static final int GAME = 3;
    private static final int END = 4;

    // game status
    private static final int PLAY = 0;

    private static final String NO_NAME = "";

    private String name;
    private int screen = HOME;
    private int status = PLAY;
    private boolean newGame = true;

    // game screens
    private HomeScreen home;
    private EndScreen end;
    private InfoScreen info;
    private Game game;

    public ShadowTaxi(Properties gameProps, Properties messageProps) {
        super(Integer.parseInt(gameProps.getProperty("window.width")),
                Integer.parseInt(gameProps.getProperty("window.height")),
                messageProps.getProperty("home.title"));

        this.GAME_PROPS = gameProps;
        this.MESSAGE_PROPS = messageProps;

        // initialise game screens
        home = new HomeScreen(GAME_PROPS, MESSAGE_PROPS);
        info = new InfoScreen(GAME_PROPS, MESSAGE_PROPS);
        game = new Game(GAME_PROPS, MESSAGE_PROPS);
        end = new EndScreen(GAME_PROPS, MESSAGE_PROPS);
    }

    /**
     * Render the relevant screens and game objects based on the keyboard input
     * given by the user and the status of the game play.
     * @param input The current mouse/keyboard input.
     */
    @Override
    protected void update(Input input) {

        if (input.wasPressed(Keys.ESCAPE)){
            // exit game
            Window.close();
        }

        if (screen == HOME) {
            // show home screen
            if (newGame) {
                // reset game data
                info.setName(NO_NAME);
                game = new Game(GAME_PROPS, MESSAGE_PROPS);
                newGame = false;
            }

            home.display();

            if (input.wasPressed(Keys.ENTER)) {
                screen = INFO;
            }
        }

        else if (screen == INFO) {
            // show player information screen
            info.display();
            name = info.updateName(input);

            if (input.wasPressed(Keys.ENTER)) {
                screen = GAME;
            }
        }

        else if (screen == GAME) {
            status = game.playGame(input, name);

            if (status != PLAY) {
                // game over
                end.setStatus(status);
                screen = END;
            }

        }

        else if (screen == END) {
            // show endgame screen
            end.display();

            if (input.wasPressed(Keys.SPACE)) {
                // restart with new player
                screen = HOME;
                newGame = true;
            }
        }
    }


    /** Main method through which program is called
     * NOTE: THIS METHOD WAS WRITTEN AND PROVIDED BY THE SWEN20003 TEACHING TEAM
     * @author SWEN20003 Teaching Team
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        Properties game_props = IOUtils.readPropertiesFile("res/app.properties");
        Properties message_props = IOUtils.readPropertiesFile("res/message_en.properties");
        ShadowTaxi game = new ShadowTaxi(game_props, message_props);
        game.run();
    }
}

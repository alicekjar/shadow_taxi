import bagel.Font;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Properties;

/*

 */

/** Class used to display the status of the game upon completion
 * along with leaderboard of top 5 highest scores
 */
public class EndScreen extends Screen {
    private final String SCORES_FILE;

    private final String GAME_WON;
    private final String GAME_LOST;
    private final String SCOREBOARD;

    private final Font STATUS_FONT;
    private final Font SCORE_FONT;

    private final int STATUS_Y;
    private final int SCORE_Y;

    private static final int SCORE_Y_INCR = 40;
    private static final int MAX_SCORES = 5;
    private static final int LOSE = -1;

    private int status = 0;

    /** Constructs a new EndScreen object
     * @param gameProps An object containing all game values and graphics
     * @param messageProps An object containing all text used in the game
     */
    public EndScreen(Properties gameProps, Properties messageProps) {
        super(gameProps.getProperty("backgroundImage.gameEnd"));

        SCORES_FILE = gameProps.getProperty("gameEnd.scoresFile");

        GAME_WON = messageProps.getProperty("gameEnd.won");
        GAME_LOST = messageProps.getProperty("gameEnd.lost");
        SCOREBOARD = messageProps.getProperty("gameEnd.highestScores");

        STATUS_FONT = new Font(gameProps.getProperty("font"),
                Integer.parseInt(gameProps.getProperty("gameEnd.status.fontSize")));
        SCORE_FONT = new Font(gameProps.getProperty("font"),
                Integer.parseInt(gameProps.getProperty("gameEnd.scores.fontSize")));

        STATUS_Y = Integer.parseInt(gameProps.getProperty("gameEnd.status.y"));
        SCORE_Y = Integer.parseInt(gameProps.getProperty("gameEnd.scores.y"));
    }


    /** Displays screen
     */
    @Override
    public void display() {
        drawBackground();

        SCORE_FONT.drawString(SCOREBOARD, centre(SCOREBOARD, SCORE_FONT), SCORE_Y);

        ArrayList<Score> scores = getScores();

        for (int i = 0; i < MAX_SCORES; i++) {
            // show each of the scores
            try {
                String formattedScore = String.format("%s - %.2f", scores.get(i).NAME, scores.get(i).SCORE);
                SCORE_FONT.drawString(formattedScore,
                        centre(formattedScore, SCORE_FONT),
                        SCORE_Y + ((i + 1) * SCORE_Y_INCR));
            }
            catch (IndexOutOfBoundsException e) {
                // no more scores to display
                break;
            }
        }

        // Show win/lose message
        if (status == LOSE) {
            STATUS_FONT.drawString(GAME_LOST, centre(GAME_LOST, STATUS_FONT), STATUS_Y);
        }
        else {
            STATUS_FONT.drawString(GAME_WON, centre(GAME_LOST, STATUS_FONT), STATUS_Y);
        }

    }


    /** Coverts stored scores from strings to Score objects
     * @return A sorted list of all previous scores and who achieved them
     */
    private ArrayList<Score> getScores() {
        String[][] strScores = IOUtils.readCommaSeparatedFile(SCORES_FILE);

        ArrayList<Score> scores = new ArrayList<Score>();

        for (String[] s: strScores) {
            // add new score
            scores.add(new Score(s[0], Double.parseDouble(s[1])));
        }

        // sort by score in descending order
        Collections.sort(scores);
        return scores;
    }

    public void setStatus(int status) { this.status = status; }
}

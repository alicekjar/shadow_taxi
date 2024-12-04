import bagel.Font;
import bagel.util.Point;

import java.util.Properties;

/** Class containing details of a trip corresponding to a
 * given passenger
 */
public class Trip {
    private final Properties GAME_PROPS;
    private final Properties MSG_PROPS;

    private final int TRIP_INFO_X;
    private final int TRIP_INFO_Y;
    private final Font FONT;

    private final int PRIORITY_1;
    private final int PRIORITY_2;
    private final int PRIORITY_3;

    private final double RATE_PER_Y;
    private final double PENALTY_PER_Y;

    private static final int TRIP_INFO_OFFSET_1 = 30;
    private static final int TRIP_INFO_OFFSET_2 = 60;
    private static final int TRIP_INFO_OFFSET_3 = 90;

    private static final int PRIORITY_MIN = 1;

    private final int DIST_Y;
    private final boolean HAS_UMBRELLA;
    private Point endPosition;
    private EndFlag end;

    private double earnings;
    private double penalty = 0;
    private boolean p_changed = false;
    private boolean raining = false;
    private int priority;
    private int originalPriority;
    private boolean complete = false;


    /** Constructs a Trip object
     * @param gameProps
     * @param messageProps
     * @param start Position at which the trip starts
     * @param priority Value determining additional payment for trip
     * @param endX X-coord of trip end flag
     * @param distY Distance in the y-direction between start and end
     * @param hasUmbrella Value determining if priority will be effected by rain
     */
    public Trip(Properties gameProps, Properties messageProps, Point start, int priority, int endX,
                int distY, boolean hasUmbrella) {
        GAME_PROPS = gameProps;
        MSG_PROPS = messageProps;

        TRIP_INFO_X = Integer.parseInt(GAME_PROPS.getProperty("gamePlay.tripInfo.x"));
        TRIP_INFO_Y = Integer.parseInt(GAME_PROPS.getProperty("gamePlay.tripInfo.y"));
        FONT = new Font(gameProps.getProperty("font"),
                Integer.parseInt(gameProps.getProperty("gamePlay.info.fontSize")));

        PRIORITY_1 = Integer.parseInt(GAME_PROPS.getProperty("trip.rate.priority1"));
        PRIORITY_2 = Integer.parseInt(GAME_PROPS.getProperty("trip.rate.priority2"));
        PRIORITY_3 = Integer.parseInt(GAME_PROPS.getProperty("trip.rate.priority3"));

        RATE_PER_Y = Double.parseDouble(GAME_PROPS.getProperty("trip.rate.perY"));
        PENALTY_PER_Y = Double.parseDouble(GAME_PROPS.getProperty("trip.penalty.perY"));

        HAS_UMBRELLA = hasUmbrella;
        this.priority = priority;
        originalPriority = priority;
        DIST_Y = distY;
        endPosition = new Point(endX, start.y - distY);
        end = new EndFlag(GAME_PROPS, endPosition);
        updateEarnings();
    }


    /** Updates end position of trip
     * @param increase Amount by which end position changes
     */
    public void moveY(int increase) {
        endPosition = new Point(endPosition.x, endPosition.y + increase);
        end.moveY(increase);
    }


    /** Recalculates expected earnings from trip based on current priority
     */
    public void updateEarnings() {
        double priorityFee;
        if (priority == 1) {
            priorityFee = PRIORITY_1;
        }
        else if (priority == 2) {
            priorityFee = PRIORITY_2;
        }
        else {
            priorityFee = PRIORITY_3;
        }
        earnings = RATE_PER_Y * (DIST_Y) + priorityFee;
    }


    /** Displays information about last/current trip
     */
    public void showTripDetails() {
        FONT.drawString(MSG_PROPS.getProperty("gamePlay.trip.expectedEarning") + String.format("%.1f", earnings),
                TRIP_INFO_X, TRIP_INFO_Y + TRIP_INFO_OFFSET_1);

        FONT.drawString(String.format(MSG_PROPS.getProperty("gamePlay.trip.priority") + priority),
                TRIP_INFO_X, TRIP_INFO_Y + TRIP_INFO_OFFSET_2);

        if (!complete) {
            // show current trip info
            FONT.drawString(String.format(MSG_PROPS.getProperty("gamePlay.onGoingTrip.title")),
                    TRIP_INFO_X, TRIP_INFO_Y);
        }
        else {
            // show last trip info
            FONT.drawString(String.format(MSG_PROPS.getProperty("gamePlay.completedTrip.title")),
                    TRIP_INFO_X, TRIP_INFO_Y);

            FONT.drawString(MSG_PROPS.getProperty("gamePlay.trip.penalty") + String.format("%.2f", penalty),
                    TRIP_INFO_X, TRIP_INFO_Y + TRIP_INFO_OFFSET_3);
        }
    }


    /** Updates the priority (and subsequent expected earnings) of passenger
     * @param weather Flag indicating if this change is occuring because of a change in weather
     * @param decrease Flag indicating if this change is causing a decrease
     */
    public void updatePriority(boolean weather, boolean decrease) {
        if (complete) {
            return;
        }
        if (decrease && weather && !HAS_UMBRELLA) {
            raining = true;
            priority = PRIORITY_MIN;
        }
        if (!decrease && weather && !HAS_UMBRELLA) {
            raining = false;
            priority = originalPriority;
            if (p_changed)
                priority--;
        }

        if (decrease && !weather & originalPriority != PRIORITY_MIN) {
            p_changed = true;
            if (priority != PRIORITY_MIN) {
                priority--;
            }
        }

        else if (!decrease && !weather && p_changed) {
            p_changed = false;
            if (!raining) {
                priority++;
            }
        }
        updateEarnings();
    }


    /** Calculates penalty based on distance from drop off
     * @param taxiPos Position of taxi when passenger was dropped off
     */
    public void updatePenalty(Point taxiPos) {
        complete = true;
        if (taxiPos.distanceTo(end.getPosition()) <= end.RADIUS) {
            // no penalty, within radius
            penalty = 0;
        }
        else if (taxiPos.y <= end.getPosition().y) {
            // calculate penalty
            penalty = (end.getPosition().y - taxiPos.y) * PENALTY_PER_Y;
            if (penalty > earnings) {
                // can't have negative earnings
                penalty = earnings;
            }
        }
    }

    public double getEarnings() { return earnings; }
    public int getPriority() { return priority; }
    public EndFlag getEnd() { return end; }
    public double getPenalty() { return penalty; }
}

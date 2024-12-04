/** Class containing information about a singular in-game weather event
 */
public class Weather implements Comparable<Weather> {
    /** The weather in this event
     */
    public String WEATHER;

    /** Frame at which the weather event starts
     */
    public int START;

    /** Frame at which the weather event ends
     */
    public int END;

    /** Constructs a Weather object
     * @param WEATHER The weather in this event
     * @param START Frame at which the weather event starts
     * @param END Frame at which the weather event ends
     */
    public Weather(String WEATHER, int START, int END) {
        this.WEATHER = WEATHER;
        this.START = START;
        this.END = END;
    }


    /** Allows us to sort weather events by their start time in ascending order
     * @param w2 the object to be compared.
     * @return Positive number if this starts after w2, 0 if start at same time, else negative
     */
    @Override
    public int compareTo(Weather w2) {
        return this.START - w2.START;
    }
}

/** Class used to store name and score of a previous player
 */
public class Score implements Comparable<Score> {
    /** The name provided by the player on the player information screen
     */
    public final String NAME;

    /** The score the player achieved
     */
    public final double SCORE;

    /** Constructs a Score object
     * @param name The name provided by the player
     * @param score The score the player achieved
     */
    public Score(String name, double score) {
        NAME = name;
        SCORE = score;
    }


    /** Compares two scores for sorting in descending order by SCORE
     * @param s2 the object to be compared.
     * @return Negative number if this is greater than s2, 0 if equal, else a positive number
     */
    @Override
    public int compareTo(Score s2) {
        return -(Double.compare(this.SCORE, s2.SCORE));
    }
}

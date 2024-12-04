import java.io.*;
import java.util.Properties;

/**
 * A utility class that provides methods to read and write files.
 * NOTE: THIS CLASS WAS WRITTEN AND PROVIDED BY THE SWEN20003 TEACHING TEAM
 * @author SWEN20003 Teaching Team
 */
public class IOUtils {

    /***
     * Read a file and return a list of String arrays
     * NOTE: THIS METHOD WAS WRITTEN AND PROVIDED BY THE SWEN20003 TEACHING TEAM
     * @author SWEN20003 Teaching Team
     * @param file: the path to the CSV file
     * @return: String[][]. Each String[] array represents elements in a single line in the CSV file
     */
    public static String[][] readCommaSeparatedFile(String file) {
        try {
            // checking number of lines in file
            BufferedReader reader = new BufferedReader(new FileReader(file));
            int numLines = 0;

            while (reader.readLine() != null) {
                numLines++;
            }
            reader.close();

            reader = new BufferedReader(new FileReader(file));
            String[][] lines = new String[numLines][];
            String textRead;
            int lineIndex = 0;

            while ((textRead = reader.readLine()) != null) {
                String[] splitText = textRead.split(",");
                lines[lineIndex++] = splitText;
            }
            return lines;

        } catch (IOException ex) {
            ex.printStackTrace();
            System.exit(-1);
        }

        return null;
    }

    /***
     * Read a properties file and return a Properties object
     * NOTE: THIS METHOD WAS WRITTEN AND PROVIDED BY THE SWEN20003 TEACHING TEAM
     * @author SWEN20003 Teaching Team
     * @param configFile: the path to the properties file
     * @return: Properties object
     */
    public static Properties readPropertiesFile(String configFile) {
        Properties appProps = new Properties();
        try {
            appProps.load(new FileInputStream(configFile));
        } catch(IOException ex) {
            ex.printStackTrace();
            System.exit(-1);
        }

        return appProps;
    }

    /**
     * Write a score to a file
     * NOTE: THIS METHOD WAS WRITTEN AND PROVIDED BY THE SWEN20003 TEACHING TEAM
     * @author SWEN20003 Teaching Team
     * @param filename The name of the file
     * @param score The score to be written
     */
    public static void writeScoreToFile(String filename, String score) {
        try(FileWriter fw = new FileWriter(filename, true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter out = new PrintWriter(bw))
        {
            out.println(score);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
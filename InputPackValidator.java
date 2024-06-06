import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * This class is designed to validate the inputed card pack.
 * An input pack is considered valid if every line in the pack is a non-negative integer,
 * and the total number of lines is a multiple of 8 times the number of players.
 */
public class InputPackValidator {

    // The number of players for which the input pack is being validated
    private int numOfPlayers;

    /**
     * Constructor to create an InputPackValidator object.
     * @param numOfPlayers The number of players in the game.
     */
    public InputPackValidator(int numOfPlayers){
        // Initialize the number of players
        this.numOfPlayers=numOfPlayers;
    }

    /**
     * Validates the input pack by reading from a file and checking its contents.
     * @param filePath The path to the file containing the input card pack data.
     * @return true if the input pack is valid, false otherwise.
     * @throws IOException If an I/O error occurs when reading from the file.
     */
    public boolean isValidInputPack(String filePath) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            int rowCount = 0;
            //Loop through every line of the file
            while ((line = reader.readLine()) != null) {
                // Check if the line is a non-negative integer
                if (!line.matches("\\d+")) {
                    return false;
                }
                rowCount++;
            }
            // Return true if the total number of lines is equal to 8 multiplied by the number of players.
            // Otherwise, return false
            return rowCount == 8*numOfPlayers;
        } catch (IOException e) {
            e.printStackTrace();
            return false;   // Return false if an exception is caught
        }
    }
}
    
import java.util.ArrayList;
import java.util.Random;
import java.io.*;

/**
 * The Player class represents a player in a card game.
 * The player interacts with two decks (left and right) and maintains a hand of cards.
 */
public class Player extends Thread {
    private final int playerNumber; // The unique number of this player
    private final Deck leftDeck; // The deck to the player's left
    private final Deck rightDeck; // The deck to the player's right
    private ArrayList<Card> hand =new ArrayList<Card>(); // The player's current hand of cards
    private String fileName=""; // The file name where the player's actions will be logged
    private static volatile boolean isGameWon =false ; // A flag to indicate if the game has been won
    private ArrayList<Player> players= new ArrayList<Player>(); // A list of all players in the game
 
 
    /**
     * The main game logic for the player.
     * It runs in a loop until the game is won or the thread is interrupted.
     */
    public void run (){
        // Initial Hand
        
        while(!isGameWon && !Thread.currentThread().isInterrupted()){
            try {Thread.sleep(100); // thread is paused for 100 miliseconds
            if (!isWinningHand()) {
                
            {
                draw(); // Draws a card from left deck to Hand
                discard(); // Discards a card from Hand to right deck
                writeCurrentHand(); // Log the current hand
                    
            }
        }

            if(isWinningHand()){
                interruptAllPlayers(); // Interrupt all other player threads if this player wins
                isGameWon=true; // Set the game won flag to true
                result();
                break;
                
            } 
        
            writeToFile("");
            } catch(InterruptedException e){
                Thread.currentThread().interrupt(); // Will interrupt the thread when exception is caught
                break;
            }
                
        }
    }

    /**
     * Constructor for the Player class.
     * Initializes the player number and associated decks.
     * @param playerNumber The unique identifier for this player.
     * @param left The left deck from which this player will draw cards.
     * @param right The right deck to which this player will discard cards.
     */
    public Player(int playerNumber, Deck left, Deck right){
        this.playerNumber=playerNumber;
        leftDeck=left;
        rightDeck=right;
        fileName= "Player"+Integer.toString(playerNumber)+"_output.txt"; 

    }

    public void setPlayers(ArrayList<Player> givenPlayers){
        this.players=givenPlayers;
    }


    public void addCard(Card card){
        hand.add(card);
    }

    public boolean removeCard(Card card){
         return hand.remove(card);
    }

    public ArrayList<Card> getHand() {
        return hand;
    }

    public Deck getLeftDeck(){
        return leftDeck;
    }
    
    public Deck getRightDeck(){
        return rightDeck;
    }

    public int getPlayerNumber() {
        return playerNumber;
    }

    public String getPlayerName(){
        String str= "player "+Integer.toString(playerNumber);
        return str;
    }

    /**
     * Checks if the current hand is a winning hand.
     * A winning hand is defined as all cards in the hand having the same value.
     * @return True if the hand is a winning hand, false otherwise.
     */
    public boolean isWinningHand(){
        // Get the first card in the player's hand
        Card card1=hand.get(0);
        boolean winnngHand=false;
        // Checks every card in the players hand and returns true if all cards are equal to the first card
        for (Card item : hand){
            if(card1.getValue()==item.getValue()){
                winnngHand=true;
            }
            else{
                return false;
            }
        }
        return winnngHand;
    }

    /**
     * Writes every player's final hand to their respective output file when a player has won
     */
    public void result(){
        // Writes the which player won and their winning hand to their output file.
        String winner = "player "+ playerNumber+" wins\nplayer "+playerNumber+" exits\nplayer "+playerNumber+ " final hand "+getContentsStr();
        writeToFile(winner);
        for (Player player :players){
            if (player.getPlayerNumber()!=playerNumber){
                player.writeToFile("player "+playerNumber+" has informed "+player.getPlayerName()+" that player "+playerNumber+" has won\n");
                player.writeToFile(player.getPlayerName()+" exits\n"+player.getPlayerName()+" final hand "+player.getContentsStr());
            }
        }

        // Print to the console which player won and what their final hand was.
        System.out.println(winner);
    }

    /**
    * Attempts to discard a card from the player's hand to the right deck.
    * This method is synchronized to ensure thread safety.
    */
    public synchronized void discard(){
        // Creates a new random number generator object.
        Random rand = new Random();
        // Synchronize on the right deck to ensure exclusive access.
        synchronized (rightDeck){
            while (true){
                // Generate a random number between 0 and 3 inclusive.
                int index = rand.nextInt(4);
                // Randomly select a card to discard.
                Card card = hand.get(index);
                // If the right deck has 4 or more cards, wait until it's not full.
                if (rightDeck.getSize() >=4) {  
                    try {
                        rightDeck.wait();    // Wait for the right deck to have space.
                    } catch (InterruptedException e) {
                        rightDeck.notifyAll();  // Notify other threads if interrupted.
                        return; // Exit the method if interrupted.
                    }
                    rightDeck.notifyAll();
                }
                // Check that the card value isn't equal to the player's number.
                if (card.getValue()!=playerNumber){
                    removeCard(card);   // Removes the card from the player's hand.
                    rightDeck.addCard(card);    // Adds the card to the right deck.
                    // Write the discard action to output file.
                    String output1 = "Player "+playerNumber+" discards "+card.getValue()+" to deck "+rightDeck.getDeckNumber()+"\n";
                    writeToFile(output1);
                    break;  // Exit the loop after discarding.
                }
            }
    }
    }
    /**
    * Draws a card from the left deck to the player's hand.
    * This method is synchronized to ensure thread safety.
     */
    public synchronized void draw(){
        // Synchronize on the left deck to ensure exclusive access.
        synchronized(leftDeck){
            while (leftDeck.getSize() == 0) {   // Check that the left deck isn't empty.
                try{
                    leftDeck.wait();    // Wait for the left deck to have cards.
                }catch(InterruptedException e ){
                    e.printStackTrace();
                    return; // Exit the method if interrupted.
                }
                leftDeck.notifyAll();
                break;
            }
            // Remove card from left deck.
            Card card = leftDeck.removeCard();
            // Add card to player's hand.
            hand.add(card);
            // Write discard action to player's output file.
            String output1 = "Player "+playerNumber+" draws a "+card.getValue()+" from deck "+leftDeck.getDeckNumber()+"\n";
            writeToFile(output1);
        }

    }
    /**
     * Write the player's current hand contents to external output text file
     */
    public synchronized void writeCurrentHand(){
        String output = "Player "+playerNumber+" current hand is "+ getContentsStr()+"\n";
        writeToFile(output);
    }

    /**
     * Initializes the output file for the player.
     * Creates the file if it does not exist, and clears it if it does.
     */
    public void initualiseFile(){
        File file = new File(fileName);
        try{
            file.createNewFile();
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        //Clears contents of file
        try{
           // Create a FileWriter to write to a file with the specified name.
            FileWriter writer = new FileWriter(fileName);

            // Wrap the FileWriter in a BufferedWriter for efficient writing.
            BufferedWriter buffwriter = new BufferedWriter(writer);

            // Use the BufferedWriter to write an empty string to the file.
            // This has the effect of clearing the file if it already contains text.
            buffwriter.write("");

            buffwriter.close();

        }catch(IOException e){
            e.printStackTrace();
        }
        

    }

    /**
     * Return the players hand contents as a string
     * @return A string containing the player's contents
     */
    public String getContentsStr(){
        String str="";
        for(Card card : hand){
            str=str+Integer.toString(card.getValue())+" ";
        }

        return str;
    }

    public void initualHand(){
        writeToFile("player "+playerNumber+" initial hand "+getContentsStr()+"\n");
    }


    /**
     * Appends a given string to the file with the name specified by the `fileName` field.
     * @param line The string to be written to the file.
     */
    public void writeToFile(String line) {
        try {
            // Create a FileWriter to append to the file
            FileWriter writer = new FileWriter(fileName, true);
            // Wrap the FileWriter in a BufferedWriter.
            BufferedWriter buffwriter = new BufferedWriter(writer);
            // Write the string to the file.
            buffwriter.write(line);
            buffwriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean getIsGameWon(){
        return isGameWon;
    }

    /**
     * Interrupts all player threads.
     * This is used to stop the game when a winning hand is detected.
     */
    public void interruptAllPlayers(){
        for(Player player:players){
            player.interrupt();
        }
    }
    
}
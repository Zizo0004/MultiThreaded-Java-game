import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
/*
 * The Deck class represents a deck of cards in the game.
 */
public class Deck extends Thread{

    private final int deckNumber; // Unique identifier for the deck
    private ArrayList<Card> deckContents = new ArrayList<Card>(); // Stores the cards currently in the deck
    private String fileName;  // Filename where the deck's actions will be logged

    /**
     * Constructor for deck of cards
     * @param deckNumber The unique card ID number
     */
    public Deck (int deckNumber){
        this.deckNumber=deckNumber;
        fileName= "Deck"+Integer.toString(deckNumber)+"_output.txt";
    }

    public ArrayList<Card> getDeckContents(){
        return deckContents;
    }

    public synchronized void addCard(Card card){
        deckContents.add(card);
        // Notify any waiting threads that the deck is no longer empty.
        notifyAll();
    }

    /**
     * A synchronized method that removes the first card from deck and returns the discarded card. 
     * @return The discarded card
     */
    public synchronized Card removeCard(){
        Card card = deckContents.remove(0);
        // Notify any waiting threads that the deck may have space now.
        notifyAll();
        return card;
    }

    public int getSize(){
        return deckContents.size();
    }


    public int getDeckNumber(){
        return deckNumber;
    }

    public String getDeckName(){
        String str= "Deck"+Integer.toString(deckNumber);
        return str;
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
        try{
           // Create a FileWriter to write to a file with the specified name.
            FileWriter writer = new FileWriter(fileName);

            // Wrap the FileWriter in a BufferedWriter for efficient writing.
            BufferedWriter buffwriter = new BufferedWriter(writer);

            // Use the BufferedWriter to write an empty string to the file to clear the file.
            buffwriter.write("");
            buffwriter.close();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public String getContentsStr(){
        String str="";
        for(Card card : deckContents){
            str=str+Integer.toString(card.getValue())+" ";
        }

        return str;
    }

    /**
     * Appends a given string to the file with the name specified by the `fileName` field.
     * @param line The string to be written to the file.
     */
    public void writeToFile(String line) {
        try {
            // Create a FileWriter to append to the file
            FileWriter writer = new FileWriter(fileName, true);
            // Wrap the FileWriter in a BufferedWriter
            BufferedWriter buffwriter = new BufferedWriter(writer);
            // Write the string to the file
            buffwriter.write(line);
            buffwriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Log deck contents at the end of the game.
     */
    public void gameEndOutput(){
        writeToFile("deck"+deckNumber+" contents: "+getContentsStr());
    }

    
}

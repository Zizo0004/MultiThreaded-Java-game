import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.stream.Stream;
import java.util.*;

public class CardGame extends Thread{
    public static void main(String[] args) throws Exception {
        // Initalize Scanner object
        Scanner scanner = new Scanner(System.in);
        int numOfPlayers;
        while (true) {
            try{
                //Ask the user to enter the number of players
                System.out.print("Please enter the number of players: ");
                String input = scanner.nextLine();
                numOfPlayers = Integer.parseInt(input);

                //Check that the number of players entered is valid.
                if (numOfPlayers >= 2 && numOfPlayers <=30) {
                    break;
                } else {
                    System.out.println("The input is too small or too large\n");
                }
            }
            //Catch exception if the user enters a non-integer value
            catch(NumberFormatException e) {
                System.out.println("This input is a not valid integer\n");
                continue;
            }
        }
        
        //Initualise an array list to store all the cards
        ArrayList<Card> pack = new ArrayList<Card>();
        while(true){
            //Asks user to input file name of pack
            System.out.println("Please enter the file name of the pack");
            String filePath = scanner.nextLine();
            try (Stream<String> stream = Files.lines(Paths.get(filePath))) {
                // Iterate over every line in the file
                stream.forEach(line ->{
                    // Make a new card object for every line in the pack
                    Card card = new Card(Integer.parseInt(line));
                    pack.add(card);
                });
                // Check if the pack is valid using InputPackValidator class
                InputPackValidator validator = new InputPackValidator(numOfPlayers);
                if (validator.isValidInputPack(filePath)){
                    break;
                }
                System.out.println("Error: Invalid pack!");
                continue;
            } catch (NoSuchFileException e) {
                System.out.println("Error: File not found.");
                continue;
            }catch(NumberFormatException e){
                System.out.println("Error: Non-integer value found in file.");
                continue;
            }
        }
        scanner.close();

        Collections.shuffle(pack);  //Randomise the order of the pack
        ArrayList<Player>playerList= new ArrayList<Player>();   //A list to store all player objects
        ArrayList<Deck>deckList=new ArrayList<Deck>();  // Store all deck objects

        for(int i=0; i<numOfPlayers;i++ ){ 
            Deck deck = new Deck(i+1);  // Make a new deck object
            deckList.add(deck);      //Adds new deck objects to list of decks
            deck.initualiseFile();  // Make new output file or clear the contents of one that already exists
        }

        for (int i=0; i<numOfPlayers;i++ ){    //Creates player objects from 1 to the total number of players 
            Deck left = deckList.get(i);    //Gets left and right deck of player
            Deck right = deckList.get(((i+1)% numOfPlayers));
            Player player= new Player(i+1,left,right);
            playerList.add(player);     //Adds player object to list of players
            player.initualiseFile();    // Make new output file or clear the contents of one that already exists

        }

        int counter=0;
        for(int x = 1; x<=4;x++){    //Loops 4 times so each player gets 4 cards each
            for (Player player : playerList){
                player.setPlayers(playerList);
                Card card=pack.get(counter);
                player.addCard(card);
                counter++;
            }
            for(Deck deck : deckList){  //Loops 4 times so each deck recives 4 cards each
                Card card=pack.get(counter);
                deck.addCard(card);
                counter++;
            }
        }
        
        
        for(Player player : playerList){
            player.initualHand();   // Log the player's inital hand
            // Check if the inital hand has a winner
            if (player.isWinningHand()){   
                player.result();
                break;
            }
            // Start player threads
            player.start();
        
        }
        for (Player player : playerList) {
            try {
                // Main thread waits for all players to finish their gameplay before proceeding.
                player.join();

            } catch (InterruptedException e) {
                break;
            }

        }

        // Log every deck's contents at the end of the game
        for(Deck deck:deckList){
            deck.gameEndOutput();
        }
        
    
    }
}
/**
 * Stores the value of an individual card 
 * 
 */

public class Card {
    // The cards value is a constant so its thread safe
    private final int value;

    /** 
     * Constructor for Card class
     * @param value The value of the card
    */
    public Card(int value){
        this.value=value;
    }

    /**
     * Returns the value of the card
    */
    public int getValue() {
        return value;
    }
    
}

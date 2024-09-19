package classes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import classes.enums.Action;
import classes.enums.Color;
import classes.enums.Type;
import classes.enums.Wild;

public class Deck {
    private List<Card> cards = new ArrayList<>();

    public Deck() {
        for(Color color : Color.values()) {
            for(int i = 0; i < 10; i++) {
                cards.add(new NumberCard(Type.NUMBER, color, i));

                if(i != 0) {
                    cards.add(new NumberCard(Type.NUMBER, color, i));
                }
            }
        }

        for(Action action : Action.values()) {
            for(Color color : Color.values()) {
                for(int i = 0; i < 2; i++) {
                    cards.add(new ActionCard(Type.ACTION, action, color));
                }
            }
        }

        for(Wild wild : Wild.values()) {
            for(int i = 0; i < 4; i++) {
                cards.add(new WildCard(Type.WILD, wild));
            }
        }

        Collections.shuffle(cards);

        // for (Card card : cards) {
        //     System.out.println(card.toString());
        // }
    }

    @Override
    public String toString() {
        return "Deck [cards=" + cards + "]";
    }

    public List<Card> getCards() {
        return cards;
    }
    
    public Card drawCard() {
        return cards.remove(0);
    }
    
    public List<Card> drawInitialCards() {
        List<Card> initialHand = new ArrayList<>();

        for(int i = 0; i < 7; i++) {
            initialHand.add(drawCard());
        }

        return initialHand;
    }

    // public static void main(String[] args) {
    //     new Deck();
    // }
}

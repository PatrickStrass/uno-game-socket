package classes;

import classes.enums.Color;
import classes.enums.Type;

public class NumberCard extends Card {
    private Color color;
    private int number;

    public NumberCard(Type type, Color color, int number) {
        super(type);
        this.color = color;
        this.number = number;
    }

    public boolean matches(NumberCard card) {
        return this.color.equals(card.getColor()) || this.number == card.getNumber();
    }

    @Override
    public String toString() {
        return color.getColorCode() + color.name() + " " + number + color.resetCode();
    }

    @Override
    public boolean matches(Card card) {
        if(card instanceof NumberCard) {
            NumberCard numberCard = (NumberCard) card;
            return this.color.equals(numberCard.getColor()) || (this.number == numberCard.getNumber());
        }
        return false;     
    }

    public Color getColor() {
        return color;
    }

    public int getNumber() {
        return number;
    }
}

package classes;

import classes.enums.Type;

public abstract class Card {
    private Type type;

    public Card(Type type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Card [type=" + type + "]";
    }

    public Type getType() {
        return type;
    }

    public abstract String coloredString();

    public abstract boolean matches(Card card);
}

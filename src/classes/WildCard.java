package classes;

import classes.enums.Type;
import classes.enums.Wild;

public class WildCard extends Card {
    private Wild wild;

    public WildCard(Type type, Wild wild) {
        super(type);
        this.wild = wild;
    }

    @Override
    public String toString() {
        return wild.name();
    }

    @Override
    public String coloredString() {
        return this.toString();
    }

    @Override
    public boolean matches(Card card) {
        return true;
    }

    public Wild getWild() {
        return wild;
    }
}

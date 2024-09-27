package classes;

import classes.enums.Action;
import classes.enums.Color;
import classes.enums.Type;

public class ActionCard extends Card {
    private Action action;
    private Color color;

    public ActionCard(Type type, Action action, Color color) {
        super(type);
        this.action = action;
        this.color = color;
    }

    @Override
    public String toString() {
        return color.name() + " " + action.name();
    }

    @Override
    public String coloredString() {
        return color.getColorCode() + color.name() + " " + action.name() + color.resetCode();
    }

    @Override
    public boolean matches(Card card) {
        if(card instanceof NumberCard) {
            NumberCard numberCard = (NumberCard) card;
            return this.color.equals(numberCard.getColor());
        } else if(card instanceof WildCard) {
            return true;
        } else if(card instanceof ActionCard) {
            ActionCard actionCard = (ActionCard) card;
            return this.color.equals(actionCard.getColor());
        }
        return false; 
    }

    public Action getAction() {
        return action;
    }

    public Color getColor() {
        return color;
    }
}

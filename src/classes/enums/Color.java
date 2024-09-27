package classes.enums;

public enum Color {
    RED,
    BLUE,
    GREEN,
    YELLOW;
    
    public String getColorCode() {
        switch(this) {
            case RED:
                return "\u001B[31m";
            case BLUE:
                return "\u001B[34m";
            case GREEN: 
                return "\u001B[32m";
            case YELLOW:
                return "\u001B[33m";
            default:
                return "\u001B[0m";
        }
    }

    public String resetCode() {
        return "\u001B[0m";
    }
}

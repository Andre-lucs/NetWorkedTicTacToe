package org.andrelucs.networkedtictactoe.logic;

public enum Player {
    CROSS("CROSS"), CIRCLE("CIRCLE");

    private final String value;

    Player(String value) {
        this.value = value;
    }

    public static Player fromString(String value){
        if(value.equals("X") || value.equals("CROSS")){
            return CROSS;
        }else if(value.equals("O") || value.equals("CIRCLE")){
            return CIRCLE;
        }
        return null;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }

    public boolean compare(Player obj) {
        return obj.value.equals(value);
    }
}

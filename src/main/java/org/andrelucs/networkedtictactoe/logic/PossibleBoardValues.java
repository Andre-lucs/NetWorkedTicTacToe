package org.andrelucs.networkedtictactoe.logic;

public enum PossibleBoardValues {
    EMPTY(null), CROSS(Player.CROSS), CIRCLE(Player.CIRCLE);

    private final Player value;

    PossibleBoardValues(Player i) {
        this.value = i;
    }

    public static PossibleBoardValues fromPlayer(Player player){
        return player == Player.CROSS ? PossibleBoardValues.CROSS : PossibleBoardValues.CIRCLE;
    }

    public Player getValue() {
        return value;
    }

    @Override
    public String toString() {
        if (value == null) {
            return "";
        }
        return value.toString();
    }
}

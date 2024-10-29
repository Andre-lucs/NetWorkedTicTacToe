package org.andrelucs.networkedtictactoe.network.utils;

import org.andrelucs.networkedtictactoe.logic.Player;

public class MessageGenerator {
    
    public static String MoveMessage(String role, int row, int column) {
        return String.format("MOVE %s %d %d", role, row, column);
    }
    
    public static String RoleMessage(String role) {
        return String.format("ROLE %s", role);
    }
    
    public static String StartMessage() {
        return "START";
    }

    public static String WarnMessage(String message) {
        return String.format("WARN %s", message);
    }

    public static String EndMessage(String winner, int[] matches) {
        return String.format("END %s %d:%d %d:%d %d:%d", winner, matches[0], matches[1], matches[2], matches[3], matches[4], matches[5]);
    }

    public static String RematchMessage() {
        return "REMATCH";
    }

    public static String RematchAcceptedMessage(String player) {
        return String.format("REMATCH %s", player);
    }

    public static String ExitMessage(String value) {
        return String.format("EXIT %s", value);
    }

    public static String ExitMessage() {
        return "EXIT";
    }
}

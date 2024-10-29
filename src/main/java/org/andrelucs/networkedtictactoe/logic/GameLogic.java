package org.andrelucs.networkedtictactoe.logic;

import org.andrelucs.networkedtictactoe.network.Server;


public class GameLogic {
    private final PossibleBoardValues[][] board = new PossibleBoardValues[3][3];
    private Player playerTurn = Player.CROSS;
    private Server server;

    public GameLogic(Server server) {
        // Initialize the board with PossibleBoardValues.EMPTY
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                board[i][j] = PossibleBoardValues.EMPTY;
            }
        }

        this.server = server;
    }

    public void move(int x, int y, Player playerMoving){
        if (!playerMoving.equals(playerTurn)) {
            server.sendWarning("Not your turn", playerMoving);
            return;
        }
        if (board[x][y] != PossibleBoardValues.EMPTY) {
            server.sendWarning("Cell already occupied", playerMoving);
            return;
        }
        board[x][y] = PossibleBoardValues.fromPlayer(playerMoving);
        // Just sends the movement if the player moving is the current player
        playerTurn = playerTurn.compare(Player.CROSS) ? Player.CIRCLE : Player.CROSS;

        checkWin();
        server.moved(playerMoving, x, y);

    }

    private void checkWin() {
        for (int i = 0; i < 3; i++) {
            if (board[i][0] == board[i][1] && board[i][1] == board[i][2] && board[i][0] != PossibleBoardValues.EMPTY) {
                notifyEndGame(new int[]{i, 0, i, 1, i, 2}, board[i][0].getValue());
                return;
            }
            if (board[0][i] == board[1][i] && board[1][i] == board[2][i] && board[0][i] != PossibleBoardValues.EMPTY) {
                notifyEndGame(new int[]{0, i, 1, i, 2, i}, board[0][i].getValue());
                return;
            }
        }
        if (board[0][0] == board[1][1] && board[1][1] == board[2][2] && board[0][0] != PossibleBoardValues.EMPTY) {
            notifyEndGame(new int[]{0, 0, 1, 1, 2, 2}, board[0][0].getValue());
            return;
        }
        if (board[0][2] == board[1][1] && board[1][1] == board[2][0] && board[0][2] != PossibleBoardValues.EMPTY) {
            notifyEndGame(new int[]{0, 2, 1, 1, 2, 0}, board[0][2].getValue());
            return;
        }
    }

    private void checkDraw(){
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if(board[i][j] == PossibleBoardValues.EMPTY){
                    return;
                }
            }
        }
        server.endGameDraw();
    }

    private void notifyEndGame(int[] matches, Player winner){
        server.endGame(winner, matches);
    }

    public void reset() {
        // Initialize the board with PossibleBoardValues.EMPTY
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                board[i][j] = PossibleBoardValues.EMPTY;
            }
        }
        playerTurn = Player.CROSS;
    }
}

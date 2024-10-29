package org.andrelucs.networkedtictactoe.network;

import org.andrelucs.networkedtictactoe.logic.GameLogic;
import org.andrelucs.networkedtictactoe.logic.Player;
import org.andrelucs.networkedtictactoe.network.utils.MessageGenerator;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Server class that will handle the connections and the game logic
 * These are the messages that will be sent and received by the server:<br/>
 * SERVER -> CLIENT<br/>
 * ROLE {role} -> Sends to the client if it will be the cross or the circle <br/>
 * START -> Starts the match<br/>
 * MOVE {role} {row} {column} -> Sends the moves made <br/>
 * END {winner} {x:y} {x:y} {x:y} -> Finishes a match<br/>
 * REMATCH -> Asks if the players want a rematch <br/>
 * WARN {message} -> Sends warnings to the clients<br/><br/>
 * EXIT -> When a player logout or the server is stopped <br/>
 * CLIENT -> SERVER<br/>
 * MOVE {role} {row} {column} -> Movement request<br/>
 * REMATCH {role} -> Accepts a rematch <br/>
 * EXIT -> Client exits <br/>
 */
public class Server implements Runnable {

    private final ServerSocket serverSocket;
    private final List<ClientHandler> clients = new ArrayList<>();
    private boolean[] rematchAccepted = new boolean[2];
    private boolean running = true;
    private final GameLogic gameLogic;

    public Server(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        gameLogic = new GameLogic(this);
        InetAddress ip = InetAddress.getLocalHost();
        System.out.println("Server started on IP: " + ip.getHostAddress() + " and port: " + port);
    }

    public void handleMessage(String message){
        String[] parts = message.split(" ");
        System.out.println("Server Received a message: "+message);
        switch (parts[0]) {
            case "MOVE" -> {
                var playerMoving = Player.fromString(parts[1]);
                int x = Integer.parseInt(parts[2]);
                int y = Integer.parseInt(parts[3]);
                assert playerMoving != null;
                gameLogic.move(x, y, playerMoving);
            }
            case "EXIT" -> {
                var playerMoving = Player.fromString(parts[1]);

                removeClient(playerMoving);
                stop();

            }
            case "REMATCH" -> {
                var playerMoving = Player.fromString(parts[1]);

                rematchAccepted[playerMoving == Player.CROSS ? 0 : 1] = true;
                for (int i = 0; i < rematchAccepted.length; i++) {
                    System.out.println("Player " + i + " accepted: " + rematchAccepted[i]);
                }

                if (rematchAccepted[0] && rematchAccepted[1]) {
                    gameLogic.reset();
                    System.out.println("Rematch accepted");
                    broadcast(MessageGenerator.StartMessage());
                    rematchAccepted = new boolean[2];
                }
            }
        }
    }

    @Override
    public void run() {
        System.out.println("Listening on port");
        while (running) {
            try {
                if(clients.size() == 2){
                    Thread.sleep(500);
                    System.out.println("Game started");
                    broadcast(MessageGenerator.StartMessage());
                    break;
                }else{
                    Socket socket = serverSocket.accept();
                    ClientHandler client = new ClientHandler(socket, this);

                    Thread thread = new Thread(client);
                    thread.setDaemon(true);
                    thread.start();

                    int currentClientsSize = clients.size();
                    // First to connect will be the cross player and the next one will be the circle
                    new Thread(() ->{
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        client.setRole(currentClientsSize == 0 ? Player.CROSS : Player.CIRCLE);
                    }).start();
                    clients.add(client);
                }
            } catch (IOException e) {
                System.out.println("Server stopped");
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void broadcast(String message) {
        for (ClientHandler client : clients) {
            client.sendMessage(message);
        }
    }



    public void moved(Player playerMoving, int x, int y) {
        broadcast(MessageGenerator.MoveMessage(playerMoving.getValue(), x, y));
    }

    public void sendWarning(String message, Player playerMoving) {
        getClientWithRole(playerMoving).sendMessage(MessageGenerator.WarnMessage(message));
    }

    public void stop() {
        System.out.println("Stopping server");
        clients.forEach(i -> i.sendMessage(MessageGenerator.ExitMessage()));
        running = false;
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ClientHandler getClientWithRole(Player role){
        for (ClientHandler client : clients) {
            if (client.role.equals(role)) {
                return client;
            }
        }
        return null;
    }

    public void removeClient(Player role) {
        removeClient(getClientWithRole(role));
    }

    public void removeClient(ClientHandler clientHandler) {
        clients.forEach(i ->{
            if(i.equals(clientHandler)){
                try {
                    i.closeConnection();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
    public void endGame(Player winner, int[] matches) {
        broadcast(MessageGenerator.EndMessage(winner.getValue(), matches));
        //asks for a rematch for both players
        broadcast(MessageGenerator.RematchMessage());
    }

    public void endGameDraw() {
        broadcast(MessageGenerator.EndMessage("DRAW", new int[]{0, 0, 0, 0, 0, 0}));
        broadcast(MessageGenerator.RematchMessage());
    }
}

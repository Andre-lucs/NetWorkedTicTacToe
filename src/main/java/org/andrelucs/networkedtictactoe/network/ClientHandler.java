package org.andrelucs.networkedtictactoe.network;

import org.andrelucs.networkedtictactoe.logic.Player;
import org.andrelucs.networkedtictactoe.network.utils.MessageGenerator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable{

    private Socket socket;
    private Server server;
    private PrintWriter out;
    private BufferedReader in;
    protected Player role;


    public ClientHandler(Socket socket, Server server) throws IOException {
        this.socket = socket;
        this.server = server;
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    @Override
    public void run() {
        String clientMessage;
        try{
            while ((clientMessage = in.readLine()) != null) {
                server.handleMessage(clientMessage);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                closeConnection();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public void sendMessage(String message) {
        out.println(message);
    }

    public void closeConnection() throws IOException {
        in.close();
        out.close();
        socket.close();

    }

    public void setRole(Player role) {
        this.role = role;
        sendMessage(MessageGenerator.RoleMessage(this.role.getValue()));
    }
}

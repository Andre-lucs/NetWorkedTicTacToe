package org.andrelucs.networkedtictactoe.network;

import java.io.*;
import java.net.Socket;
import java.util.function.Function;

public class Client implements Runnable {
    private final Socket socket;
    private final PrintWriter out;
    private final BufferedReader in;
    private Function<String, Void> handleMessage;

    public Client(String serverAddress, int port) throws IOException {
        socket = new Socket(serverAddress, port);
        socket.setSoTimeout(5000);
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    public void sendMessage(String message) {
        out.println(message);
    }

    public String receiveMessage() {
        try {
            return in.readLine();
        } catch (IOException e) {
            return "";
        }
    }

    public void closeConnection() {
        System.out.println("Closing connection");
        try{
            in.close();
            out.close();
            socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void setHandleMessage(Function<String, Void> handleMessage) {
        this.handleMessage = handleMessage;
    }

    @Override
    public void run() {
        while (!socket.isClosed()) {
            System.out.println("Waiting for message");
            String message = receiveMessage();
            if (handleMessage != null) {
                handleMessage.apply(message);
            }
        }
    }
}

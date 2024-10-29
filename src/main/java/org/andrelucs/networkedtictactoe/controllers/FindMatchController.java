package org.andrelucs.networkedtictactoe.controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.andrelucs.networkedtictactoe.network.Client;
import org.andrelucs.networkedtictactoe.network.Server;
import org.andrelucs.networkedtictactoe.network.utils.MessageGenerator;

import java.io.IOException;
import java.net.InetAddress;

public class FindMatchController {

    @FXML
    private Label statusLabel;
    @FXML
    private TextField serverAddressField;
    @FXML
    private TextField serverPortField;

    private Server server;
    private Client client;
    private MainController mainController;

    public void handleCreateServer(ActionEvent actionEvent) {

        try{
            int port = Integer.parseInt(serverPortField.getText());
            this.server = new Server(port);
            new Thread(server).start();

            InetAddress ip = InetAddress.getLocalHost();
            statusLabel.setText("Server started on IP: " + ip.getHostAddress() + " and port: " + port);
        } catch (IOException e) {
            statusLabel.setText("Status: Failed to start server");
            e.printStackTrace();
        }catch (NumberFormatException e){
            statusLabel.setText("Status: Invalid port number");
        }

    }

    public void handleConnectToServer(ActionEvent actionEvent){
        try {
            String address = serverAddressField.getText();
            int port = Integer.parseInt(serverPortField.getText());

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/andrelucs/networkedtictactoe/main-scene.fxml"));
            Parent root = loader.load();

            mainController = loader.getController();
            this.client = new Client(address, port);
            mainController.setClient(this.client);
            if(address.equals("localhost")) address = InetAddress.getLocalHost().getHostAddress();
            mainController.setInfo("Connected to server at " + address + ":" + port);

            Stage stage = (Stage) statusLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        }catch (IOException e){
            statusLabel.setText("Status: Failed to connect to server");
            e.printStackTrace();
        }catch (NumberFormatException e){
            statusLabel.setText("Status: Invalid port number");
        }

    }

    public void stopServer() {
        if(server != null) server.stop();
        if(mainController != null) mainController.exit();
    }
}

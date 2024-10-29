package org.andrelucs.networkedtictactoe;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.andrelucs.networkedtictactoe.controllers.FindMatchController;

import java.io.IOException;

public class GameWindow extends Application {

    private Scene findMatchScene;
    private FindMatchController findMatchController;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/andrelucs/networkedtictactoe/find-match-scene.fxml"));
        findMatchScene = new Scene(loader.load());
        findMatchController = loader.getController();
        stage.setScene(findMatchScene);
        stage.setTitle("Server/Client Setup");
        stage.setResizable(false);
        stage.show();

    }

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void stop() throws Exception {
        findMatchController.stopServer();
        Platform.exit();
        System.exit(0);
        super.stop();
    }

}
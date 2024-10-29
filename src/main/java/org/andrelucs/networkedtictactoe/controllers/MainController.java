package org.andrelucs.networkedtictactoe.controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import org.andrelucs.networkedtictactoe.logic.GameState;
import org.andrelucs.networkedtictactoe.logic.Player;
import org.andrelucs.networkedtictactoe.logic.PossibleBoardValues;
import org.andrelucs.networkedtictactoe.network.Client;
import org.andrelucs.networkedtictactoe.network.utils.MessageGenerator;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class MainController {

    private Client client;
    private Player player;
    private final Button[][] buttonGrid = new Button[3][3];
    private final Map<Player, Integer> score;


    private GameState state;

    private Image crossImage;
    private Image circleImage;

    // UI elements
    private final Alert alert = new Alert(Alert.AlertType.WARNING);
    @FXML
    private AnchorPane main;
    @FXML
    private Label turnStatusLabel;
    @FXML
    private Label matchInfoLabel;
    @FXML
    private Label playerFormLabel;
    @FXML
    private GridPane board;
    @FXML
    private Pane rematchPanel;
    @FXML
    private Label crossScoreLabel;
    @FXML
    private Label circleScoreLabel;

    private Line resultLine;



    public MainController() {
        try{
            alert.getDialogPane().getStylesheets().add(getClass().getResource("/org/andrelucs/networkedtictactoe/stylesheets/main.css").toExternalForm());
            this.crossImage = new Image(getClass().getResourceAsStream("/org/andrelucs/networkedtictactoe/images/cross.svg.png"));
            this.circleImage = new Image(getClass().getResourceAsStream("/org/andrelucs/networkedtictactoe/images/circle.svg.png"));
        }catch (Exception e){
            System.err.println("Error loading resources");
        }
        score = new HashMap<>();
        score.put(Player.CROSS, 0);
        score.put(Player.CIRCLE, 0);
    }

    @FXML
    public void handleButtonClick(ActionEvent actionEvent) {
        if (state == GameState.WAITING) {
            showAlert("Game hasn't started yet");
            return;
        }

        Button button = (Button) actionEvent.getSource();
        List<Integer> coords = Stream.of(button.getId().substring(6).split("")).map(Integer::parseInt).toList();
        try {
            client.sendMessage(MessageGenerator.MoveMessage(player.getValue(), coords.get(0), coords.get(1)));
        } catch (IllegalArgumentException e) {
            showAlert(e.getMessage());
        }

    }

    public void showAlert(String message) {
        alert.setTitle("");
        alert.setHeaderText(message);
        alert.setContentText("");
        Platform.runLater(alert::show);

    }

    public void makeMove(Button button, PossibleBoardValues value) {
        ImageView imageView = new ImageView();
        if (value == PossibleBoardValues.CROSS) {
            imageView.setImage(crossImage);
        } else if (value == PossibleBoardValues.CIRCLE) {
            imageView.setImage(circleImage);
        }
        imageView.setFitWidth(150);
        imageView.setFitHeight(150);
        Platform.runLater(()-> button.setGraphic(imageView));
    }

    public Void handleMessages(String message){
        if(message == null) return null;
        String[] parts = message.split(" ");
        System.out.println("Client Received a message: "+message);
        switch (parts[0]) {
            case "MOVE" -> {
                Player playerMoving = Player.valueOf(parts[1]);
                int x = Integer.parseInt(parts[2]);
                int y = Integer.parseInt(parts[3]);
                makeMove(buttonGrid[x][y], PossibleBoardValues.fromPlayer(playerMoving));
                // When the player is the one moving, the turn passes to the other player
                updateTurnLabel(playerMoving != player ? "Your turn" : "Opponent's turn");
            }
            case "ROLE" -> {
                player = Player.valueOf(parts[1]);
                updatePlayerFormLabel(player);
                if(player == Player.CROSS){
                    updateTurnLabel("Your turn");
                }
                updateCrossAndCircleImageColors();
            }
            case "START" -> {
                startGame();
                if(player == null)return null;
                if(player.equals(Player.CIRCLE)){
                    updateTurnLabel("Opponent's turn");
                }else if(player.equals(Player.CROSS)){
                    updateTurnLabel("Your turn");
                }
            }
            case "END" -> {
                state = GameState.FINISHED;
                Player winner = Player.valueOf(parts[1]);
                if(winner == null){
                    showEndGameMessage("Draw", new String[0]);
                    return null;
                };
                String[] matches = new String[3];
                System.arraycopy(parts, 2, matches, 0, 3);
                updateScoreLabels(winner);
                showEndGameMessage("Player " + winner + " won", matches);

            }
            case "WARN" -> showAlert(String.join(" ", Arrays.copyOfRange(parts, 1, parts.length)));
            case "REMATCH" -> {
                state = GameState.WAITING;
                Platform.runLater(()->{
                    rematchPanel.setVisible(true);
                    rematchPanel.getParent().setMouseTransparent(false);
                    rematchPanel.getParent().toFront();
                });
            }
            case "EXIT" -> {
                Platform.runLater(()-> {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setHeaderText("Opponent left the game");
                    alert.setOnCloseRequest(e->{
                        Platform.exit();
                        client.closeConnection();
                    });
                    alert.show();
                });
            }
        }
        return null;
    }

    private void updateScoreLabels(Player winner) {
        score.put(winner, score.get(winner)+1);
        Platform.runLater(()->{
            crossScoreLabel.setText(score.get(Player.CROSS).toString());
            circleScoreLabel.setText(score.get(Player.CIRCLE).toString());
        });
    }

    private void startGame() {
        if(resultLine != null) {
            Platform.runLater(() -> {
                rematchPanel.setVisible(false);
                rematchPanel.getParent().setMouseTransparent(true);
                resultLine.setVisible(false);
                resultLine.setDisable(true);
                resultLine.setMouseTransparent(true);
            });
        }
        var buttons = board.getChildren();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                Button button = (Button) buttons.get(j * 3 + i);
                buttonGrid[i][j] = button;
                Platform.runLater(()-> button.setGraphic(null));
            }
        }
        state = GameState.PLAYING;
    }


    public void showEndGameMessage(String message, String[] matches) {
        // Draw a line on the specified buttons
        Platform.runLater(() -> {
            var alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText(message);
            alert.show();
            Line line = new Line();

            var first = Arrays.stream(matches).findFirst().get();
            var last = Arrays.stream(matches).reduce((i, second) -> second).get();

            var startButton =buttonGrid[Integer.parseInt(first.split(":")[0])][Integer.parseInt(first.split(":")[1])];
            var endButton = buttonGrid[Integer.parseInt(last.split(":")[0])][Integer.parseInt(last.split(":")[1])];
            var startBounds = startButton.localToScene(startButton.getLayoutBounds());
            var endBounds = endButton.localToScene(endButton.getLayoutBounds());
            line.setStartX(startBounds.getCenterX());
            line.setStartY(startBounds.getCenterY());
            line.setEndX(endBounds.getCenterX());
            line.setEndY(endBounds.getCenterY());
            line.setStrokeWidth(5);
            line.setStroke(Color.FIREBRICK);
            // Add line
            main.getChildren().add(line);
            this.resultLine = line;
        });

    }

    public void setClient(Client client) {
        this.client = client;
        this.client.setHandleMessage(this::handleMessages);
        var t = new Thread(client);
        t.setDaemon(true);
        t.start();
    }

    public void updateTurnLabel(String message) {
        Platform.runLater(() -> turnStatusLabel.setText(message));
    }

    public void updatePlayerFormLabel(Player player) {
        Platform.runLater(() -> playerFormLabel.setText("You are "+player));
    }

    public void setInfo(String s) {
        Platform.runLater(()->matchInfoLabel.setText(s));
    }

    public void updateCrossAndCircleImageColors(){
        // Already knowing the images are 300X300
        var width = 300;
        var height = 300;

        WritableImage crossImage = new WritableImage(width, height);
        WritableImage circleImage = new WritableImage(width, height);
        var xWriter = crossImage.getPixelWriter();
        var xReader = this.crossImage.getPixelReader();
        var cWriter = circleImage.getPixelWriter();
        var cReader = this.circleImage.getPixelReader();

        for(int i = 0; i < width; i++){
            for(int j = 0; j < height; j++){
                if (!xReader.getColor(i, j).equals(Color.TRANSPARENT)) {
                    if(player == Player.CROSS){
                        xWriter.setColor(i, j, Color.BLUE);
                    }else if(player == Player.CIRCLE) {
                        xWriter.setColor(i, j, Color.RED);
                    }
                }
                if (!cReader.getColor(i, j).equals(Color.TRANSPARENT)) {
                    if (player == Player.CROSS) {
                        cWriter.setColor(i, j, Color.RED);
                    } else if (player == Player.CIRCLE) {
                        cWriter.setColor(i, j, Color.BLUE);
                    }
                }
            }
        }

        this.crossImage = crossImage;
        this.circleImage = circleImage;

    }

    public void handleAcceptRematch(ActionEvent actionEvent) {
        client.sendMessage(MessageGenerator.RematchAcceptedMessage(player.getValue()));
        rematchPanel.setVisible(false);
    }

    public void handleRejectRematch(ActionEvent actionEvent) {
        rematchPanel.setVisible(false);
        Platform.exit();
        client.sendMessage(MessageGenerator.ExitMessage(player.getValue()));
        client.closeConnection();
    }

    public void exit() {
        client.sendMessage(MessageGenerator.ExitMessage(player.getValue()));
        client.closeConnection();

    }
}

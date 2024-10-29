module org.andrelucs.networkedtictactoe {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.andrelucs.networkedtictactoe to javafx.fxml;
    opens org.andrelucs.networkedtictactoe.controllers to javafx.fxml;
    exports org.andrelucs.networkedtictactoe;
    exports org.andrelucs.networkedtictactoe.controllers;
}
package app;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;

public class createRoomController {
    private static createRoomController instance = null;
    @FXML
    public CheckBox check;
    @FXML
    public TextField roomPass;
    @FXML
    public TextField roomName;

    public createRoomController() {
        instance = this;
        Platform.runLater(() -> {
            check.setOnAction((Event) -> {
                if (roomPass.isDisabled()) {
                    roomPass.setDisable(false);
                } else roomPass.setDisable(true);
            });
        });


    }

    public static createRoomController getInstance() { return instance; }

}
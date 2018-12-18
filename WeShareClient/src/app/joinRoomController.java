package app;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;

public class joinRoomController {
    private static joinRoomController instance = null;
    @FXML
    public TextField pubRoomName;
    @FXML
    public TextField RoomPass;

    @FXML
    public TextField priRoomName;

    @FXML
    public TableView table;




    public joinRoomController() {
        instance = this;
        Platform.runLater(() -> {

        });


    }

    public static joinRoomController getInstance() { return instance; }

}
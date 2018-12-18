package app;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.layout.VBox;

import java.util.Optional;

public class tabController {
    private static tabController instance = null;
    @FXML
    public VBox msgSpace;

    @FXML
    public ScrollPane scrollPane;

    @FXML
    public Tab tab;



    public tabController() {
        instance = this;
    }

    public static tabController getInstance() { return instance; }

}

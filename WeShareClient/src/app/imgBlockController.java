package app;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;
import javafx.scene.layout.HBox;

public class imgBlockController {
    private static imgBlockController instance = null;
    @FXML
    public HBox imgSpace;

    @FXML
    public HBox imgTextSpace;





    public imgBlockController() {
        instance = this;


    }

    public static imgBlockController getInstance() { return instance; }

}


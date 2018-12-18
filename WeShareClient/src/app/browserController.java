package app;

import javafx.fxml.FXML;
import javafx.scene.layout.VBox;

public class browserController {
    private static browserController instance = null;
    @FXML
    public VBox browserBox;

    public browserController() {
        instance = this;
    }


    public static browserController getInstance() { return instance; }
}

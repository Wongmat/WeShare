package app;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class audioBlockController {
    private static audioBlockController instance = null;
    @FXML
    public Slider slider;

    @FXML
    public Button play;

    @FXML
    public Button pause;
    @FXML
    public HBox audioTextSpace;

    @FXML
    public ImageView playImgSpace;

    @FXML
    public ImageView pauseImgSpace;




    public audioBlockController() {
        instance = this;
        Platform.runLater(() -> {
            Image play = null;
            Image pause = null;
            try {
                play = new Image(new FileInputStream("play.png"));
                pause = new Image(new FileInputStream("pause.jpg"));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            playImgSpace.setImage(play);
            pauseImgSpace.setImage(pause);
        });

    }

    public static audioBlockController getInstance() { return instance; }

}


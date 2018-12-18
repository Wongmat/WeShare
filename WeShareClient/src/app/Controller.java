package app;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Optional;

public class Controller {
    @FXML
    VBox vbox;

    @FXML
    TextField inputField;

    @FXML
    BorderPane border;

    @FXML
    AnchorPane anchor;

    @FXML
    TabPane tabPane;

    @FXML
    Button post;

    @FXML
    Button menu;

    @FXML
    Menu SendMenu;

    @FXML
    HBox logo;


    @FXML
    void initialize() {
        Platform.runLater(() -> {
            Scene scene = vbox.getScene();
            vbox.setPrefWidth(scene.getWidth() - 20);
            Text logoText = new Text("WeShare");
            logoText.setFont(Font.font("Futura",FontWeight.EXTRA_BOLD, 70));
            logo.getChildren().add(logoText);

        });
    }

    private HashMap <String, tabController> tabControllers = new HashMap<>();

    private static Controller instance;

    public Controller() {
        instance = this;
    }

    public static Controller getInstance() {
        return instance;
    }



    public static class nameDialog extends TextInputDialog {
        public nameDialog() {
            this.setTitle("WeShare");
            this.setHeaderText(null);
            this.setContentText("What's your name?: ");

        }

    }

    public void showMessages(String roomName, String msg) {
        Platform.runLater(() -> {
            Text text = new Text(msg);
            text.setFont(Font.font("Roboto", 14));
            tabController control = tabControllers.get(roomName);
            VBox msgSpace = control.msgSpace;
            text.setWrappingWidth(msgSpace.getWidth());
            msgSpace.getChildren().add(text);
            control.scrollPane.layout(); // update the height of the scroll area
            control.scrollPane.setVvalue(1); // scroll to the bottom*/
            // if(getActiveTab() != roomName) control.tab.setText(
        });
    }

    public void showUserName(String roomName, String username) {
        Platform.runLater(() -> {
            Text text = new Text(username);
            text.setFont(Font.font("Roboto",FontWeight.BOLD, 16));
            tabController control = tabControllers.get(roomName);
            VBox msgSpace = control.msgSpace;
            text.setWrappingWidth(msgSpace.getWidth());
            msgSpace.getChildren().add(text);
            control.scrollPane.layout(); // update the height of the scroll area
            control.scrollPane.setVvalue(1); // scroll to the bottom*/
        });
    }

    public void showImage(String roomName, String filename) {

        Platform.runLater(() -> {
            Image image = null;
            try {
                image = new Image(new FileInputStream(filename));
            } catch (FileNotFoundException e) {
            }

            ImageView smallImg = new ImageView();
            smallImg.setImage(image);
            Pane imgBlock  = null;
            try {
                imgBlock = FXMLLoader.load(getClass().getResource("imgBlock.fxml"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            imgBlockController imgControl = imgBlockController.getInstance(); //get imgBlock controller
            smallImg.fitHeightProperty().bind(imgControl.imgSpace.heightProperty());
            smallImg.fitWidthProperty().bind(imgControl.imgSpace.widthProperty());
            Hyperlink link = new Hyperlink(filename);
            link.setOnAction((Event)-> {

                File file = new File("./" + Main.getUsername() + "/Received/" + getActiveTab(), filename);
                Desktop desktop = Desktop.getDesktop();
                try {
                    desktop.open(file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            imgControl.imgTextSpace.getChildren().add(link);
            imgControl.imgSpace.getChildren().add(smallImg);
            tabController control = tabControllers.get(roomName); //get controller for tab
            VBox msgSpace = control.msgSpace;
            msgSpace.getChildren().add(imgBlock);
            control.scrollPane.layout(); // update the height of the scroll area
            control.scrollPane.setVvalue(1); // scroll to the bottom*/
        });
    }

    public void showAudio(String roomName, String filename) {

        Platform.runLater(() -> {
            Media media;
            tabController control = tabControllers.get(roomName);
            VBox msgSpace = control.msgSpace;
            media = new Media(Paths.get(filename).toUri().toString());
            MediaPlayer player = new MediaPlayer(media);
            Pane audioBlock = null;
            try {
                audioBlock = FXMLLoader.load(getClass().getResource("audioBlock.fxml"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            audioBlockController audioControl = audioBlockController.getInstance();
            Hyperlink link = new Hyperlink(filename);
            link.setOnAction((Event)-> {

                File file = new File("./" + Main.getUsername() + "/Received/" + getActiveTab());
                Desktop desktop = Desktop.getDesktop();
                try {
                    desktop.open(file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            audioControl.audioTextSpace.getChildren().add(link);

            player.setOnError(() -> {
                System.out.println("Error : " + player.getError().toString());
            });
            audioControl.play.setOnAction((event) -> {
                audioControl.play.setVisible(false);
                audioControl.pause.setVisible(true);
                // Button was clicked, do something...
                player.play();
            });
            audioControl.pause.setOnAction((event) -> {

                audioControl.pause.setVisible(false);
                audioControl.play.setVisible(true);
                // Button was clicked, do something...
                player.pause();
            });
            msgSpace.getChildren().add(audioBlock);
            control.scrollPane.layout(); // update the height of the scroll area
            control.scrollPane.setVvalue(1); // scroll to the bottom
        });
    }

    public void showFile(String roomName, String filename) {

        Platform.runLater(() -> {
            String path = Paths.get(filename).toUri().toString();
            Hyperlink link = new Hyperlink(filename);
            link.setOnAction((Event)-> {

                File file = new File("./" + Main.getUsername() + "/Received/" + getActiveTab(), filename);
                Desktop desktop = Desktop.getDesktop();
                try {
                    desktop.open(file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            tabController control = tabControllers.get(roomName);
            VBox msgSpace = control.msgSpace;
            msgSpace.getChildren().add(link);
            control.scrollPane.layout(); // update the height of the scroll area
            control.scrollPane.setVvalue(1); // scroll to the bottom
        });
    }

    @FXML
    void onPostButtonClicked() {
        try {
            System.out.print("Active tab: " + getActiveTab());
            Main.sendMsg(getActiveTab(), inputField.getText());
        } catch (IOException e) {
            e.printStackTrace();
        }
        Platform.runLater(() -> {
            inputField.clear();
        });
    }

    @FXML
    void onSharedFilesButtonClicked(){
        ViewBrowser();

    }

    @FXML
    void menuElements(boolean value) {
        Platform.runLater(() -> {
            anchor.setVisible(value);
            menu.setDisable(value);
            post.setDisable(value);
            SendMenu.setDisable(value);
            inputField.setDisable(value);
        });
    }

    @FXML
    void addTab(String roomName) {
        Platform.runLater(() -> {
            menuElements(false);


            Tab tab = null;
            try {
                tab = FXMLLoader.load(getClass().getResource("tab.fxml"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            tab.setText(roomName);
            tabController control = tabController.getInstance();
            control.tab.setOnClosed((Event )-> {
                try {
                    Main.LeaveRoom(roomName);
                    tabControllers.remove(roomName);
                    if (tabControllers.isEmpty()) {
                        menuElements(true);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,"Are you sure you want to exit " + '"' + roomName + '"' + "?", ButtonType.YES, ButtonType.CANCEL);
            control.tab.setOnCloseRequest((Event)->{
                Optional<ButtonType> response = confirm.showAndWait();
                if (response.get() == ButtonType.YES) {
                    this.tabPane.getTabs().remove(control.tab);
                } else Event.consume();
            });
            tabControllers.put(roomName, control);


            tabPane.getTabs().add(tab);
        });

    }

    @FXML
    void onImageButtonClicked() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose image...");
        fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("Image Files", "jpg", "png", "gif", "jpeg"));
        File selectedFile = fileChooser.showOpenDialog(new Stage());
        Main.sendFile(getActiveTab(),'%', selectedFile);

    }

    @FXML
    void onAudioButtonClicked() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose audio file");
        fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("Audio files", "mp3", "wav", "aac"));
        File selectedFile = fileChooser.showOpenDialog(new Stage());
        Main.sendFile(getActiveTab(), '#', selectedFile);

    }

    @FXML
    void onFileButtonClicked() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose file");
        File selectedFile = fileChooser.showOpenDialog(new Stage());
        Main.sendFile(getActiveTab(), '$', selectedFile);
    }

    @FXML
    public void ErrorPopup(String type, String message) {
        Platform.runLater(() -> {
            Alert dialog = new Alert(Alert.AlertType.ERROR, message,
                    ButtonType.OK);
            dialog.setHeaderText(type);
            dialog.setTitle("There was a problem...");
            dialog.show();
        });

    }

    @FXML
    public void ViewBrowser() {
        Platform.runLater(() -> {
            ScrollPane browser = null;
            try {
                browser = FXMLLoader.load(getClass().getResource("browser.fxml"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            browserController control = browserController.getInstance();
            HashMap<String, Double> files = Main.getReceivedDir();
            for (String key: files.keySet()) {
                String fileName;
                if(key.length() > 15) {
                    fileName = key.substring(0, 15) + "...";
                }
                else fileName = key;
                Hyperlink link = new Hyperlink(fileName + " " + files.get(key).toString() + "KB");
                link.setOnAction((Event)-> {
                    File file = new File("./" + Main.getUsername() + "/Received/" + getActiveTab(), key);
                    Desktop desktop = Desktop.getDesktop();
                    try {
                        desktop.open(file);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                control.browserBox.getChildren().add(link);
            }
            Alert dialog = new Alert(Alert.AlertType.NONE, null, ButtonType.OK);
            dialog.getDialogPane().setContent(browser);
            dialog.show();
        });

    }

    @FXML
    void createRoom() {
        try {
            Alert dialog = new Alert(Alert.AlertType.NONE, null,
                    ButtonType.OK, ButtonType.CANCEL);
            dialog.setTitle("Create Room");
            Parent content = FXMLLoader.load(getClass().getResource("createRoom.fxml"));
            dialog.getDialogPane().setContent(content);
            createRoomController control = createRoomController.getInstance();
            Optional<ButtonType> response = dialog.showAndWait();
            if (response.isPresent() && response.get() == ButtonType.OK) {
                String name = control.roomName.getText();
                String pass = (control.roomPass.getText().isEmpty()) ? null : control.roomPass.getText();
                char command = (pass == null) ? '+' : '*';
                Main.CreateOrJoin(name, pass, command);
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void joinRoom() {
        try {
            Alert dialog = new Alert(Alert.AlertType.NONE, null, ButtonType.CANCEL, ButtonType.OK);
            Parent content = FXMLLoader.load(getClass().getResource("joinRoom.fxml"));
            dialog.getDialogPane().setContent(content);
            Optional<ButtonType> response = dialog.showAndWait();
            if (response.isPresent() && response.get() == ButtonType.OK) {
                joinRoomController control = joinRoomController.getInstance();
                String roomName = (control.pubRoomName.getText().isEmpty()) ? control.priRoomName.getText() : control.pubRoomName.getText();
                String password = (control.RoomPass.getText().isEmpty()) ? null : control.RoomPass.getText();
                char command = (password == null) ? '^' : '&';
                Main.CreateOrJoin(roomName, password, command);
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    String getActiveTab(){
        return tabPane.getSelectionModel().getSelectedItem().getText();
    }

}







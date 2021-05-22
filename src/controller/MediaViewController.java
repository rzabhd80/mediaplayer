package controller;

import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class MediaViewController implements Initializable {
    private String filePath;
    private MediaPlayer mediaPlayer;
    public static Stage stage;
    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public MediaView getMediaView() {
        return mediaView;
    }

    public void setMediaView(MediaView mediaView) {
        this.mediaView = mediaView;
    }


    @FXML
    private BorderPane root;

    @FXML
    private Button openFile;

    @FXML
    private Button exit;

    @FXML
    private Button boostLeft;

    @FXML
    private Button stepLeft;

    @FXML
    private Button pause;

    @FXML
    private Button stepForward;

    @FXML
    private Button boostRight;
    @FXML
    private MediaView mediaView;
    @FXML
    private VBox downNav;
    @FXML
    private HBox hboxNav;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        openFile.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("select a file",
                    "*.mp3","*.mp4");
            fileChooser.getExtensionFilters().add(extensionFilter);
            File file = fileChooser.showOpenDialog(null);
            if(file !=null){
            filePath = file.toURI().toString();
            Media media = new Media(filePath);
            mediaPlayer = new MediaPlayer(media);
            mediaView.setMediaPlayer(mediaPlayer);
            mediaPlayer.play();
            }
        });
        exit.setOnAction(event -> {
            stage.close();
        });
    }
}

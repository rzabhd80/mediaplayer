package controller;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TableColumn;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.pathItem;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class MediaViewController implements Initializable {
    private String filePath;
    private MediaPlayer mediaPlayer;
    public static Stage stage;
    public static ArrayList<pathItem>paths = new ArrayList<>();
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
    private boolean paused = false;
    @FXML
    private BorderPane root;

    @FXML
    private Button openFile;

    @FXML
    private Button exit;

    @FXML
    private Button prevItem;

    @FXML
    private Button nextItem;

    @FXML
    private Button stepLeft;

    @FXML
    private Button pause;

    @FXML
    private Button stepForward;

    @FXML
    private MediaView mediaView;
    @FXML
    private VBox downNav;
    @FXML
    private HBox hboxNav;
    @FXML
    private StackPane stackPane;
    @FXML
    private Button playList;
    @FXML
    private Slider timeLine;
    @FXML
    private Slider volume;
    @FXML
    private ImageView loop;
    @FXML
    private Button loopButton;
    @FXML
    private Label durationPassed;
    @FXML
    private Label durationLeft;
    @FXML
    private Button stop;
    @FXML
    private Label volPer;
    private int currItem=-1;
    private boolean onLoop = false;
    public boolean isPaused() {
        return paused;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }
    private Media media;

    //controlling methods to set timeline slider,volume slider,etc...
    public void controlPlaySlier(MediaPlayer mediaPlayer){
        mediaPlayer.currentTimeProperty().addListener(new ChangeListener<javafx.util.Duration>() {
            @Override
            public void changed(ObservableValue<? extends javafx.util.Duration> observable, javafx.util.Duration oldValue, javafx.util.Duration newValue) {
                timeLine.setValue(newValue.toSeconds());
                durationPassed.setText(Double.toString((int)mediaPlayer.getCurrentTime().toSeconds()));
                durationLeft.setText(Double.toString((int)mediaPlayer.getTotalDuration().toSeconds()-(int)mediaPlayer.getCurrentTime().toSeconds()));
            }
        });
        timeLine.setOnMousePressed(event -> {
            mediaPlayer.seek(javafx.util.Duration.seconds(timeLine.getValue()));
        });
        timeLine.setOnMouseDragged(event -> {
            mediaPlayer.seek(javafx.util.Duration.seconds(timeLine.getValue()));
        });
        mediaPlayer.setOnReady(new Runnable() {
            @Override
            public void run() {
                double duration = mediaPlayer.getMedia().getDuration().toSeconds();
                timeLine.setMax(duration);
            }
        });
        volume.valueProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                mediaPlayer.setVolume(volume.getValue()/100);
                volPer.setText(Double.toString(volume.getValue()*100));
            }
        });
        volume.setValue(mediaPlayer.getVolume()*100);
        volume.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                mediaPlayer.setVolume(volume.getValue());
            }
        });
        volume.setOnMouseDragged(event -> {
            mediaPlayer.setVolume(volume.getValue());
        });
    }
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
             media = new Media(filePath);
            mediaPlayer = new MediaPlayer(media);
            mediaView.setMediaPlayer(mediaPlayer);
            mediaPlayer.play();
            DoubleProperty width = mediaView.fitWidthProperty();
            DoubleProperty height = mediaView.fitHeightProperty();
           width.bind(Bindings.selectDouble(mediaView.sceneProperty(),"width"));
           height.bind(Bindings.selectDouble(mediaView.sceneProperty(),"height"));
                mediaPlayer.currentTimeProperty().addListener(new ChangeListener<javafx.util.Duration>() {
                    @Override
                    public void changed(ObservableValue<? extends javafx.util.Duration> observable, javafx.util.Duration oldValue, javafx.util.Duration newValue) {
                        timeLine.setValue(newValue.toSeconds());
                    }
                });
                timeLine.setOnMousePressed(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        mediaPlayer.seek(javafx.util.Duration.seconds(timeLine.getValue()));
                    }
                });
                timeLine.setOnMouseDragged(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        mediaPlayer.seek(javafx.util.Duration.seconds(timeLine.getValue()));
                    }
                });
                mediaPlayer.setOnReady(new Runnable() {
                    @Override
                    public void run() {
                        double duration = media.getDuration().toSeconds();
                        timeLine.setMax(duration);
                    }
                });

                volume.setValue(mediaPlayer.getVolume()*100);
                volume.valueProperty().addListener(new InvalidationListener() {
                    @Override
                    public void invalidated(Observable observable) {
                        mediaPlayer.setVolume(volume.getValue()/100);
                    }
                });
                volume.setOnMousePressed(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        mediaPlayer.setVolume(volume.getValue());
                    }
                });
                loopButton.setOnAction(event1 -> {
                    if(onLoop){
                        onLoop=false;
                        loopButton.setText("loop:off");
                    } else {
                        onLoop = true;
                        loopButton.setText("loop:on");
                    }
                });
            }
            volPer.setText("100");
            mediaPlayer.currentTimeProperty().addListener(new ChangeListener<javafx.util.Duration>() {
                @Override
                public void changed(ObservableValue<? extends javafx.util.Duration> observable, javafx.util.Duration oldValue, javafx.util.Duration newValue) {
                    durationPassed.setText(Double.toString((int)mediaPlayer.getCurrentTime().toSeconds()));
                    durationLeft.setText(Double.toString((int)mediaPlayer.getTotalDuration().toSeconds()-(int)mediaPlayer.getCurrentTime().toSeconds()));
                }
            });
            volume.valueProperty().addListener(new InvalidationListener() {
                @Override
                public void invalidated(Observable observable) {
                    volPer.setText(Double.toString(volume.getValue())+"%");
                }
            });
            mediaPlayer.setOnEndOfMedia(new Runnable() {
                @Override
                public void run() {
                    if(onLoop)
                        mediaPlayer.seek(javafx.util.Duration.seconds(0));
                }
            });
            if(!onLoop){
                nextItem.setOnAction(event1 -> {
                    mediaPlayer.stop();
                    currItem++;
                    pathItem pathItem = paths.get(currItem);
                    mediaView.setMediaPlayer(null);
                    Media media = new Media(pathItem.getPath());
                    mediaPlayer = new MediaPlayer(media);
                    mediaView.setMediaPlayer(mediaPlayer);
                    controlPlaySlier(mediaPlayer);
                    mediaPlayer.play();
                });
                prevItem.setOnAction(event1 -> {
                    currItem--;
                    if(currItem>0) {
                        mediaPlayer.stop();
                        pathItem pathItem = paths.get(currItem);
                        mediaView.setMediaPlayer(null);
                        Media media = new Media(pathItem.getPath());
                        mediaPlayer = new MediaPlayer(media);
                        mediaView.setMediaPlayer(mediaPlayer);
                        controlPlaySlier(mediaPlayer);
                        mediaPlayer.play();
                    }
                });
            }
        });
        exit.setOnAction(event -> {
            stage.close();
        });
        pause.setOnAction(event -> {
            if(!paused){
                mediaPlayer.pause();
                this.paused = true;

            } else {
                mediaPlayer.play();
                this.paused =false;
            }
        });
        stepForward.setOnAction(event -> {
            Duration duration = Duration.ofSeconds(20);
            mediaPlayer.seek(mediaPlayer.getCurrentTime().add(javafx.util.Duration.seconds(20)));
        });
        stepLeft.setOnAction(event -> {
            mediaPlayer.seek(mediaPlayer.getCurrentTime().subtract(javafx.util.Duration.seconds(20)));
        });
        playList.setOnAction(event -> {
            FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getResource("../view/Playlist.fxml"));
            try {
                fxmlLoader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Stage stage = new Stage();
            stage.setScene(new Scene(fxmlLoader.getRoot()));
            stage.setResizable(false);
            stage.show();
        });
        stop.setOnAction(event -> {
            mediaPlayer.stop();
            mediaView.setMediaPlayer(null);
        });
    }
}

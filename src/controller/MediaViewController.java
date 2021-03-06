package controller;

import javafx.application.Platform;
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
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
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
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

public class MediaViewController implements Initializable {
    private String filePath;
    private MediaPlayer mediaPlayer;
    public static Stage stage;
    public static ArrayList<pathItem> paths = new ArrayList<>();

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

    private boolean playing = false;
    private boolean paused = false;
    public double vol;
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
    private int currItem = -1;
    private boolean onLoop = false;

    public boolean isPaused() {
        return paused;
    }

    private void playPlayList() {
        AtomicBoolean playing = new AtomicBoolean(false);
        if (currItem < 0 || currItem > paths.size()) {
            currItem = 0;
        }
        mediaPlayer.stop();
        mediaView.setMediaPlayer(null);
        Media media = new Media(paths.get(currItem).getPath());
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setVolume(vol);
        controlPlaySlier(mediaPlayer);
        mediaView.setMediaPlayer(mediaPlayer);
        mediaPlayer.play();
        currItem++;
        playing.set(true);
        mediaPlayer.setOnEndOfMedia(() -> {
            if (currItem < 0 || currItem > paths.size()) {
                currItem = 0;
            }
            if (onLoop) {
                mediaPlayer.seek(javafx.util.Duration.seconds(0));
            } else {
                mediaView.setMediaPlayer(null);
                Media media1 = new Media(paths.get(currItem).getPath());
                mediaPlayer = new MediaPlayer(media1);
                mediaPlayer.setVolume(vol);
                controlPlaySlier(mediaPlayer);
                mediaView.setMediaPlayer(mediaPlayer);
                mediaPlayer.play();
                currItem++;
            }
        });
        mediaPlayer.setOnEndOfMedia(() -> {
            if (onLoop) {
                mediaPlayer.seek(javafx.util.Duration.seconds(0));
            } else if (currItem < paths.size()) {
                playPlayList();
            }
        });
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    private Media media;

    public String passedTime(javafx.util.Duration duration) {
        int seconds = (int) duration.toSeconds();
        int sec = 0;
        int min = 0;
        int hour = 0;
        if (seconds > 60) {
            sec = seconds % 60;
            min = seconds / 60;
            if (min > 60) {
                hour = min / 60;
                min = min % 60;
                return hour + ":" + min + ":" + sec;
            } else {
                return hour + ":" + min + ":" + sec;
            }

        } else {
            return Integer.toString(seconds);
        }
    }

    //controlling methods to set timeline slider,volume slider,etc...
    public void controlPlaySlier(MediaPlayer mediaPlayer) {
        mediaPlayer.currentTimeProperty().addListener(new ChangeListener<javafx.util.Duration>() {
            @Override
            public void changed(ObservableValue<? extends javafx.util.Duration> observable, javafx.util.Duration oldValue,
                                javafx.util.Duration newValue) {
                timeLine.setValue(newValue.toSeconds());
                durationPassed.setText(passedTime(mediaPlayer.getCurrentTime()));
                durationLeft.setText(passedTime(javafx.util.Duration.seconds(mediaPlayer.getTotalDuration().toSeconds() - mediaPlayer.getCurrentTime().toSeconds())));
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
                mediaPlayer.setVolume(volume.getValue() / 100);
                volPer.setText(Double.toString(volume.getValue()));
            }
        });
        volume.setValue(mediaPlayer.getVolume() * 100);
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

    private void setPauseShortcuts(Button button) {
        Scene scene = button.getScene();
        scene.getAccelerators().put(
                new KeyCodeCombination(KeyCode.SPACE),
                new Runnable() {
                    @FXML
                    public void run() {
                        button.fire();
                    }
                }
        );
    }

    private void setVolumeShortcuts(Slider slider) {
        Scene scene = slider.getScene();
        scene.getAccelerators().put(
                new KeyCodeCombination(KeyCode.UP),
                new Runnable() {
                    @FXML
                    public void run() {
                        slider.setValue(slider.getValue() + 10);
                        volume.valueProperty().addListener(new InvalidationListener() {
                            @Override
                            public void invalidated(Observable observable) {
                                mediaPlayer.setVolume(volume.getValue() / 100);
                            }
                        });
                    }
                }
        );
    }

    private void setVolumeShortcuts2(Slider slider) {
        Scene scene = slider.getScene();
        scene.getAccelerators().put(
                new KeyCodeCombination(KeyCode.DOWN),
                new Runnable() {
                    @FXML
                    public void run() {
                        slider.setValue(slider.getValue() - 10);
                        volume.valueProperty().addListener(new InvalidationListener() {
                            @Override
                            public void invalidated(Observable observable) {
                                mediaPlayer.setVolume(volume.getValue() / 100);
                            }
                        });
                    }
                }
        );
    }

    private void setTimeLineSlider(Slider slider) {
        Scene scene = slider.getScene();
        scene.getAccelerators().put(
                new KeyCodeCombination(KeyCode.RIGHT),
                new Runnable() {
                    @FXML
                    public void run() {
                        stepForward.fire();
                    }
                }
        );
    }

    private void setTimeLineSliderBack(Slider slider) {
        Scene scene = slider.getScene();
        scene.getAccelerators().put(
                new KeyCodeCombination(KeyCode.LEFT),
                new Runnable() {
                    @FXML
                    public void run() {
                        stepLeft.fire();
                    }
                }
        );
    }

    public void setShortcuts() {
        setPauseShortcuts(pause);
        setVolumeShortcuts(volume);
        setVolumeShortcuts2(volume);
        setTimeLineSlider(timeLine);
        setTimeLineSliderBack(timeLine);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        openFile.setOnAction(event -> {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
            }
            FileChooser fileChooser = new FileChooser();
            FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("select a file",
                    "*.mp3", "*.mp4");
            fileChooser.getExtensionFilters().add(extensionFilter);
            File file = fileChooser.showOpenDialog(null);
            if (file != null) {
                filePath = file.toURI().toString();
                media = new Media(filePath);
                pathItem pathItem = new pathItem();
                pathItem.setPath(filePath);
                pathItem.setPath(pathItem.getPath());
                mediaPlayer = new MediaPlayer(media);
                mediaView.setMediaPlayer(mediaPlayer);
                mediaPlayer.play();
                DoubleProperty width = mediaView.fitWidthProperty();
                DoubleProperty height = mediaView.fitHeightProperty();
                width.bind(Bindings.selectDouble(mediaView.sceneProperty(), "width"));
                height.bind(Bindings.selectDouble(mediaView.sceneProperty(), "height"));
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
                volume.setValue(mediaPlayer.getVolume() * 100);
                volume.valueProperty().addListener(new InvalidationListener() {
                    @Override
                    public void invalidated(Observable observable) {
                        mediaPlayer.setVolume(volume.getValue() / 100);
                        vol = mediaPlayer.getVolume();
                    }
                });
                volume.setOnMousePressed(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        mediaPlayer.setVolume(volume.getValue());
                    }
                });
                loopButton.setOnAction(event1 -> {
                    if (onLoop) {
                        onLoop = false;
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
                public void changed(ObservableValue<? extends javafx.util.Duration> observable,
                                    javafx.util.Duration oldValue, javafx.util.Duration newValue) {
                    durationPassed.setText(passedTime(mediaPlayer.getCurrentTime()));
                    durationLeft.setText(passedTime(javafx.util.Duration.seconds(mediaPlayer.getTotalDuration().toSeconds
                            () - mediaPlayer.getCurrentTime().toSeconds())));
                }
            });
            volume.valueProperty().addListener(new InvalidationListener() {
                @Override
                public void invalidated(Observable observable) {
                    volPer.setText(Double.toString(volume.getValue()) + "%");
                }
            });
            mediaPlayer.setOnEndOfMedia(new Runnable() {
                @Override
                public void run() {
                    vol = volume.getValue();
                    if (onLoop)
                        mediaPlayer.seek(javafx.util.Duration.seconds(0));
                    else {
                        playPlayList();
                    }
                }
            });
            if (!onLoop) {
                nextItem.setOnAction(event1 -> {
                    currItem++;
                    if (currItem >= paths.size() || currItem < 0) {
                        currItem = 0;
                    }
                    if (paths.size() != 0) {
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
                prevItem.setOnAction(event1 -> {
                    currItem--;
                    if (currItem >= paths.size() || currItem < 0) {
                        currItem = 0;
                    }
                    if (paths.size() != 0) {
                        if (currItem >= 0) {
                            mediaPlayer.stop();
                            pathItem pathItem = paths.get(currItem);
                            mediaView.setMediaPlayer(null);
                            Media media = new Media(pathItem.getPath());
                            mediaPlayer = new MediaPlayer(media);
                            mediaView.setMediaPlayer(mediaPlayer);
                            controlPlaySlier(mediaPlayer);
                            mediaPlayer.play();
                        }
                    }
                });
            }
        });
        exit.setOnAction(event -> {
            stage.close();
        });
        pause.setOnAction(event -> {
            if (!paused) {
                mediaPlayer.pause();
                this.paused = true;

            } else {
                mediaPlayer.play();
                this.paused = false;
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
        KeyCodeCombination code = new KeyCodeCombination(KeyCode.E);
        pause.setOnKeyPressed(event -> {
            if (code.match(event)) {
                mediaPlayer.pause();
            }
        });
    }
}

package controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;
import model.pathItem;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.util.ResourceBundle;

public class PlaylistController implements Initializable {
    @FXML
    private TableColumn<pathItem,String> que;

    @FXML
    private Button add;
    @FXML
    private TableView<pathItem> table;
    @FXML
    private Button delete;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        que.setCellValueFactory(new PropertyValueFactory<>("path"));
        table.getColumns().add(que);
        for(pathItem path:MediaViewController.paths){
            table.getItems().add(path);
        }
        add.setOnAction(event -> {
            String filePath = null;
            FileChooser fileChooser = new FileChooser();
            FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("file selection","*.mp3","*.MP4");
            fileChooser.getExtensionFilters().add(filter);
            File file = fileChooser.showOpenDialog(null);
            if(file !=null) {
                filePath = file.toURI().toString();
            }
            URL url=null;
            try {
                assert filePath != null;
                url = new URL(filePath);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            pathItem pathItem = new pathItem();
            pathItem.setPath(filePath);
            pathItem.setPath1(url);
            MediaViewController.paths.add(pathItem);
            table.getItems().add(pathItem);
        });
    }
}

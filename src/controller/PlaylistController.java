package controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;

import java.io.File;
import java.net.URL;
import java.nio.file.Path;
import java.util.ResourceBundle;

public class PlaylistController implements Initializable {
    @FXML
    private TableView<String> table;

    @FXML
    private Button add;

    @FXML
    private Button delete;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        for(String path:MediaViewController.paths){
            TableColumn<String,String>tableColumn = new TableColumn<>();
            tableColumn.setCellValueFactory(new PropertyValueFactory<String,String>(path));
            table.getColumns().add(tableColumn);
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
            MediaViewController.paths.add(filePath);
            table.getItems().add(filePath);
        });
    }
}

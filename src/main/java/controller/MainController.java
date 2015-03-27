package controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Song;

import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    // Define the table.
    @FXML
    TableView<Song> playlistViewer;
    @FXML
    TableColumn<Song, String> title;
    @FXML
    TableColumn<Song, String> artist;
    @FXML
    TableColumn<Song, String> album;

    // Add songs to the playlist viewer.
    ObservableList<Song> visiblePlaylist = FXCollections.observableArrayList(
            // TODO Should fill with all mp3s from each directory in view.
            new Song("Centuries", "Fall Out Boy", "Idk what album that's from")
    );

    /**
     * Starts the controller after its root element has been completely processed.
     *
     * @param location The location used to resolve relative paths for the root object, or null if the location is
     *                 not known.
     * @param resources The resources used to localize the root object, or null if the root object was not localized.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        title.setCellValueFactory(new PropertyValueFactory<Song, String>("Title"));
        artist.setCellValueFactory(new PropertyValueFactory<Song, String>("Artist"));
        album.setCellValueFactory(new PropertyValueFactory<Song, String>("Album"));
        playlistViewer.setItems(visiblePlaylist);
    }

    /**
     * Ends the program.
     */
    public void quit() {
        Platform.exit();
    }

}
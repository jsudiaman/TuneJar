package viewcontroller;

import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Song;

import java.io.File;
import java.io.IOException;
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

    /**
     * Starts the controller after its root element has been completely processed.
     *
     * @param location The location used to resolve relative paths for the root object, or null if the location is
     *                 not known.
     * @param resources The resources used to localize the root object, or null if the root object was not localized.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Add songs to the playlist viewer.
        ObservableList<Song> visiblePlaylist = null;
        try {
            visiblePlaylist = FXCollections.observableArrayList(
                // TODO Should fill with all mp3s from each directory in view.
                new Song(new Mp3File(new File("src/test/resources/Queen of the Night.mp3"))),
                new Song(new Mp3File(new File("src/test/resources/The End of Mankind.mp3"))),
                new Song(new Mp3File(new File("src/test/resources/Sunlight.mp3")))
            );
        } catch (IOException | UnsupportedTagException | InvalidDataException e) {
            e.printStackTrace();
        }

        title.setCellValueFactory(new PropertyValueFactory<>("Title"));
        artist.setCellValueFactory(new PropertyValueFactory<>("Artist"));
        album.setCellValueFactory(new PropertyValueFactory<>("Album"));
        playlistViewer.setItems(visiblePlaylist);
    }

    /**
     * Ends the program.
     */
    public void quit() {
        Platform.exit();
    }

}
package viewcontroller;

import com.sun.istack.internal.NotNull;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Playlist;
import model.Song;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;

import static model.DebugUtils.*;

public class MainController implements Initializable {

    Playlist loadedPlaylist;
    ObservableList<Song> visiblePlaylist;
    @FXML
    TableView<Song> table;
    @FXML
    TableColumn<Song, String> title;
    @FXML
    TableColumn<Song, String> artist;
    @FXML
    TableColumn<Song, String> album;
    @FXML
    Label statusBar = new Label();

    /**
     * Sets up the playlist viewer.
     *
     * @param location  The location used to resolve relative paths for the root object, or null if the location is
     *                  not known.
     * @param resources The resources used to localize the root object, or null if the root object was not localized.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        visiblePlaylist = FXCollections.observableArrayList();
        title.setCellValueFactory(new PropertyValueFactory<>("Title"));
        artist.setCellValueFactory(new PropertyValueFactory<>("Artist"));
        album.setCellValueFactory(new PropertyValueFactory<>("Album"));
        table.setItems(visiblePlaylist);
    }

    // --------------- File --------------- //

    /**
     * Ends the program.
     */
    public void quit() {
        Platform.exit();
    }

    // --------------- Playback --------------- //

    /**
     * Plays the selected song. If the media player is paused and
     * the selected song is the same as the one that was playing
     * before the pause, the song will be resumed instead.
     *
     * // TODO This will not work if the list is resorted.
     */
    public void play() {
        play(table.getFocusModel().getFocusedIndex());
    }

    public void pause() {
        loadedPlaylist.pause();
    }

    // --------------- Behind the Scenes --------------- //

    /**
     * Displays the playlist in the table.
     *
     * @param p A playlist
     */
    public void loadPlaylist(@NotNull Playlist p)  {
        loadedPlaylist = p;
        visiblePlaylist = FXCollections.observableArrayList(loadedPlaylist);
        table.setItems(visiblePlaylist);
    }

    /**
     * Plays the song at the specified row of the playlist.
     *
     * @param row The row that the song is located in
     */
    public void play(int row) {
        try {
            // Have the playlist point to the appropriate song, then play it
            table.getSelectionModel().select(row);
            loadedPlaylist.setCurrentSongIndex(row);
            loadedPlaylist.play();
            MainView.setEndOfSongAction(() -> play(loadedPlaylist.nextSong()));

            // Update the status bar accordingly
            statusBar.setText("Now Playing: " + loadedPlaylist.get(row).toString());
        } catch (NullPointerException e) {
            LOGGER.log(Level.SEVERE, "Failed to play song. " +
                    (visiblePlaylist.isEmpty() ? "The playlist was empty." : "The playlist was not empty."), e);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to play song.", e);
        }
    }

}
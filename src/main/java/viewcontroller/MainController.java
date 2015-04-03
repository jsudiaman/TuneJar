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

import static model.DebugUtils.exception;
import static model.DebugUtils.info;

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

    ObservableList<Song> visiblePlaylist;

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
        playlistViewer.setItems(visiblePlaylist);
    }

    /**
     * Plays the selected song.
     */
    public void playSelected() {
        try {
            Song song = playlistViewer.getFocusModel().getFocusedItem();
            song.play();

            String infoString = "Now Playing: " + song.toString();
            info(MainController.class, infoString);
            statusBar.setText(infoString);
        } catch (NullPointerException e) {
            exception(MainController.class, "Failed to play song. " +
                    (visiblePlaylist.isEmpty() ? "The playlist was empty." : "The playlist was not empty."), e);
        } catch (Exception e) {
            exception(MainController.class, "Failed to play song.", e);
        }
    }

    /**
     * Displays the playlist in the table.
     *
     * @param p A playlist
     */
    public void loadPlaylist(@NotNull Playlist p)  {
        visiblePlaylist = FXCollections.observableArrayList(p);
        playlistViewer.setItems(visiblePlaylist);
    }

    /**
     * Ends the program.
     */
    public void quit() {
        Platform.exit();
    }

}
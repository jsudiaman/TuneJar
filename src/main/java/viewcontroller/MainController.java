package viewcontroller;

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
            MainView.logger.log(Level.INFO, "Now Playing: " + song.toString());
            statusBar.setText("Now Playing: " + song.toString());
        } catch (NullPointerException e) {
            // Log the failure to play the song and indicate whether the playlist was empty.
            MainView.logger.log(Level.SEVERE, "Failed to play song. " +
                    (visiblePlaylist.isEmpty() ? "The playlist was empty." : "The playlist was not empty."), e);
        } catch (Exception e) {
            // Log the failure to play the song.
            MainView.logger.log(Level.SEVERE, "Failed to play song.", e);
        }
    }

    /**
     * Refreshes and displays the master playlist.
     */
    public void loadMasterPlaylist() {
        MainView.refresh();
        visiblePlaylist = FXCollections.observableArrayList(MainView.getMasterPlaylist());
        playlistViewer.setItems(visiblePlaylist);
        statusBar.setText("Loading successful");
    }

    /**
     * Ends the program.
     */
    public void quit() {
        Platform.exit();
    }

}
package viewcontroller;

import com.sun.istack.internal.NotNull;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import model.Playlist;
import model.Song;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;

import static model.DebugUtils.LOGGER;

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
    @FXML
    MenuItem pauseButton = new MenuItem();

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

        // When a song is double clicked, play it.
        table.setOnMouseClicked(click -> {
            if (click.getClickCount() == 2) {
                play();
            }
        });

        // When enter is pressed, play the selected song.
        table.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                play();
            }
        });
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
     * Plays or resumes the selected song.
     */
    public void play() {
        pauseButton.setText("Pause");
        play(table.getFocusModel().getFocusedIndex());
    }

    /**
     * Handling for the pause button. If the pause button says "Pause", it
     * will pause the currently playing song, then change to "Resume".
     * <br><br>
     * If it says "Resume", it will resume the currently playing song,
     * then change to "Pause".
     * <br><br>
     * If it says anything else, or if a pause is attempted without a
     * song playing, the error will be logged.
     */
    public void pause() {
        try {
            if (pauseButton.getText().equals("Pause")) {
                statusBar.setText("Paused: " + MainView.getNowPlaying().toString());
                MainView.getNowPlaying().pause();
                pauseButton.setText("Resume");
            } else if (pauseButton.getText().equals("Resume")) {
                statusBar.setText("Now Playing: " + MainView.getNowPlaying().toString());
                MainView.getNowPlaying().play();
                pauseButton.setText("Pause");
            } else {
                LOGGER.log(Level.SEVERE, "Invalid text for pause button detected, text was: " + pauseButton.getText());
            }
        } catch (NullPointerException e) {
            LOGGER.log(Level.WARNING, "An attempt was made to pause a song without one playing.");
        }
    }

    /**
     * Stops the currently playing song.
     */
    public void stop() {
        if (MainView.getNowPlaying() == null) return;

        statusBar.setText("");
        pauseButton.setText("Pause");
        MainView.getNowPlaying().stop();
    }

    /**
     * Plays the previous song.
     */
    public void playPrev() {
        if (MainView.getNowPlaying() == null) {
            LOGGER.log(Level.WARNING, "No song is currently playing.");
            return;
        }

        int row = visiblePlaylist.indexOf(MainView.getNowPlaying());
        if (row <= 0) {
            play(0);
        } else {
            play(row - 1);
        }
    }

    /**
     * Plays the next song.
     */
    public void playNext() {
        if (MainView.getNowPlaying() == null) {
            LOGGER.log(Level.WARNING, "No song is currently playing.");
            return;
        }

        int row = visiblePlaylist.indexOf(MainView.getNowPlaying());
        if (row + 1 >= visiblePlaylist.size()) {
            play(0);
        } else {
            play(row + 1);
        }
    }

    // --------------- Behind the Scenes --------------- //

    /**
     * Plays the song at the specified row of the playlist.
     *
     * @param row The row that the song is located in
     */
    public void play(int row) {
        try {
            // Have the playlist point to the appropriate song, then play it
            table.getSelectionModel().select(row);
            visiblePlaylist.get(row).play();
            MainView.setEndOfSongAction(this::playNext);

            // Update the status bar accordingly
            statusBar.setText("Now Playing: " + MainView.getNowPlaying().toString());
        } catch (NullPointerException e) {
            LOGGER.log(Level.SEVERE, "Failed to play song. " +
                    (visiblePlaylist.isEmpty() ? "The playlist was empty." : "The playlist was not empty."), e);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to play song.", e);
        }
    }

    /**
     * Displays the playlist in the table.
     *
     * @param p A playlist
     */
    public void loadPlaylist(@NotNull Playlist p) {
        loadedPlaylist = p;
        visiblePlaylist = FXCollections.observableArrayList(loadedPlaylist);
        table.setItems(visiblePlaylist);
    }

}
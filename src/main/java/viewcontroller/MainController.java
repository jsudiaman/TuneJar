package viewcontroller;

import java.net.URL;
import java.util.*;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import model.Playlist;
import model.Song;

public class MainController implements Initializable {

    // Lists
    ObservableList<Song> songList;
    ObservableList<Playlist> playlistList;

    // Helpers
    FileMenu fileMenu;
    PlaybackMenu playbackMenu;
    SongMenu songMenu;
    PlaylistMenu playlistMenu;

    // FXML Injections
    @FXML
    TableView<Song> songTable;
    @FXML
    TableColumn<Song, String> title;
    @FXML
    TableColumn<Song, String> artist;
    @FXML
    TableColumn<Song, String> album;

    @FXML
    TableView<Playlist> playlistTable;
    @FXML
    TableColumn<Playlist, String> name;

    @FXML
    Menu addToPlaylist = new Menu();
    @FXML
    MenuItem menuPause = new MenuItem();
    @FXML
    MenuItem menuRemoveSong = new MenuItem();
    @FXML
    MenuItem menuRenamePlaylist = new MenuItem();
    @FXML
    MenuItem menuDeletePlaylist = new MenuItem();

    @FXML
    Button shortcutPause = new Button();

    @FXML
    Label status = new Label();

    @FXML
    Slider volumeSlider = new Slider();

    // --------------- Initialization --------------- //

    /**
     * Sets up the playlist viewer.
     *
     * @param location
     *            The location used to resolve relative paths for the root object, or null if the
     *            location is not known.
     * @param resources
     *            The resources used to localize the root object, or null if the root object was not
     *            localized.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize the helpers.
        fileMenu = new FileMenu(this);
        playbackMenu = new PlaybackMenu(this);
        songMenu = new SongMenu(this);
        playlistMenu = new PlaylistMenu(this);

        // Initialize the song table.
        songList = FXCollections.observableArrayList();
        title.setCellValueFactory(new PropertyValueFactory<>("Title"));
        artist.setCellValueFactory(new PropertyValueFactory<>("Artist"));
        album.setCellValueFactory(new PropertyValueFactory<>("Album"));
        songTable.setItems(songList);
        songTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        // Initialize the playlist table.
        playlistList = FXCollections.observableArrayList();
        name.setCellValueFactory(new PropertyValueFactory<>("Name"));
        playlistTable.setItems(playlistList);

        // When a song is selected, update the status bar.
        songTable.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldSelection, newSelection) -> {
                status.setText(MainView.getNowPlaying() != null ? "Now Playing: "
                        + MainView.getNowPlaying().toString() : "");
            }
        );

        // When a song is double clicked, play it.
        songTable.setRowFactory(param -> {
            TableRow<Song> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()
                        && event.getButton().equals(MouseButton.PRIMARY)) {
                    play();
                }
            });
            return row;
        });

        // When ENTER is pressed and a song is focused, play the focused song.
        songTable.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                play();
            }
        });

        // Add listeners to all playlists in the playlist table.
        menuRemoveSong.setDisable(true);
        playlistTable.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> {
                if (newValue == null) {
                    return;
                }

                // When a playlist is selected, display it.
                songList = FXCollections.observableArrayList(playlistTable.getSelectionModel().
                        getSelectedItem());
                songTable.setItems(songList);

                // The master playlist cannot be renamed, deleted, or altered,
                // so disable that functionality if the master playlist is selected.
                menuRemoveSong.setDisable(newValue.getName().equals("All Music"));
                menuRenamePlaylist.setDisable(newValue.getName().equals("All Music"));
                menuDeletePlaylist.setDisable(newValue.getName().equals("All Music"));
            }
        );

        // Initialize the volume slider.
        volumeSlider.valueProperty().addListener(
            (observable, oldValue, newValue) -> {
                MainView.setVolume(newValue.doubleValue());
            }
        );
    }

    // --------------- File --------------- //

    public void createPlaylistButton() {
        fileMenu.createPlaylist();
    }

    public void quit() {
        fileMenu.quit();
    }

    public void addDirectory() {
        fileMenu.addDirectory();
    }

    public void removeDirectory() {
        fileMenu.removeDirectory();
    }

    // --------------- Playback --------------- //

    /**
     * Plays or resumes the selected song.
     */
    public void play() {
        playbackMenu.play();
    }

    public void pause() {
        playbackMenu.pause();
    }

    public void stop() {
        playbackMenu.stop();
    }

    public void playPrev() {
        playbackMenu.playPrev();
    }

    public void playNext() {
        playbackMenu.playNext();
    }

    // --------------- Song --------------- //

    public void editSong() {
        songMenu.editSong();
    }

    public void toNewPlaylist() {
        songMenu.toNewPlaylist();
    }

    public void removeSong() {
        songMenu.removeSong();
    }

    public void search() {
        songMenu.search();
    }

    // --------------- Playlist --------------- //

    public void shuffle() {
        playlistMenu.shuffle();
    }

    public void renamePlaylist() {
        playlistMenu.renamePlaylist();
    }

    public void deletePlaylist() {
        playlistMenu.deletePlaylist();
    }

    // --------------- Utilities --------------- //

    /**
     * Scrolls to and focuses the specified row in the specified table.
     *
     * @param t
     *            The table in which the selection occurs
     * @param index
     *            The row that should be selected
     */
    void focus(TableView<?> t, int index) {
        Platform.runLater(() -> {
            t.requestFocus();
            t.getSelectionModel().select(index);
            t.getFocusModel().focus(index);
            t.scrollTo(index);
        });
    }

    void refreshTables() {
        // Keep tabs on what was selected before.
        List<Integer> playlistIndices = new ArrayList<>(playlistTable.getSelectionModel()
                .getSelectedIndices());
        List<Integer> songIndices = new ArrayList<>(songTable.getSelectionModel()
                .getSelectedIndices());

        // Where the actual "refreshing" is done
        songTable.getColumns().get(0).setVisible(false);
        songTable.getColumns().get(0).setVisible(true);
        songTable.getSelectionModel().select(0);

        playlistTable.getColumns().get(0).setVisible(false);
        playlistTable.getColumns().get(0).setVisible(true);
        playlistTable.getSelectionModel().select(0);

        // Re-select what was selected before.
        if (!playlistIndices.isEmpty()) {
            playlistTable.getSelectionModel().clearSelection();
            for (Integer i : playlistIndices) {
                playlistTable.getSelectionModel().select(i);
            }
        }
        if (!songIndices.isEmpty()) {
            songTable.getSelectionModel().clearSelection();
            for (Integer i : songIndices) {
                songTable.getSelectionModel().select(i);
            }
        }
    }

}
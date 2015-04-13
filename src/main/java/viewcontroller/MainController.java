package viewcontroller;

import java.net.URL;
import java.util.ResourceBundle;

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

import com.sun.istack.internal.NotNull;

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
    MenuBar topMenuBar = new MenuBar();
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
    ToolBar shortcutBar = new ToolBar();

    @FXML
    Slider volumeSlider = new Slider();

    // --------------- Initialization --------------- //

    /**
     * Sets up the playlist viewer.
     *
     * @param location
     *            The location used to resolve relative paths for the root
     *            object, or null if the location is not known.
     * @param resources
     *            The resources used to localize the root object, or null if the
     *            root object was not localized.
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

        // Initialize the playlist table.
        playlistList = FXCollections.observableArrayList();
        name.setCellValueFactory(new PropertyValueFactory<>("Name"));
        playlistTable.setItems(playlistList);

        // When a song is double clicked, play it.
        songTable.setRowFactory(param -> {
            TableRow<Song> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty() && event.getButton().equals(MouseButton.PRIMARY)) {
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
        playlistTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) return;

            // When a playlist is selected, display it.
            songList = FXCollections.observableArrayList(playlistTable.getSelectionModel().getSelectedItem());
            songTable.setItems(songList);

            // The master playlist cannot be renamed, deleted, or altered,
            // so disable that functionality if the master playlist is selected.
            menuRemoveSong.setDisable(newValue.getName().equals("All Music"));
            menuRenamePlaylist.setDisable(newValue.getName().equals("All Music"));
            menuDeletePlaylist.setDisable(newValue.getName().equals("All Music"));
        });

        // Initialize the volume slider.
        volumeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            MainView.setVolume(newValue.doubleValue());
        });
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

    // --------------- Playlist --------------- //

    public void loadPlaylist(@NotNull Playlist p) {
        playlistMenu.loadPlaylist(p);
    }
    
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
        int playlistIndex = playlistTable.getFocusModel().getFocusedIndex();
        int songIndex = songTable.getFocusModel().getFocusedIndex();

        // Where the actual "refreshing" is done
        songTable.getColumns().get(0).setVisible(false);
        songTable.getColumns().get(0).setVisible(true);
        songTable.getSelectionModel().select(0);

        playlistTable.getColumns().get(0).setVisible(false);
        playlistTable.getColumns().get(0).setVisible(true);
        playlistTable.getSelectionModel().select(0);

        // Re-select what was selected before.
        if (playlistIndex >= 0) playlistTable.getSelectionModel().select(playlistIndex);
        if (songIndex >= 0) songTable.getSelectionModel().select(songIndex);
    }

}
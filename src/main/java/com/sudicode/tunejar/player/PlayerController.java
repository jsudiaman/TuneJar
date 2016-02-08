package com.sudicode.tunejar.player;

import com.sudicode.tunejar.menu.FileMenu;
import com.sudicode.tunejar.menu.PlaybackMenu;
import com.sudicode.tunejar.menu.PlaylistMenu;
import com.sudicode.tunejar.menu.SongMenu;
import com.sudicode.tunejar.menu.ThemeMenu;
import com.sudicode.tunejar.menu.VolumeMenu;
import com.sudicode.tunejar.song.Playlist;
import com.sudicode.tunejar.song.Song;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Slider;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;

public class PlayerController implements Initializable {

    // Lists
    private ObservableList<Song> songList;
    private ObservableList<Playlist> playlistList;

    // Menus
    private FileMenu fileMenu;
    private PlaybackMenu playbackMenu;
    private PlaylistMenu playlistMenu;
    private SongMenu songMenu;
    private ThemeMenu themeMenu;
    private VolumeMenu volumeMenu;

    // FXML Injections
    @FXML
    private TableView<Song> songTable;
    @FXML
    private TableColumn<Song, String> title;
    @FXML
    private TableColumn<Song, String> artist;
    @FXML
    private TableColumn<Song, String> album;

    @FXML
    private TableView<Playlist> playlistTable;
    @FXML
    private TableColumn<Playlist, String> name;

    @FXML
    private Menu addToPlaylist = new Menu();
    @FXML
    private MenuItem menuPause = new MenuItem();
    @FXML
    private MenuItem menuRemoveSong = new MenuItem();
    @FXML
    private MenuItem menuRenamePlaylist = new MenuItem();
    @FXML
    private MenuItem menuDeletePlaylist = new MenuItem();

    @FXML
    private Button shortcutPause = new Button();

    @FXML
    private Label status = new Label();

    @FXML
    private Menu themeSelector = new Menu();

    @FXML
    private Slider volumeSlider = new Slider();

    // --------------- Initialization --------------- //

    /**
     * Sets up the playlist viewer.
     *
     * @param location The location used to resolve relative paths for the root object, or <code>null</code> if the
     *        location is not known.
     * @param resources The resources used to localize the root object, or <code>null</code> if the root object was not
     *        localized.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize the menus.
        setFileMenu(new FileMenu(this));
        setPlaybackMenu(new PlaybackMenu(this));
        setPlaylistMenu(new PlaylistMenu(this));
        songMenu = new SongMenu(this);
        themeMenu = new ThemeMenu(this);
        volumeMenu = new VolumeMenu(this);

        // Initialize the song table.
        setSongList(FXCollections.observableArrayList());
        getTitleColumn().setCellValueFactory(new PropertyValueFactory<>("Title"));
        getArtistColumn().setCellValueFactory(new PropertyValueFactory<>("Artist"));
        getAlbumColumn().setCellValueFactory(new PropertyValueFactory<>("Album"));
        getSongTable().setItems(getSongList());
        getSongTable().getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        getSongTable().getSortOrder().addListener((ListChangeListener<TableColumn<Song, ?>>) c -> {
            // When sort order is modified, save changes in the option file
            List<String> list = new ArrayList<>();
            getSongTable().getSortOrder().forEach(t -> list.add(t.getId()));
            Player.getPlayer().getOptions().setSortOrder(list.toArray(new String[list.size()]));

            // For custom playlists, restore the original order when unsorted
            if (list.isEmpty()) {
                int i = getPlaylistTable().getSelectionModel().getFocusedIndex();
                getPlaylistTable().getSelectionModel().select(0);
                getPlaylistTable().getSelectionModel().select(i);
            }
        });

        // Initialize the playlist table.
        setPlaylistList(FXCollections.observableArrayList());
        name.setCellValueFactory(new PropertyValueFactory<>("Name"));
        getPlaylistTable().setItems(getPlaylistList());

        // When a song is selected, update the status bar.
        getSongTable().getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            getStatus().setText(Player.getPlayer().getNowPlaying() != null
                    ? "Now Playing: " + Player.getPlayer().getNowPlaying().toString() : "");
        });

        // When a song is double clicked, play it.
        getSongTable().setRowFactory(param -> {
            TableRow<Song> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty() && event.getButton().equals(MouseButton.PRIMARY)) {
                    play();
                }
            });
            return row;
        });

        // When ENTER is pressed and a song is focused, play the focused song.
        getSongTable().setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                play();
            }
        });

        // Add listeners to all playlists in the playlist table.
        menuRemoveSong.setDisable(true);
        getPlaylistTable().getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                return;
            }

            // Cache sort order
            List<TableColumn<Song, ?>> sortOrder = getSortOrder();

            // When a playlist is selected, display it.
            setSongList(FXCollections.observableArrayList(getPlaylistTable().getSelectionModel().getSelectedItem()));
            getSongTable().setItems(getSongList());

            // The master playlist cannot be renamed, deleted, or altered,
            // so disable that functionality if the master playlist is selected.
            menuRemoveSong.setDisable(newValue.getName().equals("All Music"));
            menuRenamePlaylist.setDisable(newValue.getName().equals("All Music"));
            menuDeletePlaylist.setDisable(newValue.getName().equals("All Music"));

            // Restore sort order
            setSortOrder(sortOrder);
        });

        // Initialize the volume slider.
        initVolume();

        // Initialize the theme menu.
        initThemes();
    }

    // --------------- File --------------- //

    public void createPlaylistButton() {
        getFileMenu().createPlaylist();
    }

    public void quit() {
        getFileMenu().quit();
    }

    public void addDirectory() {
        getFileMenu().addDirectory();
    }

    public void removeDirectory() {
        getFileMenu().removeDirectory();
    }

    // --------------- Playback --------------- //

    /**
     * Plays or resumes the selected song.
     */
    public void play() {
        getPlaybackMenu().play();
    }

    public void pause() {
        getPlaybackMenu().pause();
    }

    public void stop() {
        getPlaybackMenu().stop();
    }

    public void playPrev() {
        getPlaybackMenu().playPrev();
    }

    public void playNext() {
        getPlaybackMenu().playNext();
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
        getPlaylistMenu().shuffle();
    }

    public void renamePlaylist() {
        getPlaylistMenu().renamePlaylist();
    }

    public void deletePlaylist() {
        getPlaylistMenu().deletePlaylist();
    }

    // --------------- Themes --------------- //

    public void initThemes() {
        themeMenu.init();
    }

    // --------------- Volume --------------- //

    public void initVolume() {
        volumeMenu.init();
    }

    // --------------- Utilities --------------- //

    /**
     * Scrolls to and focuses the specified row in the specified table.
     *
     * @param t The table in which the selection occurs
     * @param index The row that should be selected
     */
    public void focus(TableView<?> t, int index) {
        t.requestFocus();
        t.getSelectionModel().select(index);
        t.getFocusModel().focus(index);
        t.scrollTo(index);
    }

    public void refreshTables() {
        // Keep tabs on what was selected before.
        List<Playlist> selectedPlaylists = new ArrayList<>(getPlaylistTable().getSelectionModel().getSelectedItems());
        List<Song> selectedSongs = new ArrayList<>(getSongTable().getSelectionModel().getSelectedItems());

        // Where the actual "refreshing" is done
        getSongTable().getColumns().get(0).setVisible(false);
        getSongTable().getColumns().get(0).setVisible(true);
        getSongTable().getSelectionModel().select(0);

        getPlaylistTable().getColumns().get(0).setVisible(false);
        getPlaylistTable().getColumns().get(0).setVisible(true);
        getPlaylistTable().getSelectionModel().select(0);

        // Re-select what was selected before.
        if (!selectedPlaylists.isEmpty()) {
            getPlaylistTable().getSelectionModel().clearSelection();
            for (Playlist p : selectedPlaylists) {
                getPlaylistTable().getSelectionModel().select(p);
            }
        }
        if (!selectedSongs.isEmpty()) {
            getSongTable().getSelectionModel().clearSelection();
            for (Song s : selectedSongs) {
                getSongTable().getSelectionModel().select(s);
            }
        }
    }

    // --------------- Getters and Setters --------------- //

    public ObservableList<Playlist> getPlaylistList() {
        return playlistList;
    }

    private void setPlaylistList(ObservableList<Playlist> playlistList) {
        this.playlistList = playlistList;
    }

    public TableView<Playlist> getPlaylistTable() {
        return playlistTable;
    }

    public TableView<Song> getSongTable() {
        return songTable;
    }

    public Label getStatus() {
        return status;
    }

    public Button getShortcutPause() {
        return shortcutPause;
    }

    public MenuItem getMenuPause() {
        return menuPause;
    }

    public Slider getVolumeSlider() {
        return volumeSlider;
    }

    public Menu getAddToPlaylist() {
        return addToPlaylist;
    }

    public ObservableList<Song> getSongList() {
        return songList;
    }

    public void setSongList(ObservableList<Song> songList) {
        this.songList = songList;
    }

    public Menu getThemeSelector() {
        return themeSelector;
    }

    public TableColumn<Song, String> getTitleColumn() {
        return title;
    }

    public TableColumn<Song, String> getArtistColumn() {
        return artist;
    }

    public TableColumn<Song, String> getAlbumColumn() {
        return album;
    }

    public Player getPlayer() {
        return Player.getPlayer();
    }

    public PlaybackMenu getPlaybackMenu() {
        return playbackMenu;
    }

    private void setPlaybackMenu(PlaybackMenu playbackMenu) {
        this.playbackMenu = playbackMenu;
    }

    public PlaylistMenu getPlaylistMenu() {
        return playlistMenu;
    }

    private void setPlaylistMenu(PlaylistMenu playlistMenu) {
        this.playlistMenu = playlistMenu;
    }

    public FileMenu getFileMenu() {
        return fileMenu;
    }

    private void setFileMenu(FileMenu fileMenu) {
        this.fileMenu = fileMenu;
    }

    public List<TableColumn<Song, ?>> getSortOrder() {
        return new ArrayList<>(getSongTable().getSortOrder());
    }

    public void setSortOrder(List<TableColumn<Song, ?>> sortOrder) {
        getSongTable().getSortOrder().clear();
        getSongTable().getSortOrder().addAll(sortOrder);
    }

}

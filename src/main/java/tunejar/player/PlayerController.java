package tunejar.player;

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
import tunejar.menu.FileMenu;
import tunejar.menu.PlaybackMenu;
import tunejar.menu.PlaylistMenu;
import tunejar.menu.SongMenu;
import tunejar.menu.ThemeMenu;
import tunejar.menu.VolumeMenu;
import tunejar.song.Playlist;
import tunejar.song.Song;

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
	 * @param location
	 *            The location used to resolve relative paths for the root
	 *            object, or <code>null</code> if the location is not known.
	 * @param resources
	 *            The resources used to localize the root object, or
	 *            <code>null</code> if the root object was not localized.
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// Initialize the menus.
		fileMenu = new FileMenu(this);
		playbackMenu = new PlaybackMenu(this);
		playlistMenu = new PlaylistMenu(this);
		songMenu = new SongMenu(this);
		themeMenu = new ThemeMenu(this);
		volumeMenu = new VolumeMenu(this);

		// Initialize the song table.
		songList = FXCollections.observableArrayList();
		title.setCellValueFactory(new PropertyValueFactory<>("Title"));
		artist.setCellValueFactory(new PropertyValueFactory<>("Artist"));
		album.setCellValueFactory(new PropertyValueFactory<>("Album"));
		songTable.setItems(songList);
		songTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		songTable.getSortOrder().addListener((ListChangeListener<TableColumn<Song, ?>>) c -> {
			List<String> list = new ArrayList<String>();
			songTable.getSortOrder().forEach(t -> list.add(t.getId()));
			Player.getPlayer().getOptions().setSortOrder(list.toArray(new String[0]));
		});

		// Initialize the playlist table.
		playlistList = FXCollections.observableArrayList();
		name.setCellValueFactory(new PropertyValueFactory<>("Name"));
		playlistTable.setItems(playlistList);

		// When a song is selected, update the status bar.
		songTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
			status.setText(Player.getPlayer().getNowPlaying() != null
					? "Now Playing: " + Player.getPlayer().getNowPlaying().toString() : "");
		});

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
			if (newValue == null) {
				return;
			}

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
		initVolume();

		// Initailize the theme menu.
		initThemes();
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
	 * @param t
	 *            The table in which the selection occurs
	 * @param index
	 *            The row that should be selected
	 */
	public void focus(TableView<?> t, int index) {
		t.requestFocus();
		t.getSelectionModel().select(index);
		t.getFocusModel().focus(index);
		t.scrollTo(index);
	}

	public void refreshTables() {
		// Keep tabs on what was selected before.
		List<Playlist> selectedPlaylists = new ArrayList<>(playlistTable.getSelectionModel().getSelectedItems());
		List<Song> selectedSongs = new ArrayList<>(songTable.getSelectionModel().getSelectedItems());
		List<TableColumn<Song, ?>> sorted = new ArrayList<>(songTable.getSortOrder());

		// Where the actual "refreshing" is done
		songTable.getColumns().get(0).setVisible(false);
		songTable.getColumns().get(0).setVisible(true);
		songTable.getSelectionModel().select(0);

		playlistTable.getColumns().get(0).setVisible(false);
		playlistTable.getColumns().get(0).setVisible(true);
		playlistTable.getSelectionModel().select(0);

		// Re-select what was selected before.
		if (!selectedPlaylists.isEmpty()) {
			playlistTable.getSelectionModel().clearSelection();
			for (Playlist p : selectedPlaylists) {
				playlistTable.getSelectionModel().select(p);
			}
		}
		if (!selectedSongs.isEmpty()) {
			songTable.getSelectionModel().clearSelection();
			for (Song s : selectedSongs) {
				songTable.getSelectionModel().select(s);
			}
		}
		songTable.getSortOrder().clear();
		songTable.getSortOrder().addAll(sorted);
	}

	// --------------- Getters and Setters --------------- //

	public ObservableList<Playlist> getPlaylistList() {
		return playlistList;
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

	public PlaylistMenu getPlaylistMenu() {
		return playlistMenu;
	}

	public FileMenu getFileMenu() {
		return fileMenu;
	}

}

package viewcontroller;

import com.sun.istack.internal.NotNull;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import model.Playlist;
import model.Song;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.logging.Level;

import static model.DebugUtils.LOGGER;

public class MainController implements Initializable {

    // Lists
    private ObservableList<Song> songList;
    private ObservableList<Playlist> playlistList;

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
     * @param location  The location used to resolve relative paths for the root object, or null if the location is
     *                  not known.
     * @param resources The resources used to localize the root object, or null if the root object was not localized.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Disable all interactivity until MainView::init() completes.
        for(Menu m : topMenuBar.getMenus()) {
            m.setDisable(true);
        }
        for(Node n : shortcutBar.getItems()) {
            n.setDisable(true);
        }

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
            // When a playlist is selected, display it.
            songList = FXCollections.observableArrayList(playlistTable.getSelectionModel().getSelectedItem());
            songTable.setItems(songList);

            // The master playlist cannot be renamed, deleted, or altered, so disable that functionality if
            // the master playlist is selected.
            menuRemoveSong.setDisable(newValue.getName().equals("All Music"));
            menuRenamePlaylist.setDisable(newValue.getName().equals("All Music"));
            menuDeletePlaylist.setDisable(newValue.getName().equals("All Music"));
        });

        // Initialize the volume slider.
        volumeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            MainView.setVolume(newValue.doubleValue());
        });
    }

    public void enableInteractivity() {
        for(Menu m : topMenuBar.getMenus()) {
            m.setDisable(false);
        }

        for(Node n : shortcutBar.getItems()) {
            n.setDisable(false);
        }
    }

    // --------------- File --------------- //

    public void createPlaylistButton() {
        createPlaylist();
    }

    /**
     * Creates a new playlist.
     */
    private boolean createPlaylist() {
        // Prompt the user for a playlist name.
        TextInputDialog dialog = new TextInputDialog("Untitled Playlist");
        dialog.setTitle("New Playlist");
        dialog.setHeaderText("Create a new playlist");
        dialog.setContentText("Playlist name:");

        Optional<String> playlistName = dialog.showAndWait();
        if(playlistName.isPresent()) {
            String pName = playlistName.get();

            // Playlist creation fails if a playlist with the specified name already exists.
            for(Playlist p : playlistList) {
                if(p.getName().equalsIgnoreCase(pName)) {
                    Alert conflictAlert = new Alert(Alert.AlertType.WARNING);
                    conflictAlert.setTitle("Playlist Conflict");
                    conflictAlert.setHeaderText("A playlist named " + pName + " already exists.");
                    conflictAlert.setContentText("Please rename/delete the existing playlist, or choose another name.");
                    conflictAlert.showAndWait();
                    return false;
                }
            }

            Playlist p = new Playlist(pName);
            try {
                p.save();
                loadPlaylist(p);
                return true;
            } catch (IOException e) {
                // Playlist creation fails if it cannot be successfully saved.
                Alert failAlert = new Alert(Alert.AlertType.ERROR);
                failAlert.setTitle("Playlist Write Error");
                failAlert.setHeaderText("Failed to create playlist: " + pName);
                failAlert.setContentText("The playlist failed to save. Make sure the name does not contain any " +
                        "illegal characters.");
                failAlert.showAndWait();
                LOGGER.log(Level.SEVERE, "Failed to save playlist: " + pName + ".m3u", e);
            }
        }
        return false;
    }

    /**
     * Asks the user if it is okay to end the program. If so,
     * end the program.
     */
    public void quit() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Exit JVMP3");
        alert.setHeaderText("Confirm Exit");
        alert.setContentText("Are you sure you would like to exit?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) Platform.exit();
    }

    // --------------- Playback --------------- //

    /**
     * Plays or resumes the selected song.
     */
    public void play() {
        int index = songTable.getFocusModel().getFocusedIndex();
        if(songList.isEmpty() || index < 0 || index >= songList.size()) {
            status.setText("No song selected.");
            return;
        }
        shortcutPause.setText("Pause");
        menuPause.setText("Pause");
        play(songTable.getFocusModel().getFocusedIndex());
    }

    /**
     * Plays the song at the specified row of the song table.
     *
     * @param row The row that the song is located in
     */
    private void play(int row) {
        try {
            // Have the playlist point to the appropriate song, then play it
            songTable.getSelectionModel().select(row);
            songList.get(row).play();
            MainView.setEndOfSongAction(this::playNext);

            // Update the status bar accordingly
            status.setText("Now Playing: " + MainView.getNowPlaying().toString());
        } catch (NullPointerException e) {
            LOGGER.log(Level.SEVERE, "Failed to play song. " +
                    (songList.isEmpty() ? "The playlist was empty." : "The playlist was not empty."), e);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to play song.", e);
        }
    }

    /**
     * Handling for the pause button. If the pause button says "Pause", it
     * will pause the currently playing song, then change to "Resume".
     * <br><br>
     * If it says "Resume", it will resume the currently playing song,
     * then change to "Pause".
     * <br><br>
     * If it says anything else, the error will be logged.
     */
    public void pause() {
        if (MainView.getNowPlaying() == null) {
            status.setText("No song is currently playing.");
            return;
        }

        if (menuPause.getText().equals("Pause")) {
            status.setText("Paused: " + MainView.getNowPlaying().toString());
            MainView.getNowPlaying().pause();
            shortcutPause.setText("Resume");
            menuPause.setText("Resume");
        } else if (menuPause.getText().equals("Resume")) {
            status.setText("Now Playing: " + MainView.getNowPlaying().toString());
            MainView.getNowPlaying().play();
            shortcutPause.setText("Pause");
            menuPause.setText("Pause");
        } else {
            LOGGER.log(Level.SEVERE, "Invalid text for pause button detected, text was: " + menuPause.getText());
        }
    }

    /**
     * Stops the currently playing song.
     */
    public void stop() {
        if (MainView.getNowPlaying() == null) {
            status.setText("No song is currently playing.");
            return;
        }

        status.setText("");
        menuPause.setText("Pause");
        MainView.getNowPlaying().stop();
    }

    /**
     * Plays the previous song.
     */
    public void playPrev() {
        if (MainView.getNowPlaying() == null) {
            status.setText("No song is currently playing.");
            return;
        }

        int row = songList.indexOf(MainView.getNowPlaying());
        row = (row <= 0) ? 0 : row - 1;
        play(row);
        songTable.getSelectionModel().select(row);
    }

    /**
     * Plays the next song.
     */
    public void playNext() {
        if (MainView.getNowPlaying() == null) {
            status.setText("No song is currently playing.");
            return;
        }

        int row = songList.indexOf(MainView.getNowPlaying());
        row = (row + 1 >= songList.size()) ? 0 : row + 1;
        play(row);
        songTable.getSelectionModel().select(row);
    }

    // --------------- Song --------------- //

    /**
     * Creates a user dialog that allows modification of the selected
     * song's ID3 tags.
     */
    public void editSong() {
        Song songToEdit = songTable.getSelectionModel().getSelectedItem();
        if (songToEdit == null) {
            status.setText("No song selected.");
            return;
        }

        // Create the editor dialog.
        Dialog<List<String>> editor = new Dialog<>();
        editor.setTitle("Song Editor");
        editor.setHeaderText("Editing " + songToEdit.toString());

        // Set the button types.
        ButtonType saveButton = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        editor.getDialogPane().getButtonTypes().addAll(saveButton, ButtonType.CANCEL);

        // Create the labels and fields.
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField title = new TextField();
        title.setPromptText("Title");
        title.setText(songToEdit.getTitle());

        TextField artist = new TextField();
        artist.setPromptText("Artist");
        artist.setText(songToEdit.getArtist());

        TextField album = new TextField();
        album.setPromptText("Album");
        album.setText(songToEdit.getAlbum());

        grid.add(new Label("Title:"), 0, 0);
        grid.add(title, 1, 0);
        grid.add(new Label("Artist:"), 0, 1);
        grid.add(artist, 1, 1);
        grid.add(new Label("Album:"), 0, 2);
        grid.add(album, 1, 2);

        editor.getDialogPane().setContent(grid);

        // Convert the result to an ArrayList of type String.
        editor.setResultConverter(param -> {
            if (param == saveButton) {
                List<String> list = new ArrayList<>();
                list.add(title.getText());
                list.add(artist.getText());
                list.add(album.getText());
                return list;
            }
            return null;
        });

        Optional<List<String>> newParams = editor.showAndWait();
        if (newParams.isPresent()) {
            songToEdit.setTag(newParams.get().get(0), newParams.get().get(1), newParams.get().get(2));
            refreshTables();
        }
    }

    /**
     * Creates a new playlist and adds the selected song to it.
     */
    public void toNewPlaylist() {
        Song songToAdd = songTable.getSelectionModel().getSelectedItem();
        if (songToAdd == null) {
            status.setText("No song was selected.");
            return;
        }
        if(createPlaylist()) {
            playlistList.get(playlistList.size() - 1).add(songToAdd);
        }
    }

    /**
     * Removes the selected song from the current playlist.
     */
    public void removeSong() {
        // Find the index of the song to remove.
        int songIndex = songTable.getSelectionModel().getSelectedIndex();
        if(songIndex < 0 || songIndex > songList.size()) {
            status.setText("No song selected.");
            return;
        }

        // Remove it, then save changes to the playlist.
        Playlist pl = playlistTable.getSelectionModel().getSelectedItem();
        pl.remove(songIndex);
        refreshTables();
        try {
            pl.save();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    // --------------- Playlist --------------- //

    /**
     * Adds a playlist to the playlist table, then loads it into the song table.
     *
     * @param p A playlist
     */
    public void loadPlaylist(@NotNull Playlist p) {
        playlistList.add(p);
        playlistTable.setItems(playlistList);
        focus(playlistTable, playlistList.size() - 1);

        songList = FXCollections.observableArrayList(p);
        songTable.setItems(songList);
        LOGGER.log(Level.INFO, "Loaded playlist: " + p.getName());

        // Enable the user to add songs to the playlist (unless the playlist is MainView::masterPlaylist).
        if(p.getName().equals("All Music")) return;
        MenuItem m = new MenuItem(p.getName());
        addToPlaylist.getItems().add(m);
        m.setOnAction(event -> {
            Song songToAdd = songTable.getSelectionModel().getSelectedItem();
            p.add(songToAdd);
            try {
                p.save();
            } catch (IOException e) {
                status.setText("Playlist \"" + p.getName() + "\" save unsuccessful.");
                LOGGER.log(Level.SEVERE, "Failed to save the playlist.", e);
            } finally {
                refreshTables();
                event.consume();
            }
        });
    }

    /**
     * Shuffles the song table. If a song is currently playing, it will
     * be moved to the top of the table and playback will continue.
     */
    public void shuffle() {
        if (songList.isEmpty()) {
            status.setText("No songs to shuffle.");
            return;
        }

        Collections.shuffle(songList);
        if (MainView.getNowPlaying() != null && songList.indexOf(MainView.getNowPlaying()) >= 0) {
            Collections.swap(songList, 0, songList.indexOf(MainView.getNowPlaying()));
        } else {
            play(0);
        }
        focus(songTable, 0);
    }

    /**
     * Renames the current playlist.
     */
    public void renamePlaylist() {
        Playlist pl = playlistTable.getSelectionModel().getSelectedItem();
        String oldName = pl.getName();
        File oldFile = new File(oldName + ".m3u");

        // Prompt the user for a playlist name.
        TextInputDialog dialog = new TextInputDialog(oldName);
        dialog.setTitle("Rename");
        dialog.setHeaderText("Please enter a new name for playlist \"" + pl.getName() + "\".");
        dialog.setContentText("New name:");
        Optional<String> playlistName = dialog.showAndWait();
        if(!playlistName.isPresent()) return;

        // Make sure that the user has picked a unique playlist name.
        for(Playlist p : playlistList) {
            if(p.getName().equalsIgnoreCase(playlistName.get())) {
                Alert conflictAlert = new Alert(Alert.AlertType.WARNING);
                conflictAlert.setTitle("Playlist Conflict");
                conflictAlert.setHeaderText("A playlist named " + playlistName.get() + " already exists.");
                conflictAlert.setContentText("Please rename/delete the existing playlist, or choose another name.");
                conflictAlert.showAndWait();
                return;
            }
        }

        try {
            // Rename the playlist and attempt to save changes.
            pl.setName(playlistName.get());
            pl.save();
            if(!oldFile.delete()) {
                status.setText("Could not delete the old file.");
            }
            refreshTables();

            // Also, rename the playlist in the "Song -> Add to...<PLAYLIST>" menu.
            for(MenuItem item : addToPlaylist.getItems()) {
                if(item.getText() == null) continue;
                if(item.getText().equals(oldName)) {
                    item.setText(playlistName.get());
                }
            }

        } catch (IOException e) {
            status.setText("Rename failed. See log.txt for details.");
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    /**
     * Asks the user if it is okay to delete the current playlist.
     * If it is okay, deletes the current playlist.
     */
    public void deletePlaylist() {
        Playlist pl = playlistTable.getSelectionModel().getSelectedItem();

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete");
        alert.setHeaderText("Confirm Deletion");
        alert.setContentText("Are you sure you would like to delete playlist \"" + pl.getName() + "\"?");
        Optional<ButtonType> result = alert.showAndWait();

        if (result.get() != ButtonType.OK) return;
        if (new File(pl.getName() + ".m3u").delete()) {
            playlistList.remove(pl);
            refreshTables();
        } else {
            status.setText("Deletion failed.");
        }
    }

    // --------------- Utilities --------------- //

    /**
     * The status bar displays the desired message.
     *
     * @param message A status bar message
     */
    public void setStatus(String message) {
        status.setText(message);
    }

    /**
     * Scrolls to and focuses the specified row in the specified table.
     *
     * @param t The table in which the selection occurs
     * @param index The row that should be selected
     */
    private void focus(TableView t, int index) {
        Platform.runLater(() -> {
            t.requestFocus();
            t.getSelectionModel().select(index);
            t.getFocusModel().focus(index);
            t.scrollTo(index);
        });
    }

    public void focusMasterPlaylist() {
        focus(playlistTable, 0);
    }

    private void refreshTables() {
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
        if(playlistIndex >= 0) playlistTable.getSelectionModel().select(playlistIndex);
        if(songIndex >= 0) songTable.getSelectionModel().select(songIndex);
    }

}
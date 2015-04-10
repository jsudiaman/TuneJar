package viewcontroller;

import com.sun.istack.internal.NotNull;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import model.Playlist;
import model.Song;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.logging.Level;

import static model.DebugUtils.LOGGER;

public class MainController implements Initializable {

    // Lists
    ObservableList<Song> songList;
    ObservableList<Playlist> playlistList;

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
    Label status = new Label();
    @FXML
    MenuItem pauseButton = new MenuItem();
    @FXML
    Menu addToPlaylist = new Menu();

    /**
     * Sets up the playlist viewer.
     *
     * @param location  The location used to resolve relative paths for the root object, or null if the location is
     *                  not known.
     * @param resources The resources used to localize the root object, or null if the root object was not localized.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
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

        // When a playlist is selected, display it.
        playlistTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            songList = FXCollections.observableArrayList(playlistTable.getSelectionModel().getSelectedItem());
            songTable.setItems(songList);
        });

        // Disable multi-selections.
        songTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        playlistTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    }

    // --------------- File --------------- //

    /**
     * Creates a new playlist.
     */
    public void createPlaylist() {
        TextInputDialog dialog = new TextInputDialog("Untitled Playlist");
        dialog.setTitle("New Playlist");
        dialog.setHeaderText("Create a new playlist");
        dialog.setContentText("Playlist name:");

        Optional<String> playlistName = dialog.showAndWait();
        playlistName.ifPresent(s -> {
            // Load the new playlist.
            Playlist p = new Playlist(playlistName.get());
            loadPlaylist(p);

            // Give the user the ability to add songs to the playlist via Song -> Add To...
            MenuItem m = new MenuItem(p.getName());
            addToPlaylist.getItems().add(m);
            m.setOnAction(event -> {
                Song songToAdd = songTable.getSelectionModel().getSelectedItem();
                p.add(songToAdd);
                status.setText("Added \"" + songToAdd.toString() + "\" to playlist \"" + p.getName() + "\".");
                event.consume();
            });

            // Save the playlist.
            try {
                p.saveAsM3U();
            } catch (IOException e) {
                String message = "Failed to save playlist: " + p.getName();
                LOGGER.log(Level.SEVERE, message, e);

            }
        });
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
        pauseButton.setText("Pause");
        play(songTable.getFocusModel().getFocusedIndex());
    }

    /**
     * Plays the song at the specified row of the song table.
     *
     * @param row The row that the song is located in
     */
    public void play(int row) {
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
            LOGGER.log(Level.WARNING, "No song is currently playing.");
            return;
        }

        if (pauseButton.getText().equals("Pause")) {
            status.setText("Paused: " + MainView.getNowPlaying().toString());
            MainView.getNowPlaying().pause();
            pauseButton.setText("Resume");
        } else if (pauseButton.getText().equals("Resume")) {
            status.setText("Now Playing: " + MainView.getNowPlaying().toString());
            MainView.getNowPlaying().play();
            pauseButton.setText("Pause");
        } else {
            LOGGER.log(Level.SEVERE, "Invalid text for pause button detected, text was: " + pauseButton.getText());
        }
    }

    /**
     * Stops the currently playing song.
     */
    public void stop() {
        if (MainView.getNowPlaying() == null) {
            LOGGER.log(Level.WARNING, "No song is currently playing.");
            return;
        }

        status.setText("");
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

        int row = songList.indexOf(MainView.getNowPlaying());
        if (row <= 0) {
            play(0);
        } else {
            play(row - 1);
        }
        // TODO focusNowPlaying();
    }

    /**
     * Plays the next song.
     */
    public void playNext() {
        if (MainView.getNowPlaying() == null) {
            LOGGER.log(Level.WARNING, "No song is currently playing.");
            return;
        }

        int row = songList.indexOf(MainView.getNowPlaying());
        if (row + 1 >= songList.size()) {
            play(0);
        } else {
            play(row + 1);
        }
        // TODO focusNowPlaying();
    }

    // --------------- Song --------------- //

    /**
     * Creates a user dialog that allows modification of the selected
     * song's ID3 tags.
     */
    public void edit() {
        Song songToEdit = songTable.getSelectionModel().getSelectedItem();
        if (songToEdit == null) {
            LOGGER.log(Level.WARNING, "No song selected.");
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

            // Get the song table to refresh the data
            songTable.getColumns().get(0).setVisible(false);
            songTable.getColumns().get(0).setVisible(true);
        }
    }

    /**
     * Creates a new playlist and adds the selected song to it.
     */
    public void addSongNewPlaylist() {
        Song songToAdd = songTable.getSelectionModel().getSelectedItem();
        int size = playlistList.size();
        createPlaylist();

        // If the playlistList's size increased by 1, we know that a playlist was created.
        // Therefore, we should add the selected song to it.
        if(size + 1 == playlistList.size()) {
            playlistList.get(size).add(songToAdd);
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
        select(playlistTable, playlistList.size() - 1);

        songList = FXCollections.observableArrayList(p);
        songTable.setItems(songList);
    }

    /**
     * Shuffles the song table. If a song is currently playing, it will
     * be moved to the top of the table and playback will continue.
     */
    public void shuffle() {
        Collections.shuffle(songList);
        if (MainView.getNowPlaying() != null) {
            Collections.swap(songList, 0, songList.indexOf(MainView.getNowPlaying()));
        } else {
            play(0);
        }
        select(songTable, 0);
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

    public void select(TableView t, int index) {
        Platform.runLater(() -> {
            t.requestFocus();
            t.getSelectionModel().select(index);
            t.getFocusModel().focus(index);
            t.scrollTo(index);
        });
    }

}
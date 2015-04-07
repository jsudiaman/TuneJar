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
import javafx.util.Callback;
import model.Playlist;
import model.Song;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
        table.setRowFactory(tv -> {
            TableRow<Song> row = new TableRow<>();
            row.setOnMouseClicked(click -> {
                if (click.getClickCount() == 2 && !row.isEmpty() && click.getButton().equals(MouseButton.PRIMARY)) {
                    play();
                }
            });
            return row;
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
     * If it says anything else, the error will be logged.
     */
    public void pause() {
        if (MainView.getNowPlaying() == null) {
            LOGGER.log(Level.WARNING, "No song is currently playing.");
            return;
        }

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
    }

    /**
     * Stops the currently playing song.
     */
    public void stop() {
        if (MainView.getNowPlaying() == null) {
            LOGGER.log(Level.WARNING, "No song is currently playing.");
            return;
        }

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

    // --------------- Song --------------- //
    public void edit() {
        Song songToEdit = table.getSelectionModel().getSelectedItem();
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
        if(newParams.isPresent()) {
            songToEdit.setTag(newParams.get().get(0), newParams.get().get(1), newParams.get().get(2));

            // Get the table to refresh the data
            table.getColumns().get(0).setVisible(false);
            table.getColumns().get(0).setVisible(true);
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
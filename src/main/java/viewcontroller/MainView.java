package viewcontroller;

import static model.DebugUtils.LOGGER;
import static model.DebugUtils.fatalException;
import static model.FileManipulator.*;

import java.io.*;
import java.net.URL;
import java.util.Collection;
import java.util.logging.Level;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import model.Playlist;
import model.Song;

import com.sun.istack.internal.Nullable;

public class MainView extends Application {

    // GUI
    private static MediaPlayer player;
    private static Song nowPlaying;

    // Data Structures
    private static Playlist masterPlaylist;
    private static Collection<File> directories;

    private static Stage primaryStage;

    /**
     * Calls Application::launch().
     *
     * @param args
     *            The command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Starts the program.
     *
     * @param primaryStage
     *            The stage that will hold the interface
     */
    @Override
    public void start(Stage primaryStage) {
        // Start the program
        try {
            init(primaryStage);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            fatalException(e);
        }
    }

    /**
     * Handles program initialization.
     *
     * @param primaryStage
     *            The stage that will hold the interface
     * @throws IOException
     *             Failed to load the FXML, or could not load/save a file.
     */
    private void init(Stage primaryStage) throws IOException {
        // Load the FXML file and display the interface.
        MainView.primaryStage = primaryStage;
        URL location = getClass().getResource("MainController.fxml");
        FXMLLoader fxmlLoader = new FXMLLoader();
        Parent root = fxmlLoader.load(location.openStream());

        Scene scene = new Scene(root, 1000, 600);
        scene.getStylesheets().add(getClass().getResource("DarkTheme.css").toString());

        primaryStage.setTitle("JVMP3");
        primaryStage.setScene(scene);
        primaryStage.show();

        // Load the directories. If none are present, prompt the user for one.
        try {
            directories = readDirectories();
        } catch (FileNotFoundException e) {
            directories = initialSetup(primaryStage);
        }

        final MainController controller = fxmlLoader.getController();
        controller.status.setText("Loading your songs, please be patient...");

        Thread loadSongs = new Thread (()-> {
            Platform.runLater(() -> {
                // Create and display a playlist containing all songs from each directory.
                refresh();
                controller.loadPlaylist(masterPlaylist);

                // Save the directories.
                try {
                    writeFiles(directories);
                } catch (IOException e) {
                    LOGGER.log(Level.SEVERE, "Failed to save directories.", e);
                    controller.status.setText("Failed to save directories.");
                }

                // Finally, load in all playlists from the working directory.
                controller.status.setText("");
                Collection<Playlist> playlistSet = null;
                try {
                    playlistSet = getPlaylists();
                } catch (IOException | NullPointerException e) {
                    LOGGER.log(Level.SEVERE, "Failed to load playlists from the working directory.", e);
                    fatalException(e);
                }
                if (playlistSet != null) {
                    playlistSet.forEach(controller::loadPlaylist);
                }
                controller.focusMasterPlaylist();
            });
        });
        loadSongs.start();
    }

    /**
     * The master playlist takes in all MP3 files that can be found in available
     * directories.
     */
    public static void refresh() {
        // Initialize the master playlist.
        masterPlaylist = new Playlist("All Music");

        // Then add all songs found in the directories to the master playlist.
        LOGGER.log(Level.INFO, "directories: " + (directories != null ? directories.toString() : null));
        for (File directory : directories) {
            LOGGER.log(Level.INFO, "Now adding songs from directory " + directory.toString());
            Collection<Song> songs = getSongs(directory);
            
            if (songs == null) {
                LOGGER.log(Level.SEVERE, "Failed to load songs from " + directory.toString() + ", skipping...");
            } else {
                masterPlaylist.addAll(getSongs(directory));
            }
        }
        LOGGER.log(Level.INFO, "Refresh successful!");
    }

    // ------------------- Media Player Controls ------------------- //

    /**
     * Loads a song into the media player, then plays it.
     *
     * @param song
     *            The song to play
     */
    public static void playSong(Song song, double volume) {
        if (nowPlaying != null) {
            nowPlaying.stop();
        }
        nowPlaying = song;
        LOGGER.log(Level.INFO, "Playing: " + nowPlaying.toString());
        String uriString = new File(song.getAbsoluteFilename()).toURI().toString();
        player = new MediaPlayer(new Media(uriString));
        player.setVolume(volume);
        player.play();
    }

    /**
     * Resumes the media player.
     */
    public static void resumePlayback() {
        if (player != null && nowPlaying != null) {
            LOGGER.log(Level.INFO, "Resuming: " + nowPlaying.toString());
            player.play();
        }
    }

    /**
     * Pauses the media player.
     */
    public static void pausePlayback() {
        if (player != null && nowPlaying != null) {
            LOGGER.log(Level.INFO, "Pausing: " + nowPlaying.toString());
            player.pause();
        }
    }

    /**
     * Stops the media player.
     */
    public static void stopPlayback() {
        if (player != null && nowPlaying != null) {
            LOGGER.log(Level.INFO, "Stopping: " + nowPlaying.toString());
            player.stop();
        }
        nowPlaying = null;
    }

    // ------------------- Getters and Setters ------------------- //

    /**
     * Sets up the media player to perform a specified action at the end of
     * every song.
     *
     * @param action
     *            An action wrapped in a Runnable
     */
    public static void setEndOfSongAction(Runnable action) {
        player.setOnEndOfMedia(action);
    }

    @Nullable
    public static Song getNowPlaying() {
        return nowPlaying;
    }

    public static void setVolume(double value) {
        if (player != null) player.setVolume(value);
    }

    public static Playlist getMasterPlaylist() {
        return masterPlaylist;
    }

    // ------------------- File Manipulation ------------------- //

    /**
     * Adds a user-selected directory to the directory collection.
     * 
     * @throws IOException
     *             The directory collection was not properly saved
     */
    public static void addDirectory() throws IOException {
        File directory = chooseDirectory(primaryStage);
        if (directory == null) return;
        directories.add(directory);
        writeFiles(directories);
        refresh();
    }

}

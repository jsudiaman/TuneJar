package viewcontroller;

import com.sun.istack.internal.Nullable;
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.Set;
import java.util.logging.Level;

import static model.DebugUtils.LOGGER;
import static model.DebugUtils.fatalException;
import static model.FileManipulator.*;

public class MainView extends Application {

    // GUI
    private static MediaPlayer player;
    private static Song nowPlaying;

    // Data Structures
    private static Playlist masterPlaylist;
    private static Set<File> directorySet;

    /**
     * Calls Application::launch().
     *
     * @param args The command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Starts the program.
     *
     * @param primaryStage The stage that will hold the interface
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
     * @param primaryStage The stage that will hold the interface
     * @throws IOException Failed to load the FXML, or could not load/save a file.
     */
    private void init(Stage primaryStage) throws IOException {
        // Load the FXML file and display the interface.
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
            directorySet = readDirectories(new File("directories.dat"));
        } catch (FileNotFoundException e) {
            directorySet = initialSetup(primaryStage);
        }

        MainController controller = fxmlLoader.getController();
        controller.setStatus("Loading your songs, please be patient...");

        // Multithreading this task since it takes a while
        Thread initPlaylist = new Thread(() -> {
            // Create and display a playlist containing all songs from each directory.
            refresh();
            controller.loadPlaylist(masterPlaylist);

            // Save the directory set.
            try {
                writeFileSet(directorySet);
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Failed to save the directory set.", e);
            }

            // Finally, load in all playlists from the working directory.
            Platform.runLater(() -> {
                controller.setStatus("Loaded " + masterPlaylist.size() + " songs successfully.");
                Set<Playlist> playlistSet = null;
                try {
                    playlistSet = allPlaylists();
                } catch (IOException | NullPointerException e) {
                    LOGGER.log(Level.SEVERE, "Failed to load playlists from the working directory.", e);
                }
                if (playlistSet != null) {
                    playlistSet.forEach(controller::loadPlaylist);
                }
                controller.focusMasterPlaylist();
                controller.enableTopMenuBar();
            });
        });
        initPlaylist.start();
    }

    /**
     * The master playlist takes in all MP3 files that can be found in available directories.
     */
    public static void refresh()  {
        // Initialize the master playlist.
        masterPlaylist = new Playlist("All Music");

        // Then add all songs found in the directory set to the master playlist.
        LOGGER.log(Level.INFO, "directorySet: " + (directorySet != null ? directorySet.toString() : null));
        try {
            for (File directory : directorySet) {
                LOGGER.log(Level.INFO, "Now adding songs from directory " + directory.toString());
                masterPlaylist.addAll(songSet(directory));
            }
            LOGGER.log(Level.INFO, "Refresh successful!");
        } catch (NullPointerException e) {
            LOGGER.log(Level.SEVERE, "An unusable directory is in the directory set.", e);
            fatalException(e);
        }
    }

    // ------------------- Media Player Controls ------------------- //

    /**
     * Loads a song into the media player, then plays it.
     *
     * @param song The song to play
     */
    public static void playSong(Song song) {
        if (nowPlaying != null) {
            nowPlaying.stop();
        }
        nowPlaying = song;
        LOGGER.log(Level.INFO, "Playing: " + nowPlaying.toString());
        String uriString = new File(song.getAbsoluteFilename()).toURI().toString();
        player = new MediaPlayer(new Media(uriString));
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
     * Sets up the media player to perform a specified action at the end
     * of every song.
     *
     * @param action An action wrapped in a Runnable
     */
    public static void setEndOfSongAction(Runnable action) {
        player.setOnEndOfMedia(action);
    }

    @Nullable
    public static Song getNowPlaying() {
        return nowPlaying;
    }
}

package viewcontroller;

import com.mpatric.mp3agic.Mp3File;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import model.Playlist;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.Set;

import static model.DebugUtils.*;
import static model.FileManipulator.*;

public class MainView extends Application {

    // GUI
    private static MediaPlayer player;
    private static MainController controller;

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
            fatalException(MainView.class, e);
        }
    }

    /**
     * Handles program initialization.
     *
     * @param primaryStage The stage that will hold the interface
     *
     * @throws IOException Failed to load the FXML, or could not load/save a file.
     * @throws NullPointerException An unusable directory is in the directory set
     */
    private void init(Stage primaryStage) throws IOException, NullPointerException {
        // Load the FXML file and display the interface.
        URL location = getClass().getResource("MainController.fxml");
        FXMLLoader fxmlLoader = new FXMLLoader();
        Parent root = fxmlLoader.load(location.openStream());

        primaryStage.setTitle("Java MP3 Player");
        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.show();

        // Load the directories. If none are present, prompt the user for one.
        try {
            directorySet = readDirectories(new File("directories.dat"));
        } catch (FileNotFoundException e) {
            directorySet = initialSetup(primaryStage);
        }

        // Create and display a playlist containing all songs from each directory.
        refresh();
        controller = fxmlLoader.getController();
        controller.loadPlaylist(masterPlaylist);

        // Finally, save the directory set.
        // If the load method above threw NullPointerException, this statement will not be reached.
        writeFileSet("directories.dat", directorySet);
    }

    /**
     * The master playlist takes in all MP3 files that can be found in available directories.
     *
     * @throws NullPointerException An unusable directory is in the directory set
     */
    public static void refresh() throws NullPointerException {
        masterPlaylist = new Playlist("All Music");
        info(MainView.class, "\ndirectorySet: " + (directorySet != null ? directorySet.toString() : null));
        try {
            for (File directory : directorySet) {
                info(MainView.class, "Now adding songs from directory " + directory.toString());
                masterPlaylist.addAll(songList(directory));
            }
        }
        catch (NullPointerException e) {
            fatalException(MainView.class, new NullPointerException("An unusable directory is in the directory set."));
        }
    }

    // ------------------- Media Player Controls ------------------- //
    /**
     * Loads an MP3 file into the media player, then plays it.
     *
     * @param file The MP3 file to play
     */
    public static void playMP3(Mp3File file) {
        String uriString = new File(file.getFilename()).toURI().toString();
        player = new MediaPlayer(new Media(uriString));
        player.play();
    }

    /**
     * Pauses the media player.
     */
    public static void pausePlayback() {
        if (player != null) {
            player.pause();
        }
    }

    /**
     * Stops the media player.
     */
    public static void stopPlayback() {
        if (player != null) {
            player.stop();
        }
    }

    // ------------------- Getters and Setters ------------------- //

    public static Playlist getMasterPlaylist() {
        return masterPlaylist;
    }

}

package viewcontroller;

import com.mpatric.mp3agic.Mp3File;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import model.Playlist;

import java.io.*;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.*;

import static model.FileManipulator.*;

public class MainView extends Application {

    public final static Logger logger = Logger.getLogger(MainView.class.getName()); // Global logger

    private static MediaPlayer player;
    private static Playlist masterPlaylist;
    Set<File> directorySet;

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
        // Begin logging
        try {
            Handler handler = new FileHandler("log.txt");
            handler.setFormatter(new SimpleFormatter());
            logger.addHandler(handler);
            logger.log(Level.INFO, "log.txt initialized successfully.");
        } catch (IOException e) {
            logger.log(Level.WARNING, "Failed to generate log.txt. Logs will be written only to the console.", e);
        }

        // Start the program
        try {
            init(primaryStage);
        } catch (NullPointerException e) {
            logger.log(Level.SEVERE, "NullPointerException thrown to MainView::start()", e);
        } catch (Exception e) {
            handleFatalException(e);
        }
    }

    /**
     * Handles program initialization.
     *
     * @param primaryStage The stage that will hold the interface
     *
     * @throws IOException Failed to load the FXML, or could not load/save a file.
     * @throws NullPointerException Thrown by Playlist::addAll().
     */
    private void init(Stage primaryStage) throws IOException, NullPointerException {
        // Load the FXML file and display the interface.
        Parent root = FXMLLoader.load(getClass().getResource("MainController.fxml"));
        primaryStage.setTitle("Java MP3 Player");
        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.show();

        // Load the directories. If none are present, prompt the user for one.
        try {
            directorySet = readDirectories(new File("directories.dat"));
        } catch (FileNotFoundException e) {
            // Alert the user that no directories were found
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Welcome!");
            alert.setHeaderText(null);
            alert.setContentText("Hi there! It seems like you don't have any directories set up." +
                    "\nThat usually happens when you run this for the first time." +
                    "\nIf that's the case, let's find your MP3s!");
            alert.showAndWait();

            // Begin building up a data structure to store directories
            directorySet = new HashSet<>();
            File chosenDirectory = chooseDirectory(primaryStage);
            if (chosenDirectory == null) {
                logger.log(Level.WARNING, "User pressed 'cancel' when asked to choose a directory.");
            } else {
                directorySet.add(chosenDirectory);
            }
        }

        // Create a playlist containing all songs from each directory in the directory set.
        masterPlaylist = new Playlist();
        for (File directory : directorySet) {
            masterPlaylist.addAll(songList(directory)); // TODO Handle the potential NullPointerException
        }

        // Display that playlist in MainController.
        // TODO Implement a way to do this

        // Finally, save the directory set.
        writeFileSet("directories.dat", directorySet);
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

    // ------------------- Exception Handling ------------------- //

    /**
     * Logs the error and displays a dialog box explaining what happened.
     * Once the dialog box is closed, the program exits with exit code -1.
     *
     * @param e An exception that should end the program
     */
    private void handleFatalException(Exception e) {
        // Log the error.
        logger.log(Level.SEVERE, "Fatal exception thrown to MainView::start()", e);
        Alert alert = new Alert(Alert.AlertType.ERROR);

        // Store the stack trace in a string.
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);

        // Create an alert to let the user know what happened.
        alert.setTitle("Fatal Error!");
        alert.setHeaderText(e.getClass().toString().substring(6) + ": " + e.getMessage());
        alert.setContentText("Please send the log.txt file to our developers for analysis.");

        // Store the stack trace string in a textarea hidden by a "Show/Hide Details" button.
        TextArea textArea = new TextArea(sw.toString());
        textArea.setEditable(false);
        textArea.setWrapText(false);
        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);

        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);
        GridPane gridPane = new GridPane();
        gridPane.setMaxWidth(Double.MAX_VALUE);
        gridPane.add(textArea, 0, 0);

        // Display the alert, then exit the program.
        alert.getDialogPane().setExpandableContent(gridPane);
        alert.showAndWait();
        System.exit(-1);
    }

}

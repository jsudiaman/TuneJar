package viewcontroller;

import com.mpatric.mp3agic.Mp3File;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.*;
import java.util.*;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainView extends Application {

    public final static Logger logger = Logger.getLogger(MainView.class.getName()); // Global logger
    public static MediaPlayer player;

    private Set<File> directorySet;

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
    public void start(Stage primaryStage)  {
        // Begin logging
        try {
            FileHandler fh = new FileHandler("Log.xml");
            logger.addHandler(fh);
        } catch (IOException e) {
            logger.log(Level.WARNING, "Failed to generate log file. Logs will be written only to the console.", e);
        }

        // Start the program
        try {
            init(primaryStage);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Fatal exception at MainView::start()", e);
            System.exit(-1);
        }
    }

    /**
     * Handles program initialization.
     *
     * @param primaryStage The stage that will hold the interface
     * @throws IOException
     */
    private void init(Stage primaryStage) throws IOException {
        // Load the FXML file and display the interface.
        Parent root = FXMLLoader.load(getClass().getResource("MainController.fxml"));
        primaryStage.setTitle("Java MP3 Player");
        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.show();

        // Load the directories. If none are present, prompt the user for one.
        try {
            directorySet = readDirectories(new File("Directories.dat"));
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
            directorySet.add(chooseDirectory(primaryStage));

            // Store the directories in a text file
            writeDirectories("Directories.dat");
        } finally {
            // TODO Create the "master playlist".
        }
    }

    /**
     * Prompts the user for a directory.
     *
     * @param stage The stage that will hold the dialog box
     * @return The directory specified by the user, or null if the user cancels
     */
    static File chooseDirectory(Stage stage) {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Where are your MP3s?");
        return chooser.showDialog(stage);
    }

    /**
     * Loads an MP3 file into the media player.
     *
     * @param file The MP3 file to load
     */
    public static void loadMP3(Mp3File file) {
        String uriString = new File(file.getFilename()).toURI().toString();
        player = new MediaPlayer(new Media(uriString));
    }

    /**
     * Read in a list of directories, line by line, from a specified text file.
     *
     * @param file The file to read directories from
     * @return A set containing all of the specified directories
     * @throws FileNotFoundException
     */
    static Set<File> readDirectories(File file) throws FileNotFoundException {
        Set<File> dirSet = new HashSet<>();
        Scanner fileIn = new Scanner(new FileReader(file));

        // Read in the directories line by line.
        while(fileIn.hasNextLine()) {
            dirSet.add(new File(fileIn.nextLine()));
        }

        fileIn.close();
        return dirSet;
    }

    /**
     * Output the contents of the directory set, line by line.
     *
     * @param fileName Where to write the output. The file will be overwritten if it exists.
     * @throws FileNotFoundException
     */
    void writeDirectories(String fileName) throws FileNotFoundException {
        PrintWriter writer = new PrintWriter(new FileOutputStream(fileName, false));
        for(File f : directorySet) {
            writer.println(f.getAbsoluteFile());
        }
        writer.close();
    }

}

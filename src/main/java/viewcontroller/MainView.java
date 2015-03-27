package viewcontroller;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MainView extends Application {

    private static MediaPlayer player;
    private List<File> directoryList;

    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Starts the program.
     *
     * @param primaryStage The stage that will hold the interface
     * @throws Exception
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Load the FXML file and display the interface.
        Parent root = FXMLLoader.load(getClass().getResource("MainController.fxml"));
        primaryStage.setTitle("Java MP3 Player");
        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.show();

        // Load the directories. If none are present, prompt the user for one.
        // TODO Implement a way to store the directory so that the user doesn't always see this.
        // TODO Ability to store multiple directories would also be a good idea
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Welcome!");
        alert.setHeaderText(null);
        alert.setContentText("Hi there! It seems like you don't have any directories set up." +
                "\nThat usually happens when you run this for the first time." +
                "\nIf that's the case, let's find your MP3s!");
        alert.showAndWait();

        // TODO Allow the user to choose a directory, then store it.

    }

    /**
     * Prompts the user for a directory.
     *
     * @param stage The stage that will hold the dialog box
     * @return The directory specified by the user, or null if the user cancels
     */
    public static File chooseDirectory(Stage stage) {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Where are your MP3s?");
        return chooser.showDialog(stage);
    }

    /**
     * Loads the file into the media player.
     *
     * @param fileName The file to load
     */
    public static void setSong(File fileName) {
        String uriString = fileName.toURI().toString();
        player = new MediaPlayer(new Media(uriString));
    }

    /**
     * Read in a list of directories from a specified text file.
     *
     * @param fileName The file to read directories from
     * @return The list of directories
     */
    public static List directoryList(File fileName) throws FileNotFoundException {
        List<File> dirList = new ArrayList<>();
        Scanner fileIn = new Scanner(new FileReader(fileName));

        return dirList;
    }

}

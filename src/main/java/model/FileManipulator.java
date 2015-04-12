package model;

import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;
import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import javafx.scene.control.Alert;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.*;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

import static model.DebugUtils.LOGGER;

/**
 * Helper class for file manipulation within the GUI.
 */
public final class FileManipulator {

    private static final String DIRECTORY_FILENAME = "directories.dat"; // Where to save the directories.

    private FileManipulator() {
        throw new AssertionError();
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
     * Prompts the user for a directory and initializes a data structure to
     * store directories.
     *
     * @param stage The stage that will hold the dialog box
     * @return A collection that holds one directory chosen by the user, or an empty
     * set if the user cancels
     */
    public static Collection<File> initialSetup(Stage stage) {
        Set<File> set;

        // Alert the user that no directories were found
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Welcome!");
        alert.setHeaderText(null);
        alert.setContentText("Hi there! It seems like you don't have any directories set up."
                + "\nThat usually happens when you run this for the first time."
                + "\nIf that's the case, let's find your MP3s!");
        alert.showAndWait();

        // Begin building up a data structure to store directories
        set = new HashSet<>();
        File chosenDirectory = chooseDirectory(stage);
        if (chosenDirectory == null) {
            LOGGER.log(Level.WARNING, "User pressed 'cancel' when asked to choose a directory.");
        } else {
            set.add(chosenDirectory);
        }

        return set;
    }

    /**
     * Read in a list of directories, line by line, from a text file.
     *
     * @return A collection containing all of the specified directories
     * @throws IOException The file cannot be found or accessed
     */
    public static Collection<File> readDirectories() throws IOException {
        Set<File> dirSet = new HashSet<>();

        // Read in the directories line by line.
        BufferedReader reader = new BufferedReader(new FileReader(DIRECTORY_FILENAME));
        for (String nextLine; (nextLine = reader.readLine()) != null; ) {
            dirSet.add(new File(nextLine));
        }

        // Close the text file.
        reader.close();
        return dirSet;
    }

    /**
     * Output the contents of a collection of files, line by line.
     *
     * @param files A collection of files
     * @throws IOException Unable to write the output to the file
     */
    public static void writeFiles(Collection<File> files) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(DIRECTORY_FILENAME, false));
        for (File f : files) {
            writer.write(f.getAbsoluteFile().toString());
            writer.newLine();
        }
        writer.close();
    }

    /**
     * Takes in a directory and recursively searches for all mp3 files contained
     * within that directory. The files are then constructed as Song objects to
     * be wrapped up in a collection.
     *
     * @param directory A File object that is a directory.
     * @return A collection containing all the Song objects, or null if the File
     * object is not a directory.
     */
    @Nullable
    public static Collection<Song> getSongs(@NotNull File directory) {
        // Initialize the set or return null if necessary.
        if (directory == null || !directory.isDirectory()) return null;
        Set<Song> set = new HashSet<>();

        // Iterate through each file in the directory.
        for (File f : directory.listFiles()) {
            // If a directory was found, add the mp3 files in that directory as well.
            if (f.isDirectory()) {
                set.addAll(getSongs(f));
            } else {
                // Attempt to construct a song object. If successful, add it to the set.
                if (!f.toString().endsWith(".mp3")) continue;
                try {
                    Song song = new Song(new Mp3File(f));
                    set.add(song);
                } catch (UnsupportedTagException | InvalidDataException | IOException e) {
                    LOGGER.log(Level.SEVERE, "Failed to construct a song object from file: " + f.toString(), e);
                }
            }
        }

        return set;
    }

    /**
     * Searches the working directory for .m3u files and creates a playlist
     * out of each one. All of the created playlists are then wrapped into a collection
     * and returned.
     *
     * @return All of the created playlists
     *
     * @throws IOException Unable to access the working directory
     * @throws NullPointerException NullPointerException thrown by dereference of the working directory
     */
    public static Collection<Playlist> getPlaylists() throws IOException, NullPointerException {
        Set<Playlist> set = new HashSet<>();

        // Iterate through each file in the working directory.
        for (File f : new File(".").listFiles()) {
            // Attempt to construct a playlist object. If successful, add it to the set.
            if (f.toString().endsWith(".m3u")) {
                Playlist playlist = new Playlist(f);
                set.add(playlist);
            }
        }

        return set;
    }

}

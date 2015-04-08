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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import static model.DebugUtils.LOGGER;

/**
 * Helper class for file manipulation within the GUI.
 */
public final class FileManipulator {

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
     * Prompts the user for a directory and initializes a data structure
     * to store directories.
     *
     * @param stage The stage that will hold the dialog box
     * @return A set that holds one directory chosen by the user, or an
     * empty set if the user cancels
     */
    public static Set<File> initialSetup(Stage stage) {
        Set<File> set;

        // Alert the user that no directories were found
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Welcome!");
        alert.setHeaderText(null);
        alert.setContentText("Hi there! It seems like you don't have any directories set up." +
                "\nThat usually happens when you run this for the first time." +
                "\nIf that's the case, let's find your MP3s!");
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
     * Read in a list of directories, line by line, from a specified text file.
     *
     * @param file The file to read directories from
     * @return A set containing all of the specified directories
     * @throws IOException The file cannot be found or accessed
     */
    public static Set<File> readDirectories(File file) throws IOException {
        Set<File> dirSet = new HashSet<>();

        // Read in the directories line by line.
        BufferedReader reader = new BufferedReader(new FileReader(file));
        for (String nextLine; (nextLine = reader.readLine()) != null; ) {
            dirSet.add(new File(nextLine));
        }

        // Close the text file.
        reader.close();
        return dirSet;
    }

    /**
     * Output the contents of a file set, line by line.
     *
     * @param fileName Where to write the output. The file will be overwritten if it exists.
     * @param fileSet  A set of files
     * @throws IOException Unable to write the output to the file
     */
    public static void writeFileSet(String fileName, Set<File> fileSet) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, false));
        for (File f : fileSet) {
            writer.write(f.getAbsoluteFile().toString());
            writer.newLine();
        }
        writer.close();
    }

    /**
     * Takes in a directory and recursively searches for all mp3 files contained within that directory.
     * The files are then constructed as Song objects to be wrapped up in an ordered list.
     *
     * @param directory A File object that is a directory.
     * @return A list containing all the Song objects, or null if the File object is not a directory.
     */
    @Nullable
    public static List<Song> songList(@NotNull File directory) {
        // Initialize the list or return null if necessary.
        if (directory == null || !directory.isDirectory()) return null;
        List<Song> list = new ArrayList<>();

        // Iterate through each file in the directory.
        for (File f : directory.listFiles()) {
            // If a directory was found, add the mp3 files in that directory as well.
            if (f.isDirectory()) {
                list.addAll(songList(f));
            } else {
                // Attempt to construct a song object. If successful, add it to the list.
                if (!f.toString().endsWith(".mp3")) continue;
                try {
                    Song song = new Song(new Mp3File(f));
                    list.add(song);
                } catch (UnsupportedTagException | InvalidDataException | IOException e) {
                    LOGGER.log(Level.SEVERE, "Failed to construct a song object from file: " + f.toString(), e);
                }
            }
        }

        return list;
    }

    /**
     * Recursively searches the directory for .m3u files and creates a playlist
     * out of each one. All of the created playlists are then wrapped in a list and
     * returned.
     *
     * @param directory The directory in which playlists should be searched for
     * @return All of the created playlists, or null if <i>directory</i> is not
     * a directory.
     */
    public static List<Playlist> allPlaylists(File directory) {
        // TODO Not yet implemented
        return null;
    }

}

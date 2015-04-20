package com.jonsudiaman.jvmp3.model;

import static com.jonsudiaman.jvmp3.model.DebugUtils.LOGGER;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

import javafx.scene.control.Alert;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;
import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;

/**
 * Helper class for file manipulation within the GUI.
 */
public final class FileManipulator {

    // Where to save the directories.
    private static final String DIRECTORY_FILENAME = "directories.dat";

    private FileManipulator() {
        throw new AssertionError();
    }

    /**
     * Prompts the user for a directory.
     *
     * @param stage
     *            The stage that will hold the dialog box
     * @return The directory specified by the user, or null if the user cancels
     */
    public static File chooseDirectory(Stage stage) {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Where are your MP3s?");
        return chooser.showDialog(stage);
    }

    /**
     * Prompts the user for a directory.
     *
     * @param stage
     *            The stage that will hold the dialog box
     * @return A directory chosen by the user, or null if the user cancels
     */
    @Nullable
    public static File initialDirectory(Stage stage) {
        // Alert the user that no directories were found
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Welcome!");
        alert.setHeaderText(null);
        alert.setContentText("Hi there! It seems like you don't have any directories set up."
                + "\nThat usually happens when you run this for the first time."
                + "\nIf that's the case, let's find your MP3s!");
        alert.showAndWait();

        // Begin building up a data structure to store directories
        File chosenDirectory = chooseDirectory(stage);
        if (chosenDirectory == null) {
            LOGGER.log(Level.WARNING, "User pressed 'cancel' when asked to choose a directory.");
            return null;
        } else {
            return chosenDirectory;
        }
    }

    /**
     * Read in a list of directories, line by line, from a text file.
     *
     * @return A collection containing all of the specified directories
     * @throws IOException
     *             The file cannot be found or accessed
     */
    public static Collection<File> readDirectories() throws IOException {
        Set<File> dirSet = new HashSet<>();

        // Read in the directories line by line.
        BufferedReader reader = new BufferedReader(new FileReader(DIRECTORY_FILENAME));
        for (String nextLine; (nextLine = reader.readLine()) != null;) {
            dirSet.add(new File(nextLine));
        }

        // Close the text file.
        reader.close();
        return dirSet;
    }

    /**
     * Output the contents of a collection of files, line by line.
     *
     * @param files
     *            A collection of files
     * @throws IOException
     *             Unable to write the output to the file
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
     * Takes in a directory and recursively searches for all mp3 files contained within that
     * directory. The files are then constructed as Song objects to be wrapped up in a collection.
     *
     * @param directory
     *            A File object that is a directory.
     * @return A collection containing all the Song objects.
     */
    public static Collection<Song> getSongs(@NotNull File directory) {
        // Initialize the set or return an empty set if necessary.
        Set<Song> set = new HashSet<>();
        if (directory == null || !directory.isDirectory()) {
            LOGGER.log(Level.SEVERE, "Failed to access directory: "
                    + (directory == null ? "null" : directory.toString()) + ", skipping...");
            return set;
        }

        // Iterate through each file in the directory.
        for (File f : directory.listFiles()) {
            try {
                // If a directory was found, add the mp3 files in that directory as well.
                if (f.isDirectory()) {
                    set.addAll(getSongs(f));
                } else {
                    // Attempt to construct a song object. If successful, add it to the set.
                    if (!f.toString().endsWith(".mp3")) {
                        continue;
                    }
                    Song song = new Song(new Mp3File(f));
                    set.add(song);
                }
            } catch (UnsupportedTagException | InvalidDataException | IOException
                    | NullPointerException e) {
                LOGGER.log(Level.SEVERE,
                        "Failed to construct a song object from file: " + f.toString(), e);
            }
        }

        return set;
    }

    /**
     * Searches the working directory for .m3u files and creates a playlist out of each one. All of
     * the created playlists are then wrapped into a collection and returned.
     *
     * @return All of the created playlists
     *
     * @throws IOException
     *             Unable to access the working directory
     */
    public static Collection<Playlist> getPlaylists() throws IOException {
        // Initialization
        Set<Playlist> set = new HashSet<>();
        File[] fileList = new File(".").listFiles();
        if (fileList == null) {
            LOGGER.log(Level.SEVERE, "Unable to access the working directory.");
            return set;
        }

        // Iterate through each file in the working directory.
        for (File f : fileList) {
            if (f.toString().endsWith(".m3u")) {
                Playlist playlist = new Playlist(f);
                set.add(playlist);
            }
        }

        return set;
    }

}

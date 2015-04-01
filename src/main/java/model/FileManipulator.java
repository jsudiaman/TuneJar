package model;

import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import viewcontroller.MainView;

import java.io.*;
import java.util.*;
import java.util.logging.Level;

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
     * Read in a list of directories, line by line, from a specified text file.
     *
     * @param file The file to read directories from
     * @return A set containing all of the specified directories
     * @throws FileNotFoundException
     */
    public static Set<File> readDirectories(File file) throws FileNotFoundException {
        Set<File> dirSet = new HashSet<>();
        Scanner fileIn = new Scanner(new FileReader(file)); // TODO Improve efficiency by using BufferedReader instead

        // Read in the directories line by line.
        while (fileIn.hasNextLine()) {
            dirSet.add(new File(fileIn.nextLine()));
        }

        fileIn.close();
        return dirSet;
    }

    /**
     * Output the contents of a file set, line by line.
     *
     * @param fileName Where to write the output. The file will be overwritten if it exists.
     * @param fileSet  A set of files
     * @throws FileNotFoundException
     */
    public static void writeFileSet(String fileName, Set<File> fileSet) throws IOException {
        FileOutputStream fileOut = new FileOutputStream(fileName, false);
        PrintWriter writer = new PrintWriter(fileOut); // TODO Improve efficiency by using BufferedWriter instead
        for (File f : fileSet) {
            writer.println(f.getAbsoluteFile());
        }
        writer.close();
        fileOut.close();
    }

    /**
     * Takes in a directory and recursively searches for all mp3 files contained within that directory.
     * The files are then constructed as Song objects to be wrapped up in an ordered list.
     *
     * @param directory A File object that is a directory.
     * @return A list containing all the Song objects, or null if the File object is not a directory.
     */
    public static List<Song> songList(File directory) throws NullPointerException {
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
                    MainView.logger.log(Level.SEVERE, "Failed to construct a song object from file: " +
                        f.toString(), e);
                }
            }
        }

        return list;
    }

}
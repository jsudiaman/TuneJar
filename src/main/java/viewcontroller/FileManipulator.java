package viewcontroller;

import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.*;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

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
    static File chooseDirectory(Stage stage) {
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
     * Output the contents of a file set, line by line.
     *
     * @param fileName Where to write the output. The file will be overwritten if it exists.
     * @param fileSet A set of files
     * @throws FileNotFoundException
     */
    static void writeFileSet(String fileName, Set<File> fileSet) throws FileNotFoundException {
        PrintWriter writer = new PrintWriter(new FileOutputStream(fileName, false));
        for(File f : fileSet) {
            writer.println(f.getAbsoluteFile());
        }
        writer.close();
    }

}

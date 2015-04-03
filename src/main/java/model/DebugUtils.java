package model;

import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import viewcontroller.MainView;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.*;

/**
 * Class for logging information to the console and log.txt.
 * Useful for debugging and error handling.
 */
public final class DebugUtils {

    private final static Logger LOGGER = initialize();

    private DebugUtils() {
        throw new AssertionError();
    }

    private static Logger initialize() {
        Logger log = Logger.getLogger(MainView.class.getName());
        try {
            Handler handler = new FileHandler("log.txt");
            handler.setFormatter(new SimpleFormatter());
            log.addHandler(handler);
            log.log(Level.INFO, "log.txt initialized successfully.");
        } catch (IOException e) {
            log.log(Level.WARNING, "Failed to generate log.txt. Logs will be written only to the console.", e);
        }
        return log;
    }

    public static void info(Class c, String message) {
        LOGGER.log(Level.INFO, c.toString() + "  " + message);
    }

    public static void warning(Class c, String message) {
        LOGGER.log(Level.WARNING, c.toString() + "  " + message);
    }

    public static void error(Class c, String message) {
        LOGGER.log(Level.SEVERE, c.toString() + "  " + message);
    }

    public static void exception(Class c, String message, Exception e) {
        LOGGER.log(Level.SEVERE, message, c.toString() + "  " + e);
    }

    /**
     * Logs the exception and displays a dialog box explaining what happened.
     * Once the dialog box is closed, the program exits with exit code -1.
     *
     * @param e An exception that should end the program
     */
    public static void fatalException(Class c, Exception e) {
        // Log the exception.
        LOGGER.log(Level.SEVERE, "Fatal exception thrown by " + c.toString(), e);
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

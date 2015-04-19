package viewcontroller;

import static model.DebugUtils.LOGGER;

import java.io.IOException;
import java.util.Optional;
import java.util.logging.Level;

import javafx.application.Platform;
import javafx.scene.control.*;
import model.Playlist;

/**
 * Helper class for handling the File menu.
 */
final class FileMenu {

    private MainController controller;

    FileMenu(MainController controller) {
        this.controller = controller;
    }

    /**
     * Creates a new playlist.
     * 
     * @return The created playlist.
     */
    Playlist createPlaylist() {
        // Prompt the user for a playlist name.
        TextInputDialog dialog = new TextInputDialog("Untitled Playlist");
        dialog.setTitle("New Playlist");
        dialog.setHeaderText("Create a new playlist");
        dialog.setContentText("Playlist name:");

        Optional<String> playlistName = dialog.showAndWait();
        if (playlistName.isPresent()) {
            String pName = playlistName.get();

            // Playlist creation fails if a playlist with the specified name already exists.
            for (Playlist p : controller.playlistList) {
                if (p.getName().equalsIgnoreCase(pName)) {
                    Alert conflictAlert = new Alert(Alert.AlertType.WARNING);
                    conflictAlert.setTitle("Playlist Conflict");
                    conflictAlert.setHeaderText("A playlist named " + pName + " already exists.");
                    conflictAlert.setContentText("Please rename/delete the existing playlist, or "
                            + "choose another name.");
                    conflictAlert.showAndWait();
                    return null;
                }
            }

            Playlist p = new Playlist(pName);
            try {
                p.save();
                controller.playlistMenu.loadPlaylist(p);
                return p;
            } catch (IOException e) {
                // Playlist creation fails if it cannot be successfully saved.
                Alert failAlert = new Alert(Alert.AlertType.ERROR);
                failAlert.setTitle("Playlist Write Error");
                failAlert.setHeaderText("Failed to create playlist: " + pName);
                failAlert.setContentText("The playlist failed to save. Make sure the name "
                        + "does not contain any illegal characters.");
                failAlert.showAndWait();
                LOGGER.log(Level.SEVERE, "Failed to save playlist: " + pName + ".m3u", e);
            }
        }
        return null;
    }

    /**
     * Asks the user if it is okay to end the program. If so, end the program.
     */
    void quit() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Exit JVMP3");
        alert.setHeaderText("Confirm Exit");
        alert.setContentText("Are you sure you would like to exit?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            Platform.exit();
        }
    }

    void addDirectory() {
        MainView.addDirectory();
        Platform.runLater(() -> {
            controller.playlistList.set(0, MainView.getMasterPlaylist());
            controller.refreshTables();
            controller.focus(controller.playlistTable, 0);
        });
    }

    void removeDirectory() {
        if (MainView.removeDirectory()) {
            MainView.refresh();
            Platform.runLater(() -> {
                controller.playlistList.set(0, MainView.getMasterPlaylist());
                controller.refreshTables();
                if (MainView.getNowPlaying() != null
                        && !MainView.getMasterPlaylist().contains(MainView.getNowPlaying())) {
                    MainView.stopPlayback();
                }
            });
        }
    }

}

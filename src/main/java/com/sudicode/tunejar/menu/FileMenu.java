package com.sudicode.tunejar.menu;

import com.sudicode.tunejar.config.Defaults;
import com.sudicode.tunejar.player.PlayerController;
import com.sudicode.tunejar.song.Playlist;

import java.util.Optional;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;

/**
 * Helper class for handling the File menu.
 */
public class FileMenu extends PlayerMenu {

    public FileMenu(PlayerController controller) {
        super(controller);
    }

    /**
     * Creates a new playlist.
     *
     * @return The created playlist.
     */
    public Playlist createPlaylist() {
        // Prompt the user for a playlist name.
        TextInputDialog dialog = new TextInputDialog(Defaults.PLAYLIST_NAME);
        dialog.setTitle("New Playlist");
        dialog.setHeaderText("Create a new playlist");
        dialog.setContentText("Playlist name:");

        Optional<String> playlistName = dialog.showAndWait();
        if (playlistName.isPresent()) {
            String pName = playlistName.get();

            // Playlist creation fails if a playlist with the specified name
            // already exists.
            for (Playlist p : controller.getPlaylistList()) {
                if (p.getName().equalsIgnoreCase(pName)) {
                    Alert conflictAlert = new Alert(Alert.AlertType.WARNING);
                    conflictAlert.setTitle("Playlist Conflict");
                    conflictAlert.setHeaderText("A playlist named " + pName + " already exists.");
                    conflictAlert.setContentText("Please rename/delete the existing playlist, or choose another name.");
                    conflictAlert.showAndWait();
                    return null;
                }
            }

            Playlist p = new Playlist(pName);
            p.save(controller.getPlayer().getOptions());
            controller.getPlaylistMenu().loadPlaylist(p);
            return p;
        }
        return null;
    }

    /**
     * Asks the user if it is okay to end the program. If so, end the program.
     */
    public void quit() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Exit TuneJar");
        alert.setHeaderText("Confirm Exit");
        alert.setContentText("Are you sure you would like to exit?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            Platform.exit();
        }
    }

    public void addDirectory() {
        controller.getPlayer().addDirectory();
        controller.getPlaylistList().set(0, controller.getPlayer().getMasterPlaylist());
        controller.refreshTables();
        controller.focus(controller.getPlaylistTable(), 0);
    }

    public void removeDirectory() {
        if (controller.getPlayer().removeDirectory()) {
            controller.getPlayer().refresh();
            controller.getPlayer().stopSong();
        }
    }

}

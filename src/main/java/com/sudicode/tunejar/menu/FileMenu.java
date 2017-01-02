package com.sudicode.tunejar.menu;

import com.sudicode.tunejar.config.Defaults;
import com.sudicode.tunejar.player.PlayerController;
import com.sudicode.tunejar.song.Playlist;
import com.sudicode.tunejar.song.Song;
import com.sudicode.tunejar.song.SongFactory;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;
import javafx.stage.FileChooser;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Helper class for handling the File menu.
 */
public class FileMenu extends PlayerMenu {

    private static final Logger logger = LoggerFactory.getLogger(FileMenu.class);

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
        if (result.orElse(ButtonType.CANCEL) == ButtonType.OK) {
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

    public void restart() {
        controller.getPlayer().restart();
    }

    /**
     * Imports playlist.
     */
    public void importPlaylist() {
        // Choose file to import
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select M3U file to import");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Playlist File (*.m3u)", "*.m3u"));
        File m3uFile = fileChooser.showOpenDialog(controller.getPlayer().getScene().getWindow());
        if (m3uFile == null) {
            return;
        }
        String baseName = FilenameUtils.getBaseName(m3uFile.getName());

        // Check if playlist exists
        for (Playlist p : controller.getPlaylistList()) {
            if (p.getName().equalsIgnoreCase(baseName)) {
                Alert conflictAlert = new Alert(Alert.AlertType.WARNING);
                conflictAlert.setTitle("Playlist Conflict");
                conflictAlert.setHeaderText("A playlist named " + baseName + " already exists.");
                conflictAlert.setContentText("Please rename/delete the existing playlist, or rename the M3U file.");
                conflictAlert.showAndWait();
                return;
            }
        }

        // Create playlist in memory
        List<Song> songs = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(m3uFile))) {
            for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                try {
                    songs.add(SongFactory.create(new File(line)));
                } catch (IllegalArgumentException e) {
                    logger.error("Could not add file: " + line, e);
                }
            }
        } catch (IOException e) {
            String err = String.format("Could not import playlist: %s", m3uFile.getAbsolutePath());
            logger.error(err, e);

            // Show alert
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText(err);
            alert.showAndWait();

            // Return without adding playlist
            return;
        }

        // Add playlist
        Playlist pl = new Playlist(baseName);
        pl.addAll(songs);
        try {
            controller.getPlaylistMenu().loadPlaylist(pl);
            pl.save(controller.getPlayer().getOptions());
        } finally {
            controller.refreshTables();
        }
    }
}

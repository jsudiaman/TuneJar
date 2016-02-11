/*
 * TuneJar <http://sudicode.com/tunejar/>
 * Copyright (C) 2016 Jonathan Sudiaman
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
*/

package com.sudicode.tunejar.menu;

import com.sudicode.tunejar.config.Defaults;
import com.sudicode.tunejar.player.PlayerController;
import com.sudicode.tunejar.song.Playlist;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Optional;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;

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
            try {
                p.save();
                controller.getPlaylistMenu().loadPlaylist(p);
                return p;
            } catch (IOException e) {
                // Playlist creation fails if it cannot be successfully saved.
                Alert failAlert = new Alert(Alert.AlertType.ERROR);
                failAlert.setTitle("Playlist Write Error");
                failAlert.setHeaderText("Failed to create playlist: " + pName);
                failAlert.setContentText("The playlist failed to save. Make sure the name "
                        + "does not contain any illegal characters.");
                failAlert.showAndWait();
                logger.error("Failed to save playlist: " + pName + ".m3u", e);
            }
        }
        return null;
    }

    /**
     * Asks the user if it is okay to end the program. If so, end the program.
     */
    public void quit() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Exit JVMP3");
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
        }
    }

}

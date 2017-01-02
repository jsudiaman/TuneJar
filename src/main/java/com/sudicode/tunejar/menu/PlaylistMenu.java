package com.sudicode.tunejar.menu;

import com.sudicode.tunejar.config.Options;
import com.sudicode.tunejar.player.PlayerController;
import com.sudicode.tunejar.song.Playlist;
import com.sudicode.tunejar.song.Song;
import javafx.collections.FXCollections;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextInputDialog;
import javafx.stage.FileChooser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

/**
 * Helper class for handling the Playlist menu.
 */
public class PlaylistMenu extends PlayerMenu {

    private static final Logger logger = LoggerFactory.getLogger(PlaylistMenu.class);

    public PlaylistMenu(PlayerController controller) {
        super(controller);
    }

    /**
     * Adds a playlist to the playlist table, then loads it into the song table.
     *
     * @param p A playlist
     */
    public void loadPlaylist(Playlist p) {
        // Cache sort order
        List<TableColumn<Song, ?>> sortOrder = controller.getSortOrder();

        controller.getPlaylistList().add(p);
        controller.getPlaylistTable().setItems(controller.getPlaylistList());
        controller.focus(controller.getPlaylistTable(), controller.getPlaylistList().size() - 1);

        controller.setSongList(FXCollections.observableArrayList(p));
        controller.getSongTable().setItems(controller.getSongList());
        logger.info("Loaded playlist: " + p.getName());

        // Restore sort order
        controller.setSortOrder(sortOrder);

        if ("All Music".equals(p.getName())) {
            return;
        }
        // Enable the user to add songs to the playlist.
        MenuItem m = new MenuItem(p.getName());
        controller.getAddToPlaylist().getItems().add(m);
        m.setOnAction(event -> {
            List<Song> songsToAdd = new ArrayList<>();
            songsToAdd.addAll(controller.getSongTable().getSelectionModel().getSelectedItems());

            if (songsToAdd.isEmpty()) {
                controller.getStatus().setText("No song was selected.");
                return;
            }
            p.addAll(songsToAdd);
            try {
                p.save(controller.getPlayer().getOptions());
            } finally {
                controller.refreshTables();
                event.consume();
            }
        });
    }

    /**
     * Renames the current playlist.
     */
    public void renamePlaylist() {
        Playlist pl = controller.getPlaylistTable().getSelectionModel().getSelectedItem();
        String oldName = pl.getName();

        // Prompt the user for a playlist name.
        TextInputDialog dialog = new TextInputDialog(oldName);
        dialog.setTitle("Rename");
        dialog.setHeaderText("Please enter a new name for playlist \"" + pl.getName() + "\".");
        dialog.setContentText("New name:");
        Optional<String> playlistName = dialog.showAndWait();
        if (!playlistName.isPresent()) {
            return;
        }

        // Make sure that the user has picked a unique playlist name.
        for (Playlist p : controller.getPlaylistList()) {
            if (p.getName().equalsIgnoreCase(playlistName.get())) {
                Alert conflictAlert = new Alert(Alert.AlertType.WARNING);
                conflictAlert.setTitle("Playlist Conflict");
                conflictAlert.setHeaderText("A playlist named " + playlistName.get() + " already exists.");
                conflictAlert.setContentText("Please rename/delete the existing playlist, or choose another name.");
                conflictAlert.showAndWait();
                return;
            }
        }

        try {
            // Rename the playlist and save changes.
            Options options = controller.getPlayer().getOptions();
            pl.setName(playlistName.get());
            pl.save(options);
            LinkedHashMap<String, String> playlists = options.getPlaylists();
            playlists.remove(oldName);
            options.setPlaylists(playlists);
            controller.refreshTables();

            // Also, rename the playlist in the "Song -> Add to...<PLAYLIST>"
            // menu.
            for (MenuItem item : controller.getAddToPlaylist().getItems()) {
                if (item.getText() == null) {
                    continue;
                }
                if (item.getText().equals(oldName)) {
                    item.setText(playlistName.get());
                }
            }

        } catch (Exception e) {
            controller.getStatus().setText("Rename failed.");
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * Asks the user if it is okay to delete the current playlist. If it is
     * okay, deletes the current playlist.
     */
    public void deletePlaylist() {
        Playlist pl = controller.getPlaylistTable().getSelectionModel().getSelectedItem();
        String name = pl.getName();

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete");
        alert.setHeaderText("Confirm Deletion");
        alert.setContentText("Are you sure you would like to delete playlist \"" + pl.getName() + "\"?");
        Optional<ButtonType> result = alert.showAndWait();

        if (result.orElse(ButtonType.CANCEL) != ButtonType.OK) {
            return;
        }
        try {
            Options options = controller.getPlayer().getOptions();
            LinkedHashMap<String, String> playlists = options.getPlaylists();
            playlists.remove(pl.getName());
            options.setPlaylists(playlists);
            controller.getPlaylistList().remove(pl);

            // Remove the playlist from the "Song -> Add To..." menu.
            List<MenuItem> list = controller.getAddToPlaylist().getItems();
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getText() == null) {
                    continue;
                }
                if (list.get(i).getText().equals(name)) {
                    controller.getAddToPlaylist().getItems().remove(i);
                    break;
                }
            }

            controller.refreshTables();
        } catch (Exception e) {
            controller.getStatus().setText("Deletion failed.");
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * Exports the current playlist.
     */
    public void exportPlaylist() {
        // Choose file to export to
        Playlist playlist = controller.getPlaylistTable().getSelectionModel().getSelectedItem();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export " + playlist.getName() + " as M3U file");
        fileChooser.setInitialFileName(playlist.getName());
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Playlist File (*.m3u)", "*.m3u"));
        File m3uFile = fileChooser.showSaveDialog(controller.getPlayer().getScene().getWindow());

        // Export playlist
        if (m3uFile != null) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(m3uFile))) {
                for (Song song : playlist) {
                    writer.write(song.getAbsoluteFilename());
                    writer.newLine();
                }
            } catch (IOException e) {
                String err = String.format("Could not export playlist: %s to file: %s", playlist.getName(), m3uFile.getAbsolutePath());
                logger.error(err, e);

                // Show alert
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText(err);
                alert.showAndWait();
            }
        }
    }
}

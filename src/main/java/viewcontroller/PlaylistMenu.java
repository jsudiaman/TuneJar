package viewcontroller;

import static model.DebugUtils.LOGGER;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;

import javafx.collections.FXCollections;
import javafx.scene.control.*;
import model.Playlist;
import model.Song;

import com.sun.istack.internal.NotNull;

/**
 * Helper class for handling the Playlist menu.
 */
final class PlaylistMenu {

    private MainController controller;

    PlaylistMenu(MainController controller) {
        this.controller = controller;
    }

    /**
     * Adds a playlist to the playlist table, then loads it into the song table.
     *
     * @param p
     *            A playlist
     */
    void loadPlaylist(@NotNull Playlist p) {
        controller.playlistList.add(p);
        controller.playlistTable.setItems(controller.playlistList);
        controller.focus(controller.playlistTable, controller.playlistList.size() - 1);

        controller.songList = FXCollections.observableArrayList(p);
        controller.songTable.setItems(controller.songList);
        LOGGER.log(Level.INFO, "Loaded playlist: " + p.getName());

        // Enable the user to add songs to the playlist (unless the playlist is MainView::masterPlaylist).
        if (p.getName().equals("All Music")) return;
        MenuItem m = new MenuItem(p.getName());
        controller.addToPlaylist.getItems().add(m);
        m.setOnAction(event -> {
            List<Song> songsToAdd = new ArrayList<Song>();
            songsToAdd.addAll(controller.songTable.getSelectionModel().getSelectedItems());
            
            if(songsToAdd.size() == 0) {
                controller.status.setText("No song selected.");
                return;
            }
            p.addAll(songsToAdd);
            try {
                p.save();
            } catch (IOException e) {
                controller.status.setText("Playlist \"" + p.getName() + "\" save unsuccessful.");
                LOGGER.log(Level.SEVERE, "Failed to save the playlist.", e);
            } finally {
                controller.refreshTables();
                event.consume();
            }
        });
    }
    
    /**
     * Shuffles the song table. If a song is currently playing, it will be moved
     * to the top of the table and playback will continue.
     */
    void shuffle() {
        if (controller.songList.isEmpty()) {
            controller.status.setText("No songs to shuffle.");
            return;
        }

        Collections.shuffle(controller.songList);
        if (MainView.getNowPlaying() != null && controller.songList.indexOf(MainView.getNowPlaying()) >= 0) {
            Collections.swap(controller.songList, 0, controller.songList.indexOf(MainView.getNowPlaying()));
        } else {
            controller.playbackMenu.play(0);
        }
        controller.focus(controller.songTable, 0);
    }
    
    /**
     * Renames the current playlist.
     */
    void renamePlaylist() {
        Playlist pl = controller.playlistTable.getSelectionModel().getSelectedItem();
        String oldName = pl.getName();
        File oldFile = new File(oldName + ".m3u");

        // Prompt the user for a playlist name.
        TextInputDialog dialog = new TextInputDialog(oldName);
        dialog.setTitle("Rename");
        dialog.setHeaderText("Please enter a new name for playlist \"" + pl.getName() + "\".");
        dialog.setContentText("New name:");
        Optional<String> playlistName = dialog.showAndWait();
        if (!playlistName.isPresent()) return;

        // Make sure that the user has picked a unique playlist name.
        for (Playlist p : controller.playlistList) {
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
            // Rename the playlist and attempt to save changes.
            pl.setName(playlistName.get());
            pl.save();
            if (!oldFile.delete()) {
                controller.status.setText("Could not delete the old file.");
            }
            controller.refreshTables();

            // Also, rename the playlist in the "Song -> Add to...<PLAYLIST>" menu.
            for (MenuItem item : controller.addToPlaylist.getItems()) {
                if (item.getText() == null) continue;
                if (item.getText().equals(oldName)) {
                    item.setText(playlistName.get());
                }
            }

        } catch (IOException e) {
            controller.status.setText("Rename failed. See log.txt for details.");
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    /**
     * Asks the user if it is okay to delete the current playlist. If it is
     * okay, deletes the current playlist.
     */
    void deletePlaylist() {
        Playlist pl = controller.playlistTable.getSelectionModel().getSelectedItem();
        String name = pl.getName();

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete");
        alert.setHeaderText("Confirm Deletion");
        alert.setContentText("Are you sure you would like to delete playlist \"" + pl.getName() + "\"?");
        Optional<ButtonType> result = alert.showAndWait();

        if (result.get() != ButtonType.OK) return;
        if (new File(pl.getName() + ".m3u").delete()) {
            controller.playlistList.remove(pl);
            
            // Remove the playlist from the "Song -> Add To..." menu.
            List<MenuItem> list = controller.addToPlaylist.getItems();
            for(int i = 0; i < list.size(); i++) {
                if(list.get(i).getText() == null) continue;
                if(list.get(i).getText().equals(name)) {
                    controller.addToPlaylist.getItems().remove(i);
                    break;
                }
            }
            
            controller.refreshTables();
        } else {
            controller.status.setText("Deletion failed.");
        }
    }

}

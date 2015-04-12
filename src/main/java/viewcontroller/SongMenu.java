package viewcontroller;

import static model.DebugUtils.LOGGER;

import java.io.IOException;
import java.util.*;
import java.util.logging.Level;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import model.Playlist;
import model.Song;

/**
 * Helper class for handling the Song menu.
 */
public class SongMenu {

    private MainController controller;
    
    SongMenu(MainController controller) {
        this.controller = controller;
    }
    
    /**
     * Creates a user dialog that allows modification of the selected song's ID3
     * tags.
     */
     void editSong() {
        Song songToEdit = controller.songTable.getSelectionModel().getSelectedItem();
        if (songToEdit == null) {
            controller.status.setText("No song selected.");
            return;
        }

        // Create the editor dialog.
        Dialog<List<String>> editor = new Dialog<>();
        editor.setTitle("Song Editor");
        editor.setHeaderText("Editing " + songToEdit.toString());

        // Set the button types.
        ButtonType saveButton = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        editor.getDialogPane().getButtonTypes().addAll(saveButton, ButtonType.CANCEL);

        // Create the labels and fields.
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField title = new TextField();
        title.setPromptText("Title");
        title.setText(songToEdit.getTitle());

        TextField artist = new TextField();
        artist.setPromptText("Artist");
        artist.setText(songToEdit.getArtist());

        TextField album = new TextField();
        album.setPromptText("Album");
        album.setText(songToEdit.getAlbum());

        grid.add(new Label("Title:"), 0, 0);
        grid.add(title, 1, 0);
        grid.add(new Label("Artist:"), 0, 1);
        grid.add(artist, 1, 1);
        grid.add(new Label("Album:"), 0, 2);
        grid.add(album, 1, 2);

        editor.getDialogPane().setContent(grid);

        // Convert the result to an ArrayList of type String.
        editor.setResultConverter(param -> {
            if (param == saveButton) {
                List<String> list = new ArrayList<>();
                list.add(title.getText());
                list.add(artist.getText());
                list.add(album.getText());
                return list;
            }
            return null;
        });

        Optional<List<String>> newParams = editor.showAndWait();
        if (newParams.isPresent()) {
            songToEdit.setTag(newParams.get().get(0), newParams.get().get(1), newParams.get().get(2));
            controller.refreshTables();
            controller.status.setText("Edit successful.");
        }
    }
     
     /**
      * Creates a new playlist and adds the selected song to it.
      */
     void toNewPlaylist() {
         Song songToAdd = controller.songTable.getSelectionModel().getSelectedItem();
         if (songToAdd == null) {
             controller.status.setText("No song was selected.");
             return;
         }
         if (controller.fileMenu.createPlaylist()) {
             controller.playlistList.get(controller.playlistList.size() - 1).add(songToAdd);
         }
     }
     
     /**
      * Removes the selected song from the current playlist.
      */
     void removeSong() {
         // Find the index of the song to remove.
         int songIndex = controller.songTable.getSelectionModel().getSelectedIndex();
         if (songIndex < 0 || songIndex > controller.songList.size()) {
             controller.status.setText("No song selected.");
             return;
         }

         // Remove it, then save changes to the playlist.
         Playlist pl = controller.playlistTable.getSelectionModel().getSelectedItem();
         pl.remove(songIndex);
         controller.refreshTables();
         try {
             pl.save();
         } catch (IOException e) {
             LOGGER.log(Level.SEVERE, e.getMessage(), e);
         }
     }

    
}

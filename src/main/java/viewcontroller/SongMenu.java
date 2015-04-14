package viewcontroller;

import static model.DebugUtils.LOGGER;

import java.io.IOException;
import java.util.*;
import java.util.logging.Level;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import model.Playlist;
import model.Song;

/**
 * Helper class for handling the Song menu.
 */
final class SongMenu {

    private MainController controller;

    SongMenu(MainController controller) {
        this.controller = controller;
    }

    /**
     * Creates a user dialog that allows modification of the selected song's ID3
     * tags.
     */
    void editSong() {
        ObservableList<Song> songsToEdit = controller.songTable.getSelectionModel().getSelectedItems();
        
        if (songsToEdit.size() == 0) {
            controller.status.setText("No song selected.");
            return;
        }
        
        if(songsToEdit.size() > 1) {
            controller.status.setText("You can only edit one song at a time.");
            return;
        }
        
        Song songToEdit = songsToEdit.get(0);
        
        if (!songToEdit.canSave()) {
            controller.status.setText("The file is locked. See log.txt for details.");
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
     * Creates a new playlist and adds the selected songs to it.
     */
    void toNewPlaylist() {
        List<Song> songs = new ArrayList<Song>();
        songs.addAll(controller.songTable.getSelectionModel().getSelectedItems());
        
        if (songs.size() == 0) {
            controller.status.setText("No song was selected.");
            return;
        }

        Playlist pl;
        if ((pl = controller.fileMenu.createPlaylist()) != null) {
            pl.addAll(songs);
            try {
                pl.save();
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, e.getMessage(), e);
            }
        }
    }

    /**
     * Removes the selected songs from the current playlist.
     */
    void removeSong() {
        // Find the songs to remove.
        List<Song> songs = new ArrayList<>();
        songs.addAll(controller.songTable.getSelectionModel().getSelectedItems());
        
        if (songs.size() == 0) {
            controller.status.setText("No song selected.");
            return;
        }

        // Remove them, then save changes to the playlist.
        Playlist pl = controller.playlistTable.getSelectionModel().getSelectedItem();
        pl.removeAll(songs);
        controller.refreshTables();
        try {
            pl.save();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    void search() {
        // Create the dialog box.
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Search");
        dialog.setHeaderText("What are you looking for?");
        dialog.setContentText("Enter search term:");
        Optional<String> keyword = dialog.showAndWait();
        
        // Perform the search.
        if(keyword.isPresent() && keyword.get().trim().length() > 0) {
            int count = search(keyword.get().trim());
            if(count == 0) {
                controller.status.setText("No matches found.");
            } else {
                controller.status.setText("Found " + count + " matching songs.");
            }
        }
    }

    /**
     * Arranges the playlist such that songs matching the keyword
     * have priority.
     * 
     * @param keyword
     * @return The amount of songs that match
     */
    int search(String keyword) {
        keyword = keyword.toLowerCase();

        // Assign priorities to the songs, depending on relevance.
        int count = 0;
        Map<Song, Integer> priorityMap = new HashMap<>();
        for (Song s : controller.songList) {
            if (s.getTitle().toLowerCase().contains(keyword)) {
                priorityMap.put(s, 1);
                count++;
            } else if (s.getArtist().toLowerCase().contains(keyword)) {
                priorityMap.put(s, 2);
                count++;
            } else if (s.getAlbum().toLowerCase().contains(keyword)) {
                priorityMap.put(s, 3);
                count++;
            } else {
                priorityMap.put(s, 4);
            }
        }

        // Sort the song by relevance first, then by the song table's comparator.
        Collections.sort(controller.songList, (o1, o2) -> {
            int result = priorityMap.get(o1) - priorityMap.get(o2);
            if (result == 0) {
                if (controller.songTable.getComparator() == null) return 0;
                else return controller.songTable.getComparator().compare(o1, o2);
            } else {
                return result;
            }
        });
        
        // Select all relevant songs. 
        controller.songTable.scrollTo(0);
        controller.songTable.getSelectionModel().clearSelection();
        for (int i = 0; i < count; i++) {
            controller.songTable.getSelectionModel().select(i);
        }
        return count;
    }

}

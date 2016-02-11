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

import com.sudicode.tunejar.player.PlayerController;
import com.sudicode.tunejar.song.Playlist;
import com.sudicode.tunejar.song.Song;
import com.sudicode.tunejar.song.WavSong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.GridPane;

/**
 * Helper class for handling the Song menu.
 */
public class SongMenu extends PlayerMenu {

    private static final Logger logger = LoggerFactory.getLogger(SongMenu.class);

    public SongMenu(PlayerController controller) {
        super(controller);
    }

    /**
     * Creates a user dialog that allows modification of the selected song.
     */
    public void editSong() {
        ObservableList<Song> songsToEdit = controller.getSongTable().getSelectionModel().getSelectedItems();

        if (songsToEdit.isEmpty()) {
            controller.getStatus().setText("No song selected.");
            return;
        }

        if (songsToEdit.size() > 1) {
            controller.getStatus().setText("You can only edit one song at a time.");
            return;
        }

        Song songToEdit = songsToEdit.get(0);

        if (songToEdit instanceof WavSong) {
            controller.getStatus().setText("The file does not support editing.");
            return;
        } else if (!songToEdit.canEdit()) {
            controller.getStatus().setText("The file is locked.");
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
        newParams.ifPresent(list -> {
            try {
                songToEdit.setTitle(list.get(0));
                songToEdit.setArtist(list.get(1));
                songToEdit.setAlbum(list.get(2));
                controller.refreshTables();
                controller.getStatus().setText("Edit successful.");
            } catch (Exception e) {
                logger.error("Edit unsuccessful.", e);
                controller.refreshTables();
                controller.getStatus().setText("Edit unsuccessful.");
            }
        });
    }

    /**
     * Creates a new playlist and adds the selected songs to it.
     */
    public void toNewPlaylist() {
        List<Song> songs = new ArrayList<>(controller.getSongTable().getSelectionModel().getSelectedItems());

        if (songs.isEmpty()) {
            controller.getStatus().setText("No song was selected.");
            return;
        }

        Playlist pl;
        if ((pl = controller.getFileMenu().createPlaylist()) != null) {
            pl.addAll(songs);
            try {
                pl.save();
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            } finally {
                controller.refreshTables();
            }
        }
    }

    /**
     * Removes the selected songs from the current playlist.
     */
    public void removeSong() {
        // Find the songs to remove.
        List<Song> songs = new ArrayList<>(controller.getSongTable().getSelectionModel().getSelectedItems());

        if (songs.isEmpty()) {
            controller.getStatus().setText("No song selected.");
            return;
        }

        // Remove them, then save changes to the playlist.
        Playlist pl = controller.getPlaylistTable().getSelectionModel().getSelectedItem();
        pl.removeAll(songs);
        controller.refreshTables();
        try {
            pl.save();
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    public void search() {
        // Create the dialog box.
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Search");
        dialog.setHeaderText("What are you looking for?");
        dialog.setContentText("Enter search term:");
        Optional<String> keyword = dialog.showAndWait();

        // Perform the search.
        if (keyword.isPresent() && keyword.get().trim().length() > 0) {
            int count = search(keyword.get().trim());
            if (count == 0) {
                controller.getStatus().setText("No matches found.");
            } else {
                controller.getStatus().setText("Found " + count + " matching songs.");
            }
        }
    }

    /**
     * Arranges the playlist such that songs matching the keyword have priority.
     *
     * @param keyword The keyword
     * @return The amount of songs that match
     */
    public int search(String keyword) {
        keyword = keyword.toLowerCase();

        // Assign priorities to the songs, depending on relevance.
        int count = 0;
        Map<Song, Integer> priorityMap = new HashMap<>();
        for (Song s : controller.getSongList()) {
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

        // Sort the song by relevance first, then by the song table's
        // comparator.
        Collections.sort(controller.getSongList(), (o1, o2) -> {
            int result = priorityMap.get(o1) - priorityMap.get(o2);
            if (result == 0) {
                if (controller.getSongTable().getComparator() == null) {
                    return 0;
                } else {
                    return controller.getSongTable().getComparator().compare(o1, o2);
                }
            } else {
                return result;
            }
        });

        // Select all relevant songs.
        controller.getSongTable().scrollTo(0);
        controller.getSongTable().getSelectionModel().clearSelection();
        for (int i = 0; i < count; i++) {
            controller.getSongTable().getSelectionModel().select(i);
        }
        return count;
    }

}

package menu;

import static util.DebugUtils.LOGGER;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;

import javafx.collections.FXCollections;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextInputDialog;
import song.Playlist;
import song.Song;
import viewcontroller.Controller;
import viewcontroller.View;

/**
 * Helper class for handling the Playlist menu.
 */
public class PlaylistMenu {

	private Controller controller;

	public PlaylistMenu(Controller controller) {
		this.controller = controller;
	}

	/**
	 * Adds a playlist to the playlist table, then loads it into the song table.
	 *
	 * @param p
	 *            A playlist
	 */
	public void loadPlaylist(Playlist p) {
		controller.getPlaylistList().add(p);
		controller.getPlaylistTable().setItems(controller.getPlaylistList());
		controller.focus(controller.getPlaylistTable(), controller.getPlaylistList().size() - 1);

		controller.setSongList(FXCollections.observableArrayList(p));
		controller.getSongTable().setItems(controller.getSongList());
		LOGGER.log(Level.INFO, "Loaded playlist: " + p.getName());

		// Enable the user to add songs to the playlist (unless the playlist is
		// MainView::masterPlaylist).
		if (p.getName().equals("All Music")) {
			return;
		}
		MenuItem m = new MenuItem(p.getName());
		controller.getAddToPlaylist().getItems().add(m);
		m.setOnAction(event -> {
			List<Song> songsToAdd = new ArrayList<Song>();
			songsToAdd.addAll(controller.getSongTable().getSelectionModel().getSelectedItems());

			if (songsToAdd.isEmpty()) {
				controller.getStatus().setText("No song was selected.");
				return;
			}
			p.addAll(songsToAdd);
			try {
				p.save();
			} catch (IOException e) {
				controller.getStatus().setText("Playlist \"" + p.getName() + "\" save unsuccessful.");
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
	public void shuffle() {
		if (controller.getSongList().isEmpty()) {
			controller.getStatus().setText("No songs to shuffle.");
			return;
		}

		Collections.shuffle(controller.getSongList());
		if (View.getNowPlaying() != null && controller.getSongList().indexOf(View.getNowPlaying()) >= 0) {
			Collections.swap(controller.getSongList(), 0, controller.getSongList().indexOf(View.getNowPlaying()));
		} else {
			controller.getPlaybackMenu().play(0);
		}
		controller.focus(controller.getSongTable(), 0);
	}

	/**
	 * Renames the current playlist.
	 */
	public void renamePlaylist() {
		Playlist pl = controller.getPlaylistTable().getSelectionModel().getSelectedItem();
		String oldName = pl.getName();
		File oldFile = new File(oldName + ".m3u");

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
			// Rename the playlist and attempt to save changes.
			pl.setName(playlistName.get());
			pl.save();
			if (!oldFile.delete()) {
				controller.getStatus().setText("Could not delete the old file.");
			}
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

		} catch (IOException e) {
			controller.getStatus().setText("Rename failed. See log.txt for details.");
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
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

		if (result.get() != ButtonType.OK) {
			return;
		}
		if (new File(pl.getName() + ".m3u").delete()) {
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
		} else {
			controller.getStatus().setText("Deletion failed.");
		}
	}

}

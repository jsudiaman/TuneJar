package tunejar.menu;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.collections.FXCollections;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextInputDialog;
import tunejar.config.Defaults;
import tunejar.player.PlayerController;
import tunejar.song.Playlist;
import tunejar.song.Song;

/**
 * Helper class for handling the Playlist menu.
 */
public class PlaylistMenu extends PlayerMenu {

	private static final Logger LOGGER = LogManager.getLogger();

	public PlaylistMenu(PlayerController controller) {
		super(controller);
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
		LOGGER.info("Loaded playlist: " + p.getName());

		if (p.getName().equals("All Music")) {
			return;
		}
		// Enable the user to add songs to the playlist.
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
				LOGGER.error("Failed to save the playlist.", e);
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
		if (controller.getPlayer().getNowPlaying() != null
				&& controller.getSongList().indexOf(controller.getPlayer().getNowPlaying()) >= 0) {
			Collections.swap(controller.getSongList(), 0,
					controller.getSongList().indexOf(controller.getPlayer().getNowPlaying()));
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
		File oldFile = Paths.get(Defaults.PLAYLISTS_FOLDER, oldName + ".m3u").toFile();

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
			Files.delete(Paths.get(oldFile.toURI()));
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
			LOGGER.catching(Level.ERROR, e);
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
		try {
			Files.delete(Paths.get(Defaults.PLAYLISTS_FOLDER, pl.getName() + ".m3u"));
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
			LOGGER.catching(Level.ERROR, e);
		}
	}

}

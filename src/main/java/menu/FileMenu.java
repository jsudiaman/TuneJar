package menu;

import static util.DebugUtils.LOGGER;

import java.io.IOException;
import java.util.Optional;
import java.util.logging.Level;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;
import song.Playlist;
import viewcontroller.Controller;
import viewcontroller.View;

/**
 * Helper class for handling the File menu.
 */
public class FileMenu {

	private Controller controller;

	public FileMenu(Controller controller) {
		this.controller = controller;
	}

	/**
	 * Creates a new playlist.
	 * 
	 * @return The created playlist.
	 */
	public Playlist createPlaylist() {
		// Prompt the user for a playlist name.
		TextInputDialog dialog = new TextInputDialog("Untitled Playlist");
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
				LOGGER.log(Level.SEVERE, "Failed to save playlist: " + pName + ".m3u", e);
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
		View.addDirectory();
		Platform.runLater(() -> {
			controller.getPlaylistList().set(0, View.getMasterPlaylist());
			controller.refreshTables();
			controller.focus(controller.getPlaylistTable(), 0);
		});
	}

	public void removeDirectory() {
		if (View.removeDirectory()) {
			View.refresh();
			Platform.runLater(() -> {
				controller.getPlaylistList().set(0, View.getMasterPlaylist());
				controller.refreshTables();
				if (View.getNowPlaying() != null
						&& !View.getMasterPlaylist().contains(View.getNowPlaying())) {
					View.stopPlayback();
				}
			});
		}
	}

}

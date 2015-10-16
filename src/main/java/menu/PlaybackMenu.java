package menu;

import static util.DebugUtils.LOGGER;

import java.util.logging.Level;

import viewcontroller.Controller;
import viewcontroller.View;

/**
 * Helper class for handling the Playback menu.
 */
public class PlaybackMenu {

	private Controller controller;

	public PlaybackMenu(Controller controller) {
		this.controller = controller;
	}

	/**
	 * Plays or resumes the selected song.
	 */
	public void play() {
		int index = controller.getSongTable().getFocusModel().getFocusedIndex();
		if (controller.getSongList().isEmpty() || index < 0 || index >= controller.getSongList().size()) {
			controller.getStatus().setText("No song selected.");
			return;
		}
		controller.getShortcutPause().setText("Pause");
		controller.getMenuPause().setText("Pause");
		play(index);
	}

	/**
	 * Plays the song at the specified row of the song table.
	 *
	 * @param row
	 *            The row that the song is located in
	 */
	public void play(int row) {
		try {
			// Have the playlist point to the appropriate song, then play it
			controller.getSongTable().getSelectionModel().clearAndSelect(row);
			controller.getSongList().get(row).play(controller.getVolumeSlider().getValue());
			View.setEndOfSongAction(controller::playNext);

			// Update the status bar accordingly
			controller.getStatus().setText("Now Playing: " + View.getNowPlaying().toString());
		} catch (NullPointerException e) {
			LOGGER.log(Level.SEVERE, "Failed to play song. "
					+ (controller.getSongList().isEmpty() ? "The playlist was empty." : "The playlist was not empty."), e);
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Failed to play song.", e);
		}
	}

	/**
	 * Handling for the pause button. If the pause button says "Pause", it will
	 * pause the currently playing song, then change to "Resume". <br>
	 * <br>
	 * If it says "Resume", it will resume the currently playing song, then
	 * change to "Pause". <br>
	 * <br>
	 * If it says anything else, the error will be logged.
	 */
	public void pause() {
		if (View.getNowPlaying() == null) {
			controller.getStatus().setText("No song is currently playing.");
			return;
		}

		if (controller.getMenuPause().getText().equals("Pause")) {
			controller.getStatus().setText("Paused: " + View.getNowPlaying().toString());
			View.getNowPlaying().pause();
			controller.getShortcutPause().setText("Resume");
			controller.getMenuPause().setText("Resume");
		} else if (controller.getMenuPause().getText().equals("Resume")) {
			controller.getStatus().setText("Now Playing: " + View.getNowPlaying().toString());
			View.getNowPlaying().play(controller.getVolumeSlider().getValue());
			controller.getShortcutPause().setText("Pause");
			controller.getMenuPause().setText("Pause");
		} else {
			LOGGER.log(Level.SEVERE,
					"Invalid text for pause button detected, text was: " + controller.getMenuPause().getText());
		}
	}

	/**
	 * Stops the currently playing song.
	 */
	public void stop() {
		if (View.getNowPlaying() == null) {
			controller.getStatus().setText("No song is currently playing.");
			return;
		}

		controller.getStatus().setText("");
		controller.getShortcutPause().setText("Pause");
		controller.getMenuPause().setText("Pause");
		View.getNowPlaying().stop();
	}

	/**
	 * Plays the previous song.
	 */
	public void playPrev() {
		if (View.getNowPlaying() == null) {
			controller.getStatus().setText("No song is currently playing.");
			return;
		}

		int row = controller.getSongList().indexOf(View.getNowPlaying());
		row = (row <= 0) ? 0 : row - 1;
		play(row);
		controller.getSongTable().getSelectionModel().select(row);
	}

	/**
	 * Plays the next song.
	 */
	public void playNext() {
		if (View.getNowPlaying() == null) {
			controller.getStatus().setText("No song is currently playing.");
			return;
		}

		int row = controller.getSongList().indexOf(View.getNowPlaying());
		row = (row + 1 >= controller.getSongList().size()) ? 0 : row + 1;
		play(row);
		controller.getSongTable().getSelectionModel().select(row);
	}

}

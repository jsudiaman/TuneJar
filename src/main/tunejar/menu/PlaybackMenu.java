package tunejar.menu;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import tunejar.app.AppController;
import tunejar.app.AppLauncher;

/**
 * Helper class for handling the Playback menu.
 */
public class PlaybackMenu {

	// Singleton Object
	private static PlaybackMenu instance = new PlaybackMenu();

	private static final Logger LOGGER = LogManager.getLogger();

	private AppController controller;

	private PlaybackMenu() {
		this.controller = AppController.getInstance();
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
			controller.getSongList().get(row).play();
			AppLauncher.getInstance().setEndOfSongAction(controller::playNext);

			// Update the status bar accordingly
			controller.getStatus().setText("Now Playing: " + AppLauncher.getInstance().getNowPlaying().toString());
		} catch (NullPointerException e) {
			if (controller.getSongList().isEmpty())
				LOGGER.info("The playlist is empty.");
			else
				LOGGER.info("The playlist is not empty.");
			LOGGER.error("Failed to play song.", e);
		} catch (Exception e) {
			LOGGER.error("Failed to play song.", e);
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
		if (AppLauncher.getInstance().getNowPlaying() == null) {
			controller.getStatus().setText("No song is currently playing.");
			return;
		}

		if (controller.getMenuPause().getText().equals("Pause")) {
			controller.getStatus().setText("Paused: " + AppLauncher.getInstance().getNowPlaying().toString());
			AppLauncher.getInstance().getNowPlaying().pause();
			controller.getShortcutPause().setText("Resume");
			controller.getMenuPause().setText("Resume");
		} else if (controller.getMenuPause().getText().equals("Resume")) {
			controller.getStatus().setText("Now Playing: " + AppLauncher.getInstance().getNowPlaying().toString());
			AppLauncher.getInstance().getNowPlaying().play();
			controller.getShortcutPause().setText("Pause");
			controller.getMenuPause().setText("Pause");
		} else {
			LOGGER.fatal("Invalid text for pause button detected, text was: " + controller.getMenuPause().getText());
			throw new AssertionError();
		}
	}

	/**
	 * Stops the currently playing song.
	 */
	public void stop() {
		if (AppLauncher.getInstance().getNowPlaying() == null) {
			controller.getStatus().setText("No song is currently playing.");
			return;
		}

		controller.getStatus().setText("");
		controller.getShortcutPause().setText("Pause");
		controller.getMenuPause().setText("Pause");
		AppLauncher.getInstance().getNowPlaying().stop();
	}

	/**
	 * Plays the previous song.
	 */
	public void playPrev() {
		if (AppLauncher.getInstance().getNowPlaying() == null) {
			controller.getStatus().setText("No song is currently playing.");
			return;
		}

		int row = controller.getSongList().indexOf(AppLauncher.getInstance().getNowPlaying());
		row = (row <= 0) ? 0 : row - 1;
		play(row);
		controller.getSongTable().getSelectionModel().select(row);
	}

	/**
	 * Plays the next song.
	 */
	public void playNext() {
		if (AppLauncher.getInstance().getNowPlaying() == null) {
			controller.getStatus().setText("No song is currently playing.");
			return;
		}

		int row = controller.getSongList().indexOf(AppLauncher.getInstance().getNowPlaying());
		row = (row + 1 >= controller.getSongList().size()) ? 0 : row + 1;
		play(row);
		controller.getSongTable().getSelectionModel().select(row);
	}

	public static PlaybackMenu getInstance() {
		return instance;
	}

}

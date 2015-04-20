package com.jonsudiaman.jvmp3.viewcontroller;

import static com.jonsudiaman.jvmp3.model.DebugUtils.LOGGER;

import java.util.logging.Level;

/**
 * Helper class for handling the Playback menu.
 */
final class PlaybackMenu {

    private MainController controller;

    PlaybackMenu(MainController controller) {
        this.controller = controller;
    }

    /**
     * Plays or resumes the selected song.
     */
    void play() {
        int index = controller.songTable.getFocusModel().getFocusedIndex();
        if (controller.songList.isEmpty() || index < 0 || index >= controller.songList.size()) {
            controller.status.setText("No song selected.");
            return;
        }
        controller.shortcutPause.setText("Pause");
        controller.menuPause.setText("Pause");
        play(index);
    }

    /**
     * Plays the song at the specified row of the song table.
     *
     * @param row
     *            The row that the song is located in
     */
    void play(int row) {
        try {
            // Have the playlist point to the appropriate song, then play it
            controller.songTable.getSelectionModel().clearAndSelect(row);
            controller.songList.get(row).play(controller.volumeSlider.getValue());
            MainView.setEndOfSongAction(controller::playNext);

            // Update the status bar accordingly
            controller.status.setText("Now Playing: " + MainView.getNowPlaying().toString());
        } catch (NullPointerException e) {
            LOGGER.log(Level.SEVERE, "Failed to play song. "
                    + (controller.songList.isEmpty() ? "The playlist was empty."
                            : "The playlist was not empty."), e);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to play song.", e);
        }
    }

    /**
     * Handling for the pause button. If the pause button says "Pause", it will pause the currently
     * playing song, then change to "Resume". <br>
     * <br>
     * If it says "Resume", it will resume the currently playing song, then change to "Pause". <br>
     * <br>
     * If it says anything else, the error will be logged.
     */
    void pause() {
        if (MainView.getNowPlaying() == null) {
            controller.status.setText("No song is currently playing.");
            return;
        }

        if (controller.menuPause.getText().equals("Pause")) {
            controller.status.setText("Paused: " + MainView.getNowPlaying().toString());
            MainView.getNowPlaying().pause();
            controller.shortcutPause.setText("Resume");
            controller.menuPause.setText("Resume");
        } else if (controller.menuPause.getText().equals("Resume")) {
            controller.status.setText("Now Playing: " + MainView.getNowPlaying().toString());
            MainView.getNowPlaying().play(controller.volumeSlider.getValue());
            controller.shortcutPause.setText("Pause");
            controller.menuPause.setText("Pause");
        } else {
            LOGGER.log(Level.SEVERE, "Invalid text for pause button detected, text was: "
                    + controller.menuPause.getText());
        }
    }

    /**
     * Stops the currently playing song.
     */
    void stop() {
        if (MainView.getNowPlaying() == null) {
            controller.status.setText("No song is currently playing.");
            return;
        }

        controller.status.setText("");
        controller.shortcutPause.setText("Pause");
        controller.menuPause.setText("Pause");
        MainView.getNowPlaying().stop();
    }

    /**
     * Plays the previous song.
     */
    void playPrev() {
        if (MainView.getNowPlaying() == null) {
            controller.status.setText("No song is currently playing.");
            return;
        }

        int row = controller.songList.indexOf(MainView.getNowPlaying());
        row = (row <= 0) ? 0 : row - 1;
        play(row);
        controller.songTable.getSelectionModel().select(row);
    }

    /**
     * Plays the next song.
     */
    void playNext() {
        if (MainView.getNowPlaying() == null) {
            controller.status.setText("No song is currently playing.");
            return;
        }

        int row = controller.songList.indexOf(MainView.getNowPlaying());
        row = (row + 1 >= controller.songList.size()) ? 0 : row + 1;
        play(row);
        controller.songTable.getSelectionModel().select(row);
    }

}

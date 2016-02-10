// @formatter:off
/*
 * TuneJar <http://sudicode.com/tunejar/>
 * Copyright (C) 2016 Jonathan Sudiaman
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
// @formatter:on

package com.sudicode.tunejar.menu;

import com.sudicode.tunejar.player.PlayerController;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Helper class for handling the Playback menu. */
public class PlaybackMenu extends PlayerMenu {

    private static final Logger logger = LoggerFactory.getLogger(PlaybackMenu.class);

    public PlaybackMenu(PlayerController controller) {
        super(controller);
    }

    /** Plays or resumes the selected song. */
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
     * @param row The row that the song is located in
     */
    public void play(int row) {
        try {
            // Have the playlist point to the appropriate song, then play it
            controller.getSongTable().getSelectionModel().clearAndSelect(row);
            controller.getPlayer().playSong(controller.getSongList().get(row));
            controller.getPlayer().setEndOfSongAction(controller::playNext);

            // Update the status bar accordingly
            controller.getStatus().setText("Now Playing: " + controller.getPlayer().getNowPlaying().toString());
        } catch (NullPointerException e) {
            if (controller.getSongList().isEmpty())
                logger.info("The playlist is empty.");
            else
                logger.info("The playlist is not empty.");
            logger.error("Failed to play song.", e);
        } catch (Exception e) {
            logger.error("Failed to play song.", e);
        }
    }

    /**
     * Handling for the pause button. If the pause button says "Pause", it will pause the currently playing song, then
     * change to "Resume". <br>
     * <br>
     * If it says "Resume", it will resume the currently playing song, then change to "Pause". <br>
     * <br>
     * If it says anything else, the error will be logged.
     */
    public void pause() {
        if (controller.getPlayer().getNowPlaying() == null) {
            controller.getStatus().setText("No song is currently playing.");
            return;
        }

        if (controller.getMenuPause().getText().equals("Pause")) {
            controller.getStatus().setText("Paused: " + controller.getPlayer().getNowPlaying().toString());
            controller.getPlayer().pauseSong();
            controller.getShortcutPause().setText("Resume");
            controller.getMenuPause().setText("Resume");
        } else if (controller.getMenuPause().getText().equals("Resume")) {
            controller.getStatus().setText("Now Playing: " + controller.getPlayer().getNowPlaying().toString());
            controller.getPlayer().resumeSong();
            controller.getShortcutPause().setText("Pause");
            controller.getMenuPause().setText("Pause");
        } else {
            logger.error("Invalid text for pause button detected, text was: {}", controller.getMenuPause().getText());
            throw new AssertionError();
        }
    }

    /** Stops the currently playing song. */
    public void stop() {
        if (controller.getPlayer().getNowPlaying() == null) {
            controller.getStatus().setText("No song is currently playing.");
            return;
        }

        controller.getStatus().setText("");
        controller.getShortcutPause().setText("Pause");
        controller.getMenuPause().setText("Pause");
        controller.getPlayer().stopSong();
    }

    /** Plays the previous song. */
    public void playPrev() {
        if (controller.getPlayer().getNowPlaying() == null) {
            controller.getStatus().setText("No song is currently playing.");
            return;
        }

        int row = controller.getSongList().indexOf(controller.getPlayer().getNowPlaying());
        row = (row <= 0) ? 0 : row - 1;
        play(row);
        controller.getSongTable().getSelectionModel().select(row);
    }

    /** Plays the next song. */
    public void playNext() {
        if (controller.getPlayer().getNowPlaying() == null) {
            controller.getStatus().setText("No song is currently playing.");
            return;
        }

        int row = controller.getSongList().indexOf(controller.getPlayer().getNowPlaying());
        row = (row + 1 >= controller.getSongList().size()) ? 0 : row + 1;
        play(row);
        controller.getSongTable().getSelectionModel().select(row);
    }

}

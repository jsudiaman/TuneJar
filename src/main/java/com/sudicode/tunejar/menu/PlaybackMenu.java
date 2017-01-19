package com.sudicode.tunejar.menu;

import com.sudicode.tunejar.config.Defaults;
import com.sudicode.tunejar.player.PlayerController;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.ToggleGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Helper class for handling the Playback menu. */
public class PlaybackMenu extends PlayerMenu {

    private static final Logger logger = LoggerFactory.getLogger(PlaybackMenu.class);

    /**
     * If shuffle is enabled, this list is non-null and contains row indices in
     * shuffled order.
     */
    private List<Integer> shuffledRowList;

    /** Used to traverse the shuffled row list. */
    private int shuffledRowIter;

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
        buildShuffledRowList();
    }

    /**
     * Plays the song at the specified row of the song table.
     *
     * @param row The row that the song is located in
     */
    public void play(int row) {
        logger.trace("Playing song at row {}", row);

        try {
            // Have the playlist point to the appropriate song, then play it
            controller.getSongTable().getSelectionModel().clearAndSelect(row);
            controller.getPlayer().playSong(controller.getSongList().get(row));
            controller.getPlayer().setEndOfSongAction(controller::playNext);

            // Update the status bar accordingly
            controller.getStatus().setText("Now Playing: " + controller.getPlayer().getNowPlaying().toString());
        } catch (Exception e) {
            controller.getStatus().setText("Failed to play song.");
            logger.error("Failed to play song.", e);
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
        if (controller.getPlayer().getNowPlaying() == null) {
            controller.getStatus().setText("No song is currently playing.");
            return;
        }

        if ("Pause".equals(controller.getMenuPause().getText())) {
            controller.getStatus().setText("Paused: " + controller.getPlayer().getNowPlaying().toString());
            controller.getPlayer().pauseSong();
            controller.getShortcutPause().setText("Resume");
            controller.getMenuPause().setText("Resume");
        } else if ("Resume".equals(controller.getMenuPause().getText())) {
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
        buildShuffledRowList();
    }

    /** Plays the previous song. */
    public void playPrev() {
        // If no song is playing, return.
        if (controller.getPlayer().getNowPlaying() == null) {
            controller.getStatus().setText("No song is currently playing.");
            return;
        }

        // If the playlist is empty, return.
        if (controller.getSongList().isEmpty()) {
            controller.getStatus().setText("The playlist is empty.");
            return;
        }

        // Play the previous song.
        int row;
        if (isShuffleEnabled()) {
            // Decrement shuffledRowIter. If it falls out of bounds, set it to
            // zero.
            if (--shuffledRowIter < 0) {
                shuffledRowIter = 0;
            }
            row = shuffledRowList.get(shuffledRowIter);
        } else {
            row = controller.getSongList().indexOf(controller.getPlayer().getNowPlaying());
            row = (row <= 0) ? 0 : row - 1;
        }
        play(row);
        controller.getSongTable().getSelectionModel().select(row);
    }

    /** Plays the next song. */
    public void playNext() {
        // If no song is playing, return.
        if (controller.getPlayer().getNowPlaying() == null) {
            controller.getStatus().setText("No song is currently playing.");
            return;
        }

        // If the playlist is empty, return.
        if (controller.getSongList().isEmpty()) {
            controller.getStatus().setText("The playlist is empty.");
            return;
        }

        // Play the next song.
        int row;
        if (isShuffleEnabled()) {
            // Increment shuffledRowIter. If it falls out of bounds, re-shuffle.
            if (++shuffledRowIter >= shuffledRowList.size()) {
                buildShuffledRowList();
            }

            // Play the next song in the shuffled list.
            row = shuffledRowList.get(shuffledRowIter);
        } else {
            row = controller.getSongList().indexOf(controller.getPlayer().getNowPlaying());
            row = (row + 1 >= controller.getSongList().size()) ? 0 : row + 1;
        }
        play(row);
        controller.getSongTable().getSelectionModel().select(row);
    }

    public void initSpeedMenu() {
        ToggleGroup group = new ToggleGroup();
        for (double speed : Defaults.PRESET_SPEEDS) {
            RadioMenuItem nextItem = new RadioMenuItem(Double.toString(speed));
            nextItem.setOnAction((click) -> controller.getPlayer().setSpeed(speed));
            nextItem.setToggleGroup(group);
            if (Double.compare(speed, 1) == 0) {
                group.selectToggle(nextItem);
            }
            controller.getSpeedMenu().getItems().add(nextItem);
        }
    }

    /**
     * Builds the shuffled row list. If shuffle is enabled, the list will be
     * initialized to a non-null value and the iterator will be set to zero. If
     * not, the list will be set to null.
     */
    public void buildShuffledRowList() {
        if (isShuffleEnabled()) {
            logger.debug("Shuffle: ON");
            List<Integer> list = new ArrayList<>();
            for (int i = 0; i < controller.getSongList().size(); i++) {
                list.add(i);
            }
            Collections.shuffle(list);
            if (controller.getPlayer().getNowPlaying() != null) {
                list.add(0, controller.getSongList().indexOf(controller.getPlayer().getNowPlaying()));
            }
            logger.debug("Built shuffled row list");
            shuffledRowList = list;
            shuffledRowIter = 0;
        } else {
            logger.debug("Shuffle: OFF");
            shuffledRowList = null;
        }
    }

    private boolean isShuffleEnabled() {
        return controller.getMenuShuffle().isSelected();
    }

}

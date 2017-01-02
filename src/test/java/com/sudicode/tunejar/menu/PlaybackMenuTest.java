package com.sudicode.tunejar.menu;

import com.sudicode.tunejar.player.Gui;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class PlaybackMenuTest {

    private Gui gui = Gui.getInstance();

    private String getStatus() {
        return gui.getController().getStatus().getText();
    }

    @Test
    public void testMediaControls() {
        // Test play, pause, and stop controls
        gui.getRobot().clickOn("All Music").clickOn("Cute.wav");
        gui.getRobot().clickOn("Playback").clickOn("#menuPlay");
        assertThat(getStatus(), startsWith("Now Playing"));
        gui.getRobot().clickOn("Playback").clickOn("#menuPause");
        assertThat(getStatus(), startsWith("Paused"));
        gui.getRobot().clickOn("Playback").clickOn("#menuPause");
        assertThat(getStatus(), startsWith("Now Playing"));
        gui.getRobot().clickOn("Playback").clickOn("#menuStop");
        assertThat(getStatus(), isEmptyString());
    }

}

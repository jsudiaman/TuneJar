package com.sudicode.tunejar.menu;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeFalse;

import com.sudicode.tunejar.player.IntegrationTest;

import org.apache.commons.lang3.SystemUtils;
import org.junit.Test;

import java.util.function.Supplier;

import javafx.scene.media.MediaException;

public class PlaybackMenuTest extends IntegrationTest {

    @Test
    public void testMediaControls() {
        try {
            // Test play, pause, and stop controls
            Supplier<String> status = () -> getController().getStatus().getText();
            getDriver().clickOn("All Music").clickOn("Cute.wav");
            getDriver().clickOn("Playback").clickOn("#menuPlay");
            assertTrue(status.get().startsWith("Now Playing"));
            getDriver().clickOn("Playback").clickOn("#menuPause");
            assertTrue(status.get().startsWith("Paused"));
            getDriver().clickOn("Playback").clickOn("#menuPause");
            assertTrue(status.get().startsWith("Now Playing"));
            getDriver().clickOn("Playback").clickOn("#menuStop");
            assertEquals("", status.get());
        } catch (MediaException e) {
            assumeFalse("Linux error", SystemUtils.IS_OS_LINUX);
            throw e;
        }
    }

}

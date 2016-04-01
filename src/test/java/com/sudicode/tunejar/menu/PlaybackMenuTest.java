package com.sudicode.tunejar.menu;

import static org.junit.Assert.assertEquals;

import com.sudicode.tunejar.player.IntegrationTest;
import org.junit.Test;

public class PlaybackMenuTest extends IntegrationTest {

    @Test
    public void testMediaControls() {
        final String testSongTitle = "Cute.wav";

        // Select song
        getDriver().clickOn("All Music").clickOn(testSongTitle);

        // Test play
        getDriver().clickOn("Playback").clickOn("#playMenuItem");
        getDriver().sleep(1000);
        assertEquals(testSongTitle, getController().getPlayer().getNowPlaying().getTitle());
    }

}

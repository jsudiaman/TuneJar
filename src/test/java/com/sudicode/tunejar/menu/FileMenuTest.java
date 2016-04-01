package com.sudicode.tunejar.menu;

import static org.junit.Assert.assertTrue;

import com.sudicode.tunejar.config.Defaults;
import com.sudicode.tunejar.player.IntegrationTest;
import com.sudicode.tunejar.song.Playlist;

import org.junit.Test;
import org.loadui.testfx.GuiTest;

import java.nio.file.Files;
import java.util.function.Supplier;

import javafx.collections.ObservableList;
import javafx.scene.control.TextField;

public class FileMenuTest extends IntegrationTest {

    @Test
    public void testNewPlaylist() throws Exception {
        Supplier<ObservableList<Playlist>> items = () -> getController().getPlaylistTable().getItems();
        int index = items.get().size();

        getDriver().clickOn("File").clickOn("New").clickOn("Playlist...");
        TextField name = GuiTest.find(Defaults.PLAYLIST_NAME);
        name.setText("FileMenuTest_testNewPlaylist");
        getDriver().clickOn("OK");
        assertTrue(items.get().get(index).getName().equals("FileMenuTest_testNewPlaylist"));
        assertTrue(Files.exists(Defaults.PLAYLISTS_FOLDER.resolve("FileMenuTest_testNewPlaylist.m3u")));
    }

}

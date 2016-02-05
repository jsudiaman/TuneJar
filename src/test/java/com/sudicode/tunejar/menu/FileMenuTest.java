package com.sudicode.tunejar.menu;

import static org.junit.Assert.assertTrue;

import com.sudicode.tunejar.config.Defaults;
import com.sudicode.tunejar.player.PlayerTest;
import com.sudicode.tunejar.song.Playlist;

import org.junit.Test;
import org.loadui.testfx.GuiTest;

import java.nio.file.Files;
import java.util.function.Supplier;

import javafx.collections.ObservableList;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;

public class FileMenuTest extends PlayerTest {

    @Test
    public void testNewPlaylist() throws Exception {
        Supplier<ObservableList<Playlist>> items = () -> getController().getPlaylistTable().getItems();
        int index = items.get().size();

        getDriver().clickOn("File").clickOn("New...").clickOn("Playlist");
        TextField name = GuiTest.find(Defaults.PLAYLIST_NAME);
        getDriver().type(KeyCode.A, KeyCode.B, KeyCode.C, KeyCode.DIGIT1, KeyCode.DIGIT2, KeyCode.DIGIT3);
        assertTrue(name.getText().equals("abc123"));
        getDriver().clickOn("OK");
        assertTrue(items.get().get(index).getName().equals("abc123"));
        assertTrue(Files.exists(Defaults.PLAYLISTS_FOLDER.resolve("abc123.m3u")));
    }

}

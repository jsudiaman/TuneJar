package com.sudicode.tunejar.menu;

import com.sudicode.tunejar.config.Defaults;
import com.sudicode.tunejar.player.Gui;
import com.sudicode.tunejar.song.Playlist;
import javafx.collections.ObservableList;
import javafx.scene.control.TextField;
import org.junit.Test;
import org.loadui.testfx.GuiTest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class FileMenuTest {

    private Gui gui = Gui.getInstance();

    private ObservableList<Playlist> getPlaylists() {
        return gui.getController().getPlaylistTable().getItems();
    }

    @Test
    public void testNewPlaylist() throws Exception {
        int index = getPlaylists().size();
        gui.getRobot().clickOn("File").clickOn("New").clickOn("Playlist...");
        TextField name = GuiTest.find(Defaults.PLAYLIST_NAME);
        name.setText("FileMenuTest_testNewPlaylist");
        gui.getRobot().clickOn("OK");

        assertThat(getPlaylists().get(index).getName(), is(equalTo("FileMenuTest_testNewPlaylist")));
        assertThat(gui.getPlayer().getOptions().getPlaylists().containsKey("FileMenuTest_testNewPlaylist"), is(true));
    }

}

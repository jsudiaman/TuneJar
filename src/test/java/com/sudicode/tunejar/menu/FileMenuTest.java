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
import javafx.scene.input.KeyCode;

public class FileMenuTest extends IntegrationTest {

    @Test
    public void testNewPlaylist() throws Exception {
        Supplier<ObservableList<Playlist>> items = () -> getController().getPlaylistTable().getItems();
        int index = items.get().size();

        getDriver().clickOn("File").clickOn("New").clickOn("Playlist...");
        TextField name = GuiTest.find(Defaults.PLAYLIST_NAME);
        getDriver().type(KeyCode.A, KeyCode.B, KeyCode.C, KeyCode.DIGIT1, KeyCode.DIGIT2, KeyCode.DIGIT3);
        assertTrue(name.getText().equals("abc123"));
        getDriver().clickOn("OK");
        assertTrue(items.get().get(index).getName().equals("abc123"));
        assertTrue(Files.exists(Defaults.PLAYLISTS_FOLDER.resolve("abc123.m3u")));
    }

}

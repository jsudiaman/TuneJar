/*
 * TuneJar <http://sudicode.com/tunejar/>
 * Copyright (C) 2016 Jonathan Sudiaman
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
*/

package com.sudicode.tunejar.menu;

import static org.junit.Assert.assertTrue;

import com.sudicode.tunejar.player.IntegrationTest;

import org.junit.Test;

public class ThemeMenuTest extends IntegrationTest {

    @Test
    public void testDarkTheme() throws Exception {
        getDriver().clickOn("#themeSelector");
        getDriver().clickOn("Dark Theme");
        assertTrue(getPlayer().getScene().getStylesheets().get(0).endsWith("Dark%20Theme.css"));
        assertTrue(getPlayer().getOptions().getTheme().equals("Dark Theme"));
    }

    @Test
    public void testModena() throws Exception {
        getDriver().clickOn("#themeSelector");
        getDriver().clickOn("Modena");
        assertTrue(getPlayer().getScene().getStylesheets().get(0).endsWith("Modena.css"));
        assertTrue(getPlayer().getOptions().getTheme().equals("Modena"));
    }

}

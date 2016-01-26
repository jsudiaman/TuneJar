package com.sudicode.tunejar.menu;

import com.sudicode.tunejar.player.PlayerTest;
import org.junit.Test;

import static org.junit.Assert.*;

public class ThemeMenuTest extends PlayerTest {

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

package com.sudicode.tunejar.menu;

import com.sudicode.tunejar.player.Gui;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;


public class ThemeMenuTest {

    private Gui gui = Gui.getInstance();

    @Test
    public void testDarkTheme() throws Exception {
        gui.getRobot().clickOn("#themeSelector").sleep(250).clickOn("Dark Theme");
        assertThat(gui.getPlayer().getScene().getStylesheets().get(0), endsWith("Dark%20Theme.css"));
        assertThat(gui.getPlayer().getOptions().getTheme(), is(equalTo(("Dark Theme"))));
    }

    @Test
    public void testModena() throws Exception {
        gui.getRobot().clickOn("#themeSelector").sleep(250).clickOn("Modena");
        assertThat(gui.getPlayer().getScene().getStylesheets().get(0), endsWith("Modena.css"));
        assertThat(gui.getPlayer().getOptions().getTheme(), is(equalTo("Modena")));
    }

}

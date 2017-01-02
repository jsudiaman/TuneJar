package com.sudicode.tunejar.menu;

import com.sudicode.tunejar.player.Gui;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.jayway.awaitility.Awaitility.await;
import static com.jayway.awaitility.Duration.FIVE_SECONDS;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class ThemeMenuTest {

    private static final Logger logger = LoggerFactory.getLogger(ThemeMenuTest.class);

    private Gui gui = Gui.getInstance();

    public void testDarkTheme() throws Exception {
        gui.getRobot().clickOn("#themeSelector");
        await().atMost(FIVE_SECONDS).until(() -> gui.getRobot().clickOn("Dark Theme"));
        logger.info("Clicked on Dark Theme");
        await().atMost(FIVE_SECONDS).until(() -> gui.getPlayer().getScene().getStylesheets().get(0), endsWith("Dark%20Theme.css"));
        assertThat(gui.getPlayer().getOptions().getTheme(), is(equalTo(("Dark Theme"))));
    }

    public void testModena() throws Exception {
        gui.getRobot().clickOn("#themeSelector");
        await().atMost(FIVE_SECONDS).until(() -> gui.getRobot().clickOn("Modena"));
        logger.info("Clicked on Modena");
        await().atMost(FIVE_SECONDS).until(() -> gui.getPlayer().getScene().getStylesheets().get(0), endsWith("Modena.css"));
        assertThat(gui.getPlayer().getOptions().getTheme(), is(equalTo("Modena")));
    }

}

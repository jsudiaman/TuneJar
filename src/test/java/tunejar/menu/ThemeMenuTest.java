package tunejar.menu;

import static org.junit.Assert.*;

import org.junit.Test;

import tunejar.config.Options;
import tunejar.player.Player;
import tunejar.test.AbstractTest;

public class ThemeMenuTest extends AbstractTest {

	@Test
	public void testDarkTheme() throws Exception {
		getController().clickOn("#themeMenu");
		getController().clickOn("Dark Theme");
		// assertTrue(Player.getInstance().getScene().getStylesheets().get(0).matches(".*Dark(.*)Theme\\.css"));
		assertTrue(Options.getInstance().getTheme().equals("Dark Theme"));
	}
	
	@Test
	public void testModena() throws Exception {
		getController().clickOn("#themeMenu");
		getController().clickOn("Modena");
		assertTrue(Player.getInstance().getScene().getStylesheets().get(0).endsWith("Modena.css"));
		assertTrue(Options.getInstance().getTheme().equals("Modena"));
	}

}

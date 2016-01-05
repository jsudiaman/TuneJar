package tunejar.menu;

import static org.junit.Assert.assertTrue;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.loadui.testfx.GuiTest;

import com.jayway.awaitility.Awaitility;

import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import tunejar.config.Defaults;
import tunejar.player.PlayerTest;

public class FileMenuTest extends PlayerTest {

	@Test
	public void testNewPlaylist() throws Exception {
		int index = getController().getPlaylistTable().getItems().size();

		getDriver().clickOn("File").clickOn("New...").clickOn("Playlist");
		TextField name = GuiTest.find(Defaults.PLAYLIST_NAME);
		getRobot().type(KeyCode.T, KeyCode.E, KeyCode.S, KeyCode.T, KeyCode.DIGIT0);
		Awaitility.await().atMost(5, TimeUnit.SECONDS).until((Callable<Boolean>) () -> name.getText().equals("test0"));
		getDriver().clickOn("OK");
		assertTrue(getController().getPlaylistTable().getItems().get(index).getName().equals("test0"));
	}

}

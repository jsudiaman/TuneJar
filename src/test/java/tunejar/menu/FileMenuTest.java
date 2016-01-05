package tunejar.menu;

import java.util.concurrent.Callable;

import org.junit.Test;
import org.loadui.testfx.GuiTest;

import javafx.collections.ObservableList;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import tunejar.config.Defaults;
import tunejar.player.PlayerTest;
import tunejar.song.Playlist;

public class FileMenuTest extends PlayerTest {

	@Test
	public void testNewPlaylist() throws Exception {
		Callable<ObservableList<Playlist>> items = () -> getController().getPlaylistTable().getItems();
		int index = items.call().size();

		getDriver().clickOn("File").clickOn("New...").clickOn("Playlist");
		TextField name = GuiTest.find(Defaults.PLAYLIST_NAME);
		getRobot().type(KeyCode.T, KeyCode.E, KeyCode.S, KeyCode.T, KeyCode.DIGIT0);
		getWait().until((Callable<Boolean>) () -> name.getText().equals("test0"));
		getDriver().clickOn("OK");
		getWait().until((Callable<Boolean>) () -> items.call().get(index).getName().equals("test0"));
	}

}

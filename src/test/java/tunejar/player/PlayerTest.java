package tunejar.player;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.junit.BeforeClass;
import org.loadui.testfx.GuiTest;

import javafx.application.Application;
import javafx.scene.Parent;
import tunejar.config.Options;

public abstract class PlayerTest {

	private static boolean initialized = false;
	private static GuiTest controller;

	/**
	 * Starts the TuneJar player.
	 * 
	 * @throws Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		if (!initialized)
			init();
	}

	/**
	 * Used to manipulate the TuneJar player.
	 * 
	 * @see GuiTest
	 */
	public static GuiTest getController() {
		return controller;
	}

	public static Player getPlayer() {
		return Player.getPlayer();
	}

	private static void init() throws Exception {
		// Set directories
		Options options = new Options();
		Set<File> set = new HashSet<>();
		set.add(new File("src/test/resources/"));
		options.setDirectories(set);

		// Launch the application
		new Thread(() -> Application.launch(Player.class)).start();
		Player.getInitLatch().await();
		Thread.sleep(2 * 1000);
		controller = new GuiTest() {
			@Override
			protected Parent getRootNode() {
				return Player.getPlayer().getScene().getRoot();
			}
		};
		initialized = true;
	}

}

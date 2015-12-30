package tunejar.test;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.junit.BeforeClass;
import org.loadui.testfx.GuiTest;

import javafx.application.Application;
import javafx.scene.Parent;
import tunejar.config.Options;
import tunejar.player.Player;

public abstract class AbstractTest {

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

	private static void init() throws Exception {
		// Initialize directories to avoid being blocked by a dialog box
		if (Options.getInstance().getDirectories().isEmpty()) {
			Set<File> set = new HashSet<>();
			set.add(new File("."));
			Options.getInstance().setDirectories(set);
		}

		// Launch the application
		new Thread(() -> Application.launch(Player.class)).start();
		Player.getInitLatch().await();
		Thread.sleep(2 * 1000);
		controller = new GuiTest() {
			@Override
			protected Parent getRootNode() {
				return Player.getInstance().getScene().getRoot();
			}
		};
		initialized = true;
	}

}

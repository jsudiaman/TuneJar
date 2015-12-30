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
		// To avoid seeing the blocking dialog box
		Set<File> set = new HashSet<>();
		set.add(new File("."));
		Options.getInstance().setDirectories(set);

		if (!initialized) {
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

	/**
	 * Used to manipulate the TuneJar player.
	 * 
	 * @see GuiTest
	 */
	public static GuiTest getController() {
		return controller;
	}

}

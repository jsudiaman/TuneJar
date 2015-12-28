package tunejar.test;

import org.junit.BeforeClass;
import org.loadui.testfx.GuiTest;

import javafx.application.Application;
import javafx.scene.Parent;
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

package com.sudicode.tunejar.player;

import java.io.File;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import org.junit.BeforeClass;
import org.loadui.testfx.GuiTest;

import com.jayway.awaitility.Awaitility;
import com.jayway.awaitility.core.ConditionFactory;
import com.sudicode.tunejar.config.Defaults;
import com.sudicode.tunejar.config.Options;
import com.sudicode.tunejar.player.Player;
import com.sudicode.tunejar.player.PlayerController;

import javafx.application.Application;
import javafx.scene.Parent;

public abstract class PlayerTest {

	private static boolean initialized = false;
	private static GuiTest driver;

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

	private static void init() throws Exception {
		// Delete all playlists
		File[] files = new File(Defaults.PLAYLISTS_FOLDER).listFiles();
		if (files != null) {
			for (File f : files) {
				Files.delete(f.toPath());
			}
		}

		// Set directories
		Options options = new Options();
		Set<File> dirs = new HashSet<>();
		dirs.add(new File("src/test/resources/"));
		options.setDirectories(dirs);

		// Launch application
		new Thread(() -> Application.launch(Player.class)).start();
		getWait().ignoreException(NullPointerException.class).until((Callable<Boolean>) () -> {
			return getPlayer().isInitialized();
		});
		TimeUnit.SECONDS.sleep(1);

		// Set driver
		setDriver(new GuiTest() {
			@Override
			protected Parent getRootNode() {
				return Player.getPlayer().getScene().getRoot();
			}
		});

		// Initialization complete
		initialized = true;
	}

	protected static PlayerController getController() {
		return getPlayer().getController();
	}

	/**
	 * Used to manipulate the TuneJar player.
	 * 
	 * @see GuiTest
	 */
	protected static GuiTest getDriver() {
		return driver;
	}

	private static void setDriver(GuiTest driver) {
		PlayerTest.driver = driver;
	}

	protected static Player getPlayer() {
		return Player.getPlayer();
	}

	protected static ConditionFactory getWait() {
		return Awaitility.await().atMost(30, TimeUnit.SECONDS);
	}

}

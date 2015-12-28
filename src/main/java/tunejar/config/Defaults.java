package tunejar.config;

import java.io.File;

import tunejar.player.Player;

/**
 * These constants should only be modified by TuneJar developers. They are
 * <b>not</b> meant to be user-configurable.
 */
public class Defaults {

	private Defaults() {
	}

	public static final long GET_SONGS_TIMEOUT = 5 * 60;
	public static final int LOG_FILE_LIMIT = 15;
	public static final String TEST_MP3 = "/BitQuest.mp3";
	public static final String LOG_FOLDER = "logs";
	public static final int MAX_LOOPS = 1000;
	public static final String PLAYER_FXML = "/fxml/Player.fxml";
	public static final String THEME_DIR = new File(Player.class.getResource("/theme").getFile()).toString();
	public static final String THEME = "Modena";
	public static final String ICON = "/img/icon.png";
	public static final String OPTIONS_FILE = "options.json";
	public static final double VOLUME = 1.0;

}

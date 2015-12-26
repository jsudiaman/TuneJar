package tunejar.config;

import tunejar.song.Mp3SongTest;

/**
 * These constants should only be modified by TuneJar developers. They are
 * <b>not</b> meant to be user-configurable.
 */
public class Defaults {

	private Defaults() {
	}

	public static final long GET_SONGS_TIMEOUT = 5 * 60;
	public static final String DIRECTORY_FILENAME = "directories.dat";
	public static final String TEST_MP3_FILE = Mp3SongTest.class.getResource("BitQuest.mp3").getFile();
	public static final int LOG_FILE_LIMIT = 15;
	public static final String LOG_FOLDER = "logs";
	public static final int MAX_LOOPS = 1000;
	public static final String PLAYER_FXML = "fxml/Player.fxml";
	public static final String THEME = "theme/DarkTheme.css";
	public static final String ICON = "img/icon.png";
	public static final String OPTIONS_FILE = "options.json";

}

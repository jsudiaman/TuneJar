package tunejar.config;

import java.io.File;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import tunejar.player.Player;

/**
 * These constants should only be modified by TuneJar developers. They are
 * <b>not</b> meant to be user-configurable. User configuration can be done via
 * options.json.
 */
public class Defaults {

	private Defaults() {
	}

	public static final long GET_SONGS_TIMEOUT;
	public static final int LOG_FILE_LIMIT;
	public static final String TEST_MP3;
	public static final String TEST_MP4;
	public static final String LOG_FOLDER;
	public static final int MAX_LOOPS;
	public static final String PLAYER_FXML;
	public static final String THEME;
	public static final String ICON;
	public static final String OPTIONS_FILE;
	public static final double VOLUME;
	public static final Map<String, String> THEME_MAP;
	public static final String[] SORT_ORDER;
	public static final Set<File> DIRECTORIES;
	public static final String TUNEJAR_HOME;

	static {
		// Theme map
		String modena = Player.class.getResource("/theme/Modena.css").toString();
		String darkTheme = Player.class.getResource("/theme/Dark Theme.css").toString();
		Map<String, String> themeMap = new LinkedHashMap<>();
		themeMap.put("Modena", modena);
		themeMap.put("Dark Theme", darkTheme);
		THEME_MAP = Collections.unmodifiableMap(themeMap);

		// TuneJar home
		TUNEJAR_HOME = Paths.get(System.getProperty("user.home"), "Documents", "TuneJar").toString();
		new File(TUNEJAR_HOME).mkdirs();
		OPTIONS_FILE = Paths.get(TUNEJAR_HOME, "options.json").toString();

		// Misc. constants
		LOG_FOLDER = "logs";
		GET_SONGS_TIMEOUT = 5 * 60;
		LOG_FILE_LIMIT = 5;
		TEST_MP3 = "/BitQuest.mp3";
		TEST_MP4 = "/BitQuest.m4a";
		MAX_LOOPS = 1000;
		PLAYER_FXML = "/fxml/Player.fxml";
		THEME = "Modena";
		ICON = "/img/icon.png";
		VOLUME = 1.0;
		SORT_ORDER = new String[0];
		DIRECTORIES = new HashSet<>();
	}

}

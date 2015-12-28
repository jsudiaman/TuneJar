package tunejar.config;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import tunejar.player.Player;

/**
 * These constants should only be modified by TuneJar developers. They are
 * <b>not</b> meant to be user-configurable.
 */
public class Defaults {

	private Defaults() {
	}

	public static final long GET_SONGS_TIMEOUT;
	public static final int LOG_FILE_LIMIT;
	public static final String TEST_MP3;
	public static final String LOG_FOLDER;
	public static final int MAX_LOOPS;
	public static final String PLAYER_FXML;
	public static final String THEME;
	public static final String ICON;
	public static final String OPTIONS_FILE;
	public static final double VOLUME;
	public static final Map<String, String> THEME_MAP;

	static {
		GET_SONGS_TIMEOUT = 5 * 60;
		LOG_FILE_LIMIT = 15;
		TEST_MP3 = "/BitQuest.mp3";
		LOG_FOLDER = "logs";
		MAX_LOOPS = 1000;
		PLAYER_FXML = "/fxml/Player.fxml";
		THEME = "Modena";
		ICON = "/img/icon.png";
		OPTIONS_FILE = "options.json";
		VOLUME = 1.0;

		// Theme Map
		String modena = Player.class.getResource("/theme/Modena.css").toString();
		String darkTheme = Player.class.getResource("/theme/Dark Theme.css").toString();
		Map<String, String> themeMap = new LinkedHashMap<>();
		themeMap.put("Modena", modena);
		themeMap.put("Dark Theme", darkTheme);
		THEME_MAP = Collections.unmodifiableMap(themeMap);
	}

}

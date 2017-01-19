package com.sudicode.tunejar.config;

import com.google.common.collect.ImmutableMap;
import com.sudicode.tunejar.player.Player;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.prefs.Preferences;

/**
 * Constants.
 */
public final class Defaults {

    /**
     * Data structure used to store music directories.
     */
    public static final LinkedHashSet<File> DIRECTORIES = new LinkedHashSet<>();

    /**
     * Timeout (in seconds) for refreshing.
     */
    public static final long TIMEOUT = 5L * 60;

    /**
     * Path to the TuneJar icon.
     */
    public static final String ICON = "/img/icon.png";

    /**
     * Path to the TuneJar FXML file.
     */
    public static final String PLAYER_FXML = "/fxml/Player.fxml";

    /**
     * Order to sort the songs by (Title / Artist / Album).
     */
    public static final String[] SORT_ORDER = new String[0];

    /**
     * Order of the columns.
     */
    public static final String[] COLUMN_ORDER = new String[]{"Title", "Artist", "Album"};

    /**
     * Direction to sort by (ASCENDING / DESCENDING).
     */
    public static final String SORT_DIRECTION = "ASCENDING";

    /**
     * Player theme.
     */
    public static final String THEME = "Modena";

    /**
     * Maps theme names to the paths of their respective CSS files.
     */
    public static final Map<String, String> THEME_MAP = ImmutableMap.of("Modena", Player.class.getResource("/theme/Modena.css").toString(),
            "Dark Theme", Player.class.getResource("/theme/Dark Theme.css").toString());

    /**
     * Player volume, ranging from [0-1].
     */
    public static final double VOLUME = 1.0;

    /**
     * Default playlist name.
     */
    public static final String PLAYLIST_NAME = "Untitled Playlist";

    /**
     * Playback speeds for the user to choose from.
     */
    public static final double[] PRESET_SPEEDS = new double[]{.25, .5, 1, 1.25, 1.5, 2};

    /**
     * Shuffle playback?
     */
    public static final boolean SHUFFLE = false;

    /**
     * Data structure to store playlists. Maps playlist titles to their respective M3U strings.
     */
    public static final LinkedHashMap<String, String> PLAYLISTS = new LinkedHashMap<>();

    /**
     * Used to instantiate {@link Options}.
     */
    public static final Preferences PREFERENCES_NODE = Preferences.userNodeForPackage(Options.class);

    /**
     * Width of the player.
     */
    public static final double WINDOW_WIDTH = 1000;

    /**
     * Height of the player.
     */
    public static final double WINDOW_HEIGHT = 600;

    /**
     * Should the player be maximized?
     */
    public static final boolean MAXIMIZED = false;

    /**
     * Illegal.
     */
    private Defaults() {
    }

}

package com.sudicode.tunejar.config;

import com.sudicode.tunejar.player.Player;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;

public final class Defaults {

    public static final LinkedHashSet<File> DIRECTORIES;
    public static final long TIMEOUT;
    public static final String ICON;
    public static final Path LOG_FOLDER;
    public static final String PLAYER_FXML;
    public static final Path PLAYLISTS_FOLDER;
    public static final String[] SORT_ORDER;
    public static final String[] COLUMN_ORDER;
    public static final String SORT_DIRECTION;
    public static final String THEME;
    public static final Map<String, String> THEME_MAP;
    public static final Path TUNEJAR_HOME;
    public static final double VOLUME;
    public static final String PLAYLIST_NAME;
    public static final double[] PRESET_SPEEDS;
    public static final boolean SHUFFLE;

    static {
        // Theme map
        String modena = Player.class.getResource("/theme/Modena.css").toString();
        String darkTheme = Player.class.getResource("/theme/Dark Theme.css").toString();
        Map<String, String> themeMap = new LinkedHashMap<>();
        themeMap.put("Modena", modena);
        themeMap.put("Dark Theme", darkTheme);
        THEME_MAP = Collections.unmodifiableMap(themeMap);

        // TuneJar home
        TUNEJAR_HOME = Paths.get(System.getProperty("user.home"), "Documents", "TuneJar");
        PLAYLISTS_FOLDER = TUNEJAR_HOME.resolve("Playlists");
        LOG_FOLDER = TUNEJAR_HOME.resolve("Logs");

        // Misc. constants
        DIRECTORIES = new LinkedHashSet<>();
        TIMEOUT = 5 * 60;
        ICON = "/img/icon.png";
        PLAYER_FXML = "/fxml/Player.fxml";
        SORT_ORDER = new String[0];
        COLUMN_ORDER = new String[] {"Title", "Artist", "Album"};
        SORT_DIRECTION = "ASCENDING";
        THEME = "Modena";
        VOLUME = 1.0;
        PLAYLIST_NAME = "Untitled Playlist";
        PRESET_SPEEDS = new double[] {.25, .5, 1, 1.25, 1.5, 2};
        SHUFFLE = false;
    }

    private Defaults() {}

}

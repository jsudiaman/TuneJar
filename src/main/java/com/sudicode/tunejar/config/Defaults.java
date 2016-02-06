package com.sudicode.tunejar.config;

import com.sudicode.tunejar.player.Player;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class Defaults {

    public static final Set<File> DIRECTORIES;
    public static final long TIMEOUT;
    public static final String ICON;
    public static final int LOG_FILE_LIMIT;
    public static final Path LOG_FOLDER;
    public static final Path OPTIONS_FILE;
    public static final String PLAYER_FXML;
    public static final Path PLAYLISTS_FOLDER;
    public static final String[] SORT_ORDER;
    public static final String THEME;
    public static final Map<String, String> THEME_MAP;
    public static final Path TUNEJAR_HOME;
    public static final double VOLUME;
    public static final String PLAYLIST_NAME;

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
        OPTIONS_FILE = TUNEJAR_HOME.resolve("options.json");
        PLAYLISTS_FOLDER = TUNEJAR_HOME.resolve("Playlists");
        LOG_FOLDER = TUNEJAR_HOME.resolve("Logs");

        // Misc. constants
        DIRECTORIES = new HashSet<>();
        TIMEOUT = 5 * 60;
        ICON = "/img/icon.png";
        LOG_FILE_LIMIT = 5;
        PLAYER_FXML = "/fxml/Player.fxml";
        SORT_ORDER = new String[0];
        THEME = "Modena";
        VOLUME = 1.0;
        PLAYLIST_NAME = "Untitled Playlist";
    }

    private Defaults() {}

}

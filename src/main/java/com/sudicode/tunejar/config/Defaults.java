package com.sudicode.tunejar.config;

import com.google.common.collect.ImmutableMap;
import com.sudicode.tunejar.player.Player;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.prefs.Preferences;

public final class Defaults {

    public static final LinkedHashSet<File> DIRECTORIES;
    public static final long TIMEOUT;
    public static final String ICON;
    public static final String PLAYER_FXML;
    public static final String[] SORT_ORDER;
    public static final String[] COLUMN_ORDER;
    public static final String SORT_DIRECTION;
    public static final String THEME;
    public static final Map<String, String> THEME_MAP;
    public static final double VOLUME;
    public static final String PLAYLIST_NAME;
    public static final double[] PRESET_SPEEDS;
    public static final boolean SHUFFLE;
    public static final LinkedHashMap<String, String> PLAYLISTS;
    public static final Preferences PREFERENCES_NODE;
    public static final double WINDOW_WIDTH;
    public static final double WINDOW_HEIGHT;
    public static final boolean MAXIMIZED;

    static {
        DIRECTORIES = new LinkedHashSet<>();
        TIMEOUT = 5 * 60;
        ICON = "/img/icon.png";
        PLAYER_FXML = "/fxml/Player.fxml";
        SORT_ORDER = new String[0];
        COLUMN_ORDER = new String[]{"Title", "Artist", "Album"};
        SORT_DIRECTION = "ASCENDING";
        THEME = "Modena";
        THEME_MAP = ImmutableMap.of("Modena", Player.class.getResource("/theme/Modena.css").toString(), "Dark Theme",
                Player.class.getResource("/theme/Dark Theme.css").toString());
        VOLUME = 1.0;
        PLAYLIST_NAME = "Untitled Playlist";
        PRESET_SPEEDS = new double[]{.25, .5, 1, 1.25, 1.5, 2};
        SHUFFLE = false;
        PLAYLISTS = new LinkedHashMap<>();
        PREFERENCES_NODE = Preferences.userNodeForPackage(Options.class);
        WINDOW_WIDTH = 1000;
        WINDOW_HEIGHT = 600;
        MAXIMIZED = false;
    }

    private Defaults() {
    }

}

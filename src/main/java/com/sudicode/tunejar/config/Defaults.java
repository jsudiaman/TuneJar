/*
 * TuneJar <http://sudicode.com/tunejar/>
 * Copyright (C) 2016 Jonathan Sudiaman
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.sudicode.tunejar.config;

import com.sudicode.tunejar.player.Player;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
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
    public static final String[] COLUMN_ORDER;
    public static final String SORT_DIRECTION;
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
        DIRECTORIES = new LinkedHashSet<>();
        TIMEOUT = 5 * 60;
        ICON = "/img/icon.png";
        LOG_FILE_LIMIT = 5;
        PLAYER_FXML = "/fxml/Player.fxml";
        SORT_ORDER = new String[0];
        COLUMN_ORDER = new String[] {"Title", "Artist", "Album"};
        SORT_DIRECTION = "ASCENDING";
        THEME = "Modena";
        VOLUME = 1.0;
        PLAYLIST_NAME = "Untitled Playlist";
    }

    private Defaults() {}

}

package com.sudicode.tunejar.config;

import javafx.scene.control.TableColumn.SortType;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import static org.apache.commons.lang3.SerializationUtils.*;

/**
 * The {@link Options} object is a set of getter/setter pairs which safely
 * interact with a {@link Preferences} node.
 */
public final class Options {

    /**
     * {@link Preferences} node.
     */
    private Preferences prefs;

    /**
     * Construct a new {@link Options} object.
     *
     * @param prefs {@link Preferences} node to use
     */
    public Options(final Preferences prefs) {
        this.prefs = prefs;
    }

    /**
     * @return Current player theme.
     */
    public String getTheme() {
        return prefs.get("theme", Defaults.THEME);
    }

    /**
     * @param theme Player theme to use.
     */
    public void setTheme(final String theme) {
        prefs.put("theme", theme);
    }

    /**
     * @return Music directories.
     */
    public LinkedHashSet<File> getDirectories() {
        byte[] buff = prefs.getByteArray("directories", null);
        return buff != null ? deserialize(buff) : Defaults.DIRECTORIES;
    }

    /**
     * @param directories Music directories to use.
     */
    public void setDirectories(final LinkedHashSet<File> directories) {
        prefs.putByteArray("directories", serialize(directories));
    }

    /**
     * @return Current player volume, ranging from [0-1].
     */
    public double getVolume() {
        return prefs.getDouble("volume", Defaults.VOLUME);
    }

    /**
     * @param volume Player volume to use, ranging from [0-1].
     */
    public void setVolume(final double volume) {
        prefs.putDouble("volume", volume);
    }

    /**
     * @return Current sort order (Title / Artist / Album).
     */
    public String[] getSortOrder() {
        byte[] buff = prefs.getByteArray("sortOrder", null);
        return buff != null ? deserialize(buff) : Defaults.SORT_ORDER;
    }

    /**
     * @param sorts Sort order to use (Title / Artist / Album).
     */
    public void setSortOrder(final String... sorts) {
        prefs.putByteArray("sortOrder", serialize(sorts));
    }

    /**
     * @return Current order of the columns.
     */
    public String[] getColumnOrder() {
        byte[] buff = prefs.getByteArray("columnOrder", null);
        return buff != null ? deserialize(buff) : Defaults.COLUMN_ORDER;
    }

    /**
     * @param columns Column order to use.
     */
    public void setColumnOrder(final String... columns) {
        prefs.putByteArray("columnOrder", serialize(columns));
    }

    /**
     * @return Direction to sort titles.
     */
    public SortType getTitleSortDirection() {
        switch (prefs.get("titleSortDirection", Defaults.SORT_DIRECTION)) {
            case "ASCENDING":
                return SortType.ASCENDING;
            case "DESCENDING":
                return SortType.DESCENDING;
            default:
                return null;
        }
    }

    /**
     * @param direction Direction to sort titles (ASCENDING / DESCENDING).
     */
    public void setTitleSortDirection(final String direction) {
        prefs.put("titleSortDirection", direction);
    }

    /**
     * @return Direction to sort artists.
     */
    public SortType getArtistSortDirection() {
        switch (prefs.get("artistSortDirection", Defaults.SORT_DIRECTION)) {
            case "ASCENDING":
                return SortType.ASCENDING;
            case "DESCENDING":
                return SortType.DESCENDING;
            default:
                return null;
        }
    }

    /**
     * @param direction Direction to sort artists (ASCENDING / DESCENDING).
     */
    public void setArtistSortDirection(final String direction) {
        prefs.put("artistSortDirection", direction);
    }

    /**
     * @return Direction to sort albums.
     */
    public SortType getAlbumSortDirection() {
        switch (prefs.get("albumSortDirection", Defaults.SORT_DIRECTION)) {
            case "ASCENDING":
                return SortType.ASCENDING;
            case "DESCENDING":
                return SortType.DESCENDING;
            default:
                return null;
        }
    }

    /**
     * @param direction Direction to sort albums (ASCENDING / DESCENDING).
     */
    public void setAlbumSortDirection(final String direction) {
        prefs.put("albumSortDirection", direction);
    }

    /**
     * @return <code>true</code> if playback is shuffled.
     */
    public boolean isShuffle() {
        return prefs.getBoolean("shuffle", Defaults.SHUFFLE);
    }

    /**
     * @param shuffle <code>true</code> to shuffle playback.
     */
    public void setShuffle(final boolean shuffle) {
        prefs.putBoolean("shuffle", shuffle);
    }

    /**
     * @return Map of playlist titles to their respective M3U strings.
     */
    public LinkedHashMap<String, String> getPlaylists() {
        byte[] buff = prefs.getByteArray("playlists", null);
        return buff != null ? deserialize(buff) : Defaults.PLAYLISTS;
    }

    /**
     * @param playlists Map of playlist titles to their respective M3U strings.
     */
    public void setPlaylists(final LinkedHashMap<String, String> playlists) {
        prefs.putByteArray("playlists", serialize(playlists));
    }

    /**
     * @return Current width of the player.
     */
    public double getWindowWidth() {
        return prefs.getDouble("windowWidth", Defaults.WINDOW_WIDTH);
    }

    /**
     * @param windowWidth Player width to use.
     */
    public void setWindowWidth(final double windowWidth) {
        prefs.putDouble("windowWidth", windowWidth);
    }

    /**
     * @return Current height of the player.
     */
    public double getWindowHeight() {
        return prefs.getDouble("windowHeight", Defaults.WINDOW_HEIGHT);
    }

    /**
     * @param windowHeight Player height to use.
     */
    public void setWindowHeight(final double windowHeight) {
        prefs.putDouble("windowHeight", windowHeight);
    }

    /**
     * @return <code>true</code> if the player is maximized.
     */
    public boolean isMaximized() {
        return prefs.getBoolean("maximized", Defaults.MAXIMIZED);
    }

    /**
     * @param maximized <code>true</code> to maximize the player.
     */
    public void setMaximized(final boolean maximized) {
        prefs.putBoolean("maximized", maximized);
    }

    /**
     * Clears all key-value mappings in the {@link Preferences} node.
     *
     * @throws BackingStoreException if this operation cannot be completed due to a failure in the backing store, or
     *                               inability to communicate with it.
     */
    public void clear() throws BackingStoreException {
        prefs.clear();
    }

}

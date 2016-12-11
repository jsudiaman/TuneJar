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

    private Preferences prefs;

    public Options(Preferences prefs) {
        this.prefs = prefs;
    }

    public String getTheme() {
        return prefs.get("theme", Defaults.THEME);
    }

    public void setTheme(String theme) {
        prefs.put("theme", theme);
    }

    public LinkedHashSet<File> getDirectories() {
        byte[] buff = prefs.getByteArray("directories", null);
        return buff != null ? deserialize(buff) : Defaults.DIRECTORIES;
    }

    public void setDirectories(LinkedHashSet<File> directories) {
        prefs.putByteArray("directories", serialize(directories));
    }

    public double getVolume() {
        return prefs.getDouble("volume", Defaults.VOLUME);
    }

    public void setVolume(double volume) {
        prefs.putDouble("volume", volume);
    }

    public String[] getSortOrder() {
        byte[] buff = prefs.getByteArray("sortOrder", null);
        return buff != null ? deserialize(buff) : Defaults.SORT_ORDER;
    }

    public void setSortOrder(String... sorts) {
        prefs.putByteArray("sortOrder", serialize(sorts));
    }

    public String[] getColumnOrder() {
        byte[] buff = prefs.getByteArray("columnOrder", null);
        return buff != null ? deserialize(buff) : Defaults.COLUMN_ORDER;
    }

    public void setColumnOrder(String... columns) {
        prefs.putByteArray("columnOrder", serialize(columns));
    }

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

    public void setTitleSortDirection(String direction) {
        prefs.put("titleSortDirection", direction);
    }

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

    public void setArtistSortDirection(String direction) {
        prefs.put("artistSortDirection", direction);
    }

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

    public void setAlbumSortDirection(String direction) {
        prefs.put("albumSortDirection", direction);
    }

    public boolean isShuffle() {
        return prefs.getBoolean("shuffle", Defaults.SHUFFLE);
    }

    public void setShuffle(boolean shuffle) {
        prefs.putBoolean("shuffle", shuffle);
    }

    public LinkedHashMap<String, String> getPlaylists() {
        byte[] buff = prefs.getByteArray("playlists", null);
        return buff != null ? deserialize(buff) : Defaults.PLAYLISTS;
    }

    public void setPlaylists(LinkedHashMap<String, String> playlists) {
        prefs.putByteArray("playlists", serialize(playlists));
    }

    public double getWindowWidth() {
        return prefs.getDouble("windowWidth", Defaults.WINDOW_WIDTH);
    }

    public void setWindowWidth(double windowWidth) {
        prefs.putDouble("windowWidth", windowWidth);
    }

    public double getWindowHeight() {
        return prefs.getDouble("windowHeight", Defaults.WINDOW_HEIGHT);
    }

    public void setWindowHeight(double windowHeight) {
        prefs.putDouble("windowHeight", windowHeight);
    }

    /**
     * Clears all key-value mappings in the {@link Preferences} node.
     */
    public void clear() throws BackingStoreException {
        prefs.clear();
    }

}

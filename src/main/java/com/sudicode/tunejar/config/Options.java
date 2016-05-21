package com.sudicode.tunejar.config;

import org.apache.commons.lang3.SerializationUtils;

import java.io.File;
import java.util.LinkedHashSet;
import java.util.prefs.Preferences;

import javafx.scene.control.TableColumn.SortType;

/**
 * The {@link Options} object is a set of getter/setter pairs which safely
 * interact with TuneJar's {@link Preferences} node. It can be instantiated once
 * per session using the {@link Options#newInstance()} method.
 */
public class Options {

    private static Options instance;

    private Preferences prefs;

    private Options() {
        prefs = Preferences.userNodeForPackage(getClass());
    }

    /**
     * Instantiates {@link Options}.
     * 
     * @return An instance of {@link Options}.
     * @throws IllegalStateException If an instance already exists.
     */
    public static synchronized Options newInstance() {
        if (instance == null) {
            instance = new Options();
            return instance;
        } else {
            throw new IllegalStateException("Instance already exists");
        }
    }

    public String getTheme() {
        return prefs.get("theme", Defaults.THEME);
    }

    public void setTheme(String theme) {
        prefs.put("theme", theme);
    }

    public LinkedHashSet<File> getDirectories() {
        byte[] buff = prefs.getByteArray("directories", null);
        return buff != null ? SerializationUtils.deserialize(buff) : Defaults.DIRECTORIES;
    }

    public void setDirectories(LinkedHashSet<File> directories) {
        prefs.putByteArray("directories", SerializationUtils.serialize(directories));
    }

    public double getVolume() {
        return prefs.getDouble("volume", Defaults.VOLUME);
    }

    public void setVolume(double volume) {
        prefs.putDouble("volume", volume);
    }

    public String[] getSortOrder() {
        byte[] buff = prefs.getByteArray("sortOrder", null);
        return buff != null ? SerializationUtils.deserialize(buff) : Defaults.SORT_ORDER;
    }

    public void setSortOrder(String... sorts) {
        prefs.putByteArray("sortOrder", SerializationUtils.serialize(sorts));
    }

    public String[] getColumnOrder() {
        byte[] buff = prefs.getByteArray("columnOrder", null);
        return buff != null ? SerializationUtils.deserialize(buff) : Defaults.COLUMN_ORDER;
    }

    public void setColumnOrder(String... columns) {
        prefs.putByteArray("columnOrder", SerializationUtils.serialize(columns));
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

}

package com.sudicode.tunejar.config;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TableColumn.SortType;

@SuppressWarnings("unchecked")
public class Options {

    private static final Logger logger = LoggerFactory.getLogger(Options.class);

    private final File optionsFile;
    private JSONObject backingMap;
    private boolean writeEnabled;

    public Options(File optionsFile) {
        this.optionsFile = optionsFile;
        this.writeEnabled = true;

        try {
            init();
        } catch (ParseException e) {
            handleParseException(e);
        } catch (IOException e) {
            handleIOException(e);
        }
    }

    private void init() throws IOException, ParseException {
        if (Files.exists(optionsFile.toPath())) {
            backingMap = read();
        } else {
            reset();
        }
    }

    /** Builds the backing map by parsing the options file. */
    private JSONObject read() throws IOException, ParseException {
        synchronized (Options.class) {
            JSONParser parser = new JSONParser();
            return (JSONObject) parser.parse(new FileReader(optionsFile));
        }
    }

    /** Writes the backing map to the options file. */
    private void write() {
        synchronized (Options.class) {
            if (writeEnabled) {
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(optionsFile, false))) {
                    writer.write(backingMap.toJSONString());
                    logger.debug("Settings saved successfully to: " + optionsFile);
                } catch (IOException e) {
                    handleIOException(e);
                }
            } else {
                logger.debug("Write is disabled.");
            }
        }
    }

    /** Resets options back to their default settings. */
    private void reset() {
        backingMap = new JSONObject();
        setTheme(Defaults.THEME);
        setDirectories(Defaults.DIRECTORIES);
        setVolume(Defaults.VOLUME);
        setSortOrder(Defaults.SORT_ORDER);
        setColumnOrder(Defaults.COLUMN_ORDER);
        setTitleSortDirection(Defaults.SORT_DIRECTION);
        setArtistSortDirection(Defaults.SORT_DIRECTION);
        setAlbumSortDirection(Defaults.SORT_DIRECTION);
    }

    public String getTheme() {
        if (backingMap.get("theme") == null)
            setTheme(Defaults.THEME);

        return (String) backingMap.get("theme");
    }

    public void setTheme(String theme) {
        backingMap.put("theme", theme);
        write();
    }

    public Set<File> getDirectories() {
        if (backingMap.get("directories") == null)
            setDirectories(Defaults.DIRECTORIES);

        // Convert JSONArray to Set
        Set<File> dirSet = new HashSet<>();
        JSONArray arr = (JSONArray) backingMap.get("directories");
        arr.forEach((dir) -> dirSet.add(new File(dir.toString())));

        // Return the resulting set
        return dirSet;
    }

    public void setDirectories(Set<File> directories) {
        // Convert Set to JSONArray
        JSONArray arr = new JSONArray();
        directories.forEach((dir) -> arr.add(dir.getAbsolutePath()));

        // Store the resulting JSONArray
        backingMap.put("directories", arr);
        write();
    }

    public Double getVolume() {
        if (backingMap.get("volume") == null)
            setVolume(Defaults.VOLUME);

        return (Double) backingMap.get("volume");
    }

    public void setVolume(Double volume) {
        backingMap.put("volume", volume);
        write();
    }

    public String[] getSortOrder() {
        if (backingMap.get("sortOrder") == null)
            setSortOrder(Defaults.SORT_ORDER);

        // Convert JSONArray to String array
        JSONArray arr = (JSONArray) backingMap.get("sortOrder");
        List<String> list = new ArrayList<>();
        arr.forEach((o) -> list.add(o.toString()));

        // Return the resulting String array
        return list.toArray(new String[list.size()]);
    }

    public void setSortOrder(String... sorts) {
        // Convert String array to JSONArray
        JSONArray arr = new JSONArray();
        arr.addAll(Arrays.asList(sorts));

        // Store the resulting JSONArray
        backingMap.put("sortOrder", arr);
        write();
    }

    public String[] getColumnOrder() {
        if (backingMap.get("columnOrder") == null) {
            setColumnOrder(Defaults.COLUMN_ORDER);
        }

        // Convert JSONArray to String array
        JSONArray arr = (JSONArray) backingMap.get("columnOrder");
        List<String> list = new ArrayList<>();
        arr.forEach((o) -> list.add(o.toString()));

        // Return the resulting String array
        return list.toArray(new String[list.size()]);
    }

    public void setColumnOrder(String... columns) {
        // Convert String array to JSONArray
        JSONArray arr = new JSONArray();
        arr.addAll(Arrays.asList(columns));

        // Store the resulting JSONArray
        backingMap.put("columnOrder", arr);
        write();
    }

    public SortType getTitleSortDirection() {
        if (backingMap.get("titleSortDirection") == null) {
            setTitleSortDirection(Defaults.SORT_DIRECTION);
        }

        switch ((String) backingMap.get("titleSortDirection")) {
            case "ASCENDING":
                return SortType.ASCENDING;
            case "DESCENDING":
                return SortType.DESCENDING;
            default:
                return null;
        }
    }

    public void setTitleSortDirection(String direction) {
        backingMap.put("titleSortDirection", direction);
        write();
    }

    public SortType getArtistSortDirection() {
        if (backingMap.get("artistSortDirection") == null) {
            setArtistSortDirection(Defaults.SORT_DIRECTION);
        }

        switch ((String) backingMap.get("artistSortDirection")) {
            case "ASCENDING":
                return SortType.ASCENDING;
            case "DESCENDING":
                return SortType.DESCENDING;
            default:
                return null;
        }
    }

    public void setArtistSortDirection(String direction) {
        backingMap.put("artistSortDirection", direction);
        write();
    }

    public SortType getAlbumSortDirection() {
        if (backingMap.get("albumSortDirection") == null) {
            setAlbumSortDirection(Defaults.SORT_DIRECTION);
        }

        switch ((String) backingMap.get("albumSortDirection")) {
            case "ASCENDING":
                return SortType.ASCENDING;
            case "DESCENDING":
                return SortType.DESCENDING;
            default:
                return null;
        }
    }

    public void setAlbumSortDirection(String direction) {
        backingMap.put("albumSortDirection", direction);
        write();
    }

    public void fixCorruptedFile() {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("File Corrupted");
        alert.setHeaderText(null);
        alert.setContentText(optionsFile + " is corrupted. Your settings have been reset.");
        alert.showAndWait();
        reset();
    }

    private void handleParseException(ParseException e) {
        logger.error(e.getMessage(), e);
        fixCorruptedFile();
    }

    private void handleIOException(IOException e) {
        // Log the error and alert the user.
        logger.error(e.getMessage(), e);
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Read Error");
        alert.setHeaderText(null);
        alert.setContentText("Could not access " + optionsFile + ". Your settings will not be saved.");
        alert.showAndWait();

        // Disable write access, then reset.
        writeEnabled = false;
        reset();
    }

}

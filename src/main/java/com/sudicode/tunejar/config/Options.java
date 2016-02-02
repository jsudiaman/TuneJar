package com.sudicode.tunejar.config;

import com.cedarsoftware.util.io.JsonWriter;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

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

@SuppressWarnings("unchecked")
public class Options {

    private static final Logger logger = LoggerFactory.getLogger(Options.class);

    private final File optionsFile;
    private JSONObject optionsMap;
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
            optionsMap = read();
        } else {
            reset();
        }
    }

    private JSONObject read() throws IOException, ParseException {
        synchronized (Options.class) {
            JSONParser parser = new JSONParser();
            return (JSONObject) parser.parse(new FileReader(optionsFile));
        }
    }

    private void write() {
        synchronized (Options.class) {
            if (writeEnabled) {
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(optionsFile, false))) {
                    writer.write(JsonWriter.formatJson(optionsMap.toJSONString()));
                    logger.debug("Settings saved successfully to: " + optionsFile);
                } catch (IOException e) {
                    handleIOException(e);
                }
            } else {
                logger.debug("Write is disabled.");
            }
        }
    }

    /**
     * Resets options back to their default settings.
     */
    private void reset() {
        optionsMap = new JSONObject();
        setTheme(Defaults.THEME);
        setDirectories(Defaults.DIRECTORIES);
        setVolume(Defaults.VOLUME);
        setSortOrder(Defaults.SORT_ORDER);
    }

    public String getTheme() {
        if (optionsMap.get("theme") == null)
            setTheme(Defaults.THEME);

        return (String) optionsMap.get("theme");
    }

    public void setTheme(String theme) {
        optionsMap.put("theme", theme);
        write();
    }

    public Set<File> getDirectories() {
        if (optionsMap.get("directories") == null)
            setDirectories(Defaults.DIRECTORIES);

        // Convert JSONArray to Set
        Set<File> dirSet = new HashSet<>();
        JSONArray arr = (JSONArray) optionsMap.get("directories");
        arr.forEach((dir) -> dirSet.add(new File(dir.toString())));

        // Return the resulting set
        return dirSet;
    }

    public void setDirectories(Set<File> directories) {
        // Convert Set to JSONArray
        JSONArray arr = new JSONArray();
        directories.forEach((dir) -> arr.add(dir.getAbsolutePath()));

        // Store the resulting JSONArray
        optionsMap.put("directories", arr);
        write();
    }

    public Double getVolume() {
        if (optionsMap.get("volume") == null)
            setVolume(Defaults.VOLUME);

        return (Double) optionsMap.get("volume");
    }

    public void setVolume(Double volume) {
        optionsMap.put("volume", volume);
        write();
    }

    public String[] getSortOrder() {
        if (optionsMap.get("sortOrder") == null)
            setSortOrder(Defaults.SORT_ORDER);

        // Convert JSONArray to String array
        JSONArray arr = (JSONArray) optionsMap.get("sortOrder");
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
        optionsMap.put("sortOrder", arr);
        write();
    }

    private void handleParseException(ParseException e) {
        // Log the error and alert the user.
        logger.error(e.getMessage(), e);
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("File Corrupted");
        alert.setHeaderText(null);
        alert.setContentText(optionsFile + " is corrupted. Your settings have been reset.");
        alert.showAndWait();

        // Reset settings.
        reset();
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

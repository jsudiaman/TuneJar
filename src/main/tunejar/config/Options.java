package tunejar.config;

import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.cedarsoftware.util.io.JsonWriter;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

// Documentation: http://code.google.com/p/json-simple/
public class Options {

	private static final Options INSTANCE = new Options();

	private final Logger logger;
	private final Path optionsFile;
	private JSONObject options;
	private boolean writeEnabled;

	private Options() {
		logger = LogManager.getLogger();
		optionsFile = Paths.get(Defaults.OPTIONS_FILE);
		writeEnabled = true;

		try {
			init();
		} catch (ParseException e) {
			handleParseException(e);
		} catch (IOException e) {
			handleIOException(e);
		}
	}

	private void init() throws IOException, ParseException {
		if (Files.exists(optionsFile)) {
			options = read();
		} else {
			reset();
		}
	}

	private JSONObject read() throws IOException, ParseException {
		JSONParser parser = new JSONParser();
		return (JSONObject) parser.parse(new FileReader(optionsFile.toFile()));
	}

	private void write() {
		if (writeEnabled) {
			try (BufferedWriter writer = new BufferedWriter(new FileWriter(Defaults.OPTIONS_FILE, false))) {
				writer.write(JsonWriter.formatJson(options.toJSONString()));
				logger.info("Settings saved successfully to: " + Defaults.OPTIONS_FILE);
			} catch (IOException e) {
				handleIOException(e);
			}
		} else {
			logger.info("Write is disabled.");
		}
	}

	private void reset() {
		options = new JSONObject();
		setTheme(Defaults.THEME);
		setDirectories(new JSONArray());
		setVolume(Defaults.VOLUME);
	}

	public String getTheme() {
		return (String) options.get("theme");
	}

	@SuppressWarnings("unchecked")
	public void setTheme(String theme) {
		options.put("theme", theme);
		write();
	}

	public JSONArray getDirectories() {
		return (JSONArray) options.get("directories");
	}

	@SuppressWarnings("unchecked")
	public void setDirectories(JSONArray directories) {
		options.put("directories", directories);
		write();
	}
	
	public Double getVolume() {
		return (Double) options.get("volume");
	}
	
	@SuppressWarnings("unchecked")
	public void setVolume(Double volume) {
		options.put("volume", volume);
		write();
	}

	private void handleParseException(Exception e) {
		// Log the error and alert the user.
		logger.catching(Level.ERROR, e);
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("File Corrupted");
		alert.setHeaderText(null);
		alert.setContentText(Defaults.OPTIONS_FILE + " is corrupted. Your settings have been reset.");
		alert.showAndWait();

		// Reset settings.
		reset();
	}

	private void handleIOException(Exception e) {
		// Log the error and alert the user.
		logger.catching(Level.ERROR, e);
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Read Error");
		alert.setHeaderText(null);
		alert.setContentText("Could not access " + Defaults.OPTIONS_FILE + ". Your settings will not be saved.");
		alert.showAndWait();

		// Disable write access, then reset.
		writeEnabled = false;
		reset();
	}

	public static Options getInstance() {
		return INSTANCE;
	}

}

package tunejar.config;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

// Documentation: http://code.google.com/p/json-simple/
public class Options {

	private static final Options INSTANCE = new Options();

	private final Path optionsFile;
	private JSONObject options;

	private Options() {
		optionsFile = Paths.get(Defaults.OPTIONS_FILE);
		try {
			init();
		} catch (IOException | ParseException e) {
			throw new RuntimeException(e);
		}
	}

	private void init() throws IOException, ParseException {
		if (Files.exists(optionsFile)) {
			options = read();
		} else {
			options = new JSONObject();
			setTheme(Defaults.THEME);
			setDirectories(new JSONArray());
		}
	}

	private JSONObject read() throws FileNotFoundException, IOException, ParseException {
		JSONParser parser = new JSONParser();
		return (JSONObject) parser.parse(new FileReader(optionsFile.toFile()));
	}

	private void write() throws IOException {
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(Defaults.OPTIONS_FILE, false))) {
			options.writeJSONString(writer);
		}
	}

	public String getTheme() {
		return (String) options.get("theme");
	}

	@SuppressWarnings("unchecked")
	public void setTheme(String theme) throws IOException {
		options.put("theme", theme);
		write();
	}

	public JSONArray getDirectories() {
		return (JSONArray) options.get("directories");
	}

	@SuppressWarnings("unchecked")
	public void setDirectories(JSONArray directories) throws IOException {
		options.put("directories", directories);
		write();
	}

	public static Options getInstance() {
		return INSTANCE;
	}

}

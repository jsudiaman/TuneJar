package tunejar.app;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Class for logging information to the console and log file.
 */
public final class AppLogger {

	private static final Logger LOGGER = initLogger();
	private static final String LOG_FILE = "log.txt";

	private AppLogger() {
	}

	private static Logger initLogger() {
		Logger log = Logger.getLogger(AppLauncher.class.getName());
		try {
			Handler handler = new FileHandler(LOG_FILE);
			handler.setFormatter(new SimpleFormatter());
			log.addHandler(handler);
			log.log(Level.INFO, LOG_FILE + " initialized successfully.");
		} catch (IOException e) {
			log.log(Level.WARNING, "Failed to generate " + LOG_FILE + ". Logs will be written only to the console.", e);
		}
		return log;
	}

	public static Logger getLogger() {
		return LOGGER;
	}

}

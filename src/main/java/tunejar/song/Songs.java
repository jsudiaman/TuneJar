package tunejar.song;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Songs {

	private static final Logger LOGGER = LogManager.getLogger();

	private Songs() {
	}

	/**
	 * Constructs a {@link Song} out of a file.
	 * 
	 * @param file
	 *            The file to be used.
	 * @return The constructed {@link Song}, or <code>null</code> if the file
	 *         type is not supported.
	 */
	public static Song create(File file) {
		if (file.getName().endsWith(".mp3")) {
			LOGGER.debug("From file: " + file);
			return new Mp3Song(file);
		} else if (file.getName().endsWith(".mp4") || file.getName().endsWith(".m4a")) {
			LOGGER.debug("From file: " + file);
			return new Mp4Song(file);
		} else if (file.getName().endsWith(".wav")) {
			LOGGER.debug("From file: " + file);
			return new WavSong(file);
		}

		// Unsupported file type.
		return null;
	}

	/**
	 * Duplicates a {@link Song} by using its copy constructor.
	 * 
	 * @param song
	 *            The song to be used.
	 * @return The duplicate {@link Song}.
	 */
	public static Song duplicate(Song song) {
		if (song instanceof Mp3Song) {
			return new Mp3Song((Mp3Song) song);
		} else if (song instanceof Mp4Song) {
			return new Mp4Song((Mp4Song) song);
		} else if (song instanceof WavSong) {
			return new WavSong((WavSong) song);
		}

		// All implementations of Song should have a copy constructor.
		throw new AssertionError();
	}

}

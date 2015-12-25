package tunejar.song;

import java.io.File;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;

public class SongFactory {

	private static final SongFactory INSTANCE = new SongFactory();
	private static final Logger LOGGER = LogManager.getLogger();

	private SongFactory() {
	}

	/**
	 * Constructs a {@link Song} out of a file.
	 * 
	 * @param file
	 *            The file to be used.
	 * @return The constructed {@link Song}, or <code>null</code> if the file
	 *         type is not supported.
	 * @throws IOException
	 * @throws InvalidDataException
	 * @throws UnsupportedTagException
	 */
	public Song fromFile(File file) throws IOException, UnsupportedTagException, InvalidDataException {
		if (file.getName().endsWith("mp3")) {
			LOGGER.debug("From file: " + file);
			return new Mp3Song(new Mp3File(file));
		}

		// Unsupported file type
		return null;
	}

	/**
	 * Duplicates a {@link Song} by using its copy constructor (if applicable).
	 * 
	 * @param song
	 *            The song to be used.
	 * @return The duplicate {@link Song}, or <code>null</code> if the
	 *         {@link Song} does not support copy construction.
	 */
	public Song fromSong(Song song) {
		if (song instanceof Mp3Song) {
			return new Mp3Song((Mp3Song) song);
		}

		// Song does not support copy construction
		return null;
	}

	public static SongFactory getInstance() {
		return INSTANCE;
	}

}

package tunejar.config;

import tunejar.song.Mp3SongTest;

/**
 * These constants should only be modified by TuneJar developers. They are
 * <b>not</b> meant to be user-configurable.
 */
public class Constants {

	private Constants() {
	}

	public static final long GET_SONGS_TIMEOUT = 5 * 60;
	public static final String DIRECTORY_FILENAME = "directories.dat";
	public static final String TEST_MP3_FILE = Mp3SongTest.class.getResource("BitQuest.mp3").getFile();
	public static final int LOG_FILE_LIMIT = 15;
	public static final String LOG_FOLDER = "logs";
	public static final int MAX_LOOPS = 1000;

}

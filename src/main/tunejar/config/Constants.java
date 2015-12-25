package tunejar.config;

import tunejar.song.Mp3SongTest;

public class Constants {

	private Constants() {
	}

	public static final long GET_SONGS_TIMEOUT = 5 * 60;
	public static final String DIRECTORY_FILENAME = "directories.dat";
	public static final String TEST_MP3_FILE = Mp3SongTest.class.getResource("BitQuest.mp3").getFile();

}

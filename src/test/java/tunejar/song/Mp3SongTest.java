package tunejar.song;

import tunejar.config.Defaults;

public class Mp3SongTest extends AbstractSongTest {

	@Override
	public String getSongFile() {
		return getClass().getResource(Defaults.TEST_MP3).getFile();
	}

}

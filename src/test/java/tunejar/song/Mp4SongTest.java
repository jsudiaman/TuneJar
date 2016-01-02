package tunejar.song;

import tunejar.config.Defaults;

public class Mp4SongTest extends SongTest {

	@Override
	public String getSongFile() {
		return getClass().getResource(Defaults.TEST_MP4).getFile();
	}

}

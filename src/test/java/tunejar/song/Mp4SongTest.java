package tunejar.song;

import tunejar.config.Defaults;

public class Mp4SongTest extends AbstractSongTest {

	@Override
	public String getSongFile() {
		return getClass().getResource(Defaults.TEST_MP4).getFile();
	}

}

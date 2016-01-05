package tunejar.song;

import java.io.File;

/**
 * Currently does not support reading or writing of metadata.
 */
public class WavSong extends Song {

	public WavSong(File wavFile) {
		audioFile = wavFile;
	}

	public WavSong(WavSong wavSong) {
		this(wavSong.audioFile);
	}

	@Override
	public boolean canEdit() {
		return false;
	}

	@Override
	public void setTitle(String title) throws Exception {
		throw new AssertionError();
	}

	@Override
	public void setArtist(String artist) throws Exception {
		throw new AssertionError();
	}

	@Override
	public void setAlbum(String album) throws Exception {
		throw new AssertionError();
	}

}

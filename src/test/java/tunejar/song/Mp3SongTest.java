package tunejar.song;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import tunejar.config.Defaults;

public class Mp3SongTest {

	// NOTE: Variables that begin with '_' should not be modified outside of
	// setUp() and tearDown().
	private String _mp3File;
	private Song _song;
	private String _originalTitle;
	private String _originalArtist;
	private String _originalAlbum;

	@Before
	public void setUp() throws Exception {
		_mp3File = getClass().getResource(Defaults.TEST_MP3).getFile();
		_song = SongFactory.getInstance().fromFile(new File(_mp3File));
		_originalTitle = _song.getTitle();
		_originalArtist = _song.getArtist();
		_originalAlbum = _song.getAlbum();
	}

	@After
	public void tearDown() throws Exception {
		_song = SongFactory.getInstance().fromFile(new File(_mp3File));
		_song.setTitle(_originalTitle);
		_song.setArtist(_originalArtist);
		_song.setAlbum(_originalAlbum);
	}

	@Test
	public void testSetTitle() throws Exception {
		// Assertion: The song is editable.
		assertTrue(_song.canEdit());

		// Set the title.
		_song.setTitle("TEST");

		// Assertion: ONLY the title has changed in the metadata.
		Song song = SongFactory.getInstance().fromFile(new File(_mp3File));
		assertEquals("TEST", song.getTitle());
		assertEquals(_originalArtist, song.getArtist());
		assertEquals(_originalAlbum, song.getAlbum());
	}

	@Test
	public void testSetArtist() throws Exception {
		// Assertion: The song is editable.
		assertTrue(_song.canEdit());

		// Set the artist.
		_song.setArtist("TEST");

		// Assertion: ONLY the artist has changed in the metadata.
		Song song = SongFactory.getInstance().fromFile(new File(_mp3File));
		assertEquals(_originalTitle, song.getTitle());
		assertEquals("TEST", song.getArtist());
		assertEquals(_originalAlbum, song.getAlbum());
	}

	@Test
	public void testSetAlbum() throws Exception {
		// Assertion: The song is editable.
		assertTrue(_song.canEdit());

		// Set the album.
		_song.setAlbum("TEST");

		// Assertion: ONLY the album has changed in the metadata.
		Song song = SongFactory.getInstance().fromFile(new File(_mp3File));
		assertEquals(_originalTitle, song.getTitle());
		assertEquals(_originalArtist, song.getArtist());
		assertEquals("TEST", song.getAlbum());
	}

}

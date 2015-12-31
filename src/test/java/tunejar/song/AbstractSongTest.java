package tunejar.song;

import static org.junit.Assert.*;
import static org.junit.Assume.*;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.testfx.api.FxRobot;

import tunejar.player.Player;
import tunejar.test.AbstractTest;

/**
 * Tests an implementation of the {@link Song} interface. Subclasses are
 * required to supply a song file by overriding
 * {@link AbstractSongTest#getSongFile()}.
 */
public abstract class AbstractSongTest extends AbstractTest {

	// NOTE: Variables that begin with '_' should not be modified outside of
	// setUp() and tearDown().
	private String _songFile;
	private Song _song;
	private String _originalTitle;
	private String _originalArtist;
	private String _originalAlbum;

	public abstract String getSongFile();

	@Before
	public final void setUp() throws Exception {
		_songFile = getSongFile();
		_song = SongFactory.getInstance().fromFile(new File(_songFile));
		_originalTitle = _song.getTitle();
		_originalArtist = _song.getArtist();
		_originalAlbum = _song.getAlbum();
	}

	@After
	public final void tearDown() throws Exception {
		_song = SongFactory.getInstance().fromFile(new File(_songFile));
		if (_song.canEdit()) {
			_song.setTitle(_originalTitle);
			_song.setArtist(_originalArtist);
			_song.setAlbum(_originalAlbum);
		}
	}

	@Test
	public final void testSetTitle() throws Exception {
		assumeTrue(_song.canEdit());

		// Set the title.
		_song.setTitle("TEST");

		// Assertion: ONLY the title has changed in the metadata.
		Song song = SongFactory.getInstance().fromFile(new File(_songFile));
		assertEquals("TEST", song.getTitle());
		assertEquals(_originalArtist, song.getArtist());
		assertEquals(_originalAlbum, song.getAlbum());
	}

	@Test
	public final void testSetArtist() throws Exception {
		assumeTrue(_song.canEdit());

		// Set the artist.
		_song.setArtist("TEST");

		// Assertion: ONLY the artist has changed in the metadata.
		Song song = SongFactory.getInstance().fromFile(new File(_songFile));
		assertEquals(_originalTitle, song.getTitle());
		assertEquals("TEST", song.getArtist());
		assertEquals(_originalAlbum, song.getAlbum());
	}

	@Test
	public final void testSetAlbum() throws Exception {
		assumeTrue(_song.canEdit());

		// Set the album.
		_song.setAlbum("TEST");

		// Assertion: ONLY the album has changed in the metadata.
		Song song = SongFactory.getInstance().fromFile(new File(_songFile));
		assertEquals(_originalTitle, song.getTitle());
		assertEquals(_originalArtist, song.getArtist());
		assertEquals("TEST", song.getAlbum());
	}

	@Test
	public final void testMediaControls() throws Exception {
		FxRobot robot = new FxRobot();
		robot.interact(() -> _song.play());
		assumeTrue(Player.getInstance().isPlayable());
		assertEquals(_song, Player.getInstance().getNowPlaying());
		robot.interact(() -> _song.pause());
		assertEquals(_song, Player.getInstance().getNowPlaying());
		robot.interact(() -> _song.stop());
		assertNull(Player.getInstance().getNowPlaying());
	}

}

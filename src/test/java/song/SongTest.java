package song;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.mpatric.mp3agic.Mp3File;

import de.saxsys.mvvmfx.testingutils.jfxrunner.JfxRunner;
import viewcontroller.View;

@RunWith(JfxRunner.class)
public class SongTest {

	private File testMP3File;
	private Song testMP3Song;

	@Before
	public void setUp() throws Exception {
		testMP3File = new File("src/test/resources/MyMP3.mp3");
		testMP3Song = new Song(new Mp3File(testMP3File));

		testMP3Song.setTag("Cute", "Bensound", "No Album");
	}

	@After
	public void tearDown() throws Exception {
		testMP3Song.setTag("Cute", "Bensound", "No Album");
	}

	@Test
	public void testSongSong() {
		Song otherSong = new Song(testMP3Song);

		assertEquals(testMP3Song.getAbsoluteFilename(), otherSong.getAbsoluteFilename());
		assertEquals(testMP3Song.getFilename(), otherSong.getFilename());
		assertEquals(testMP3Song.getTitle(), otherSong.getTitle());
		assertEquals(testMP3Song.getArtist(), otherSong.getArtist());
		assertEquals(testMP3Song.getAlbum(), otherSong.getAlbum());
	}

	@Test
	public void testGetTitle() {
		assertEquals("Cute", testMP3Song.getTitle());
	}

	@Test
	public void testGetArtist() {
		assertEquals("Bensound", testMP3Song.getArtist());
	}

	@Test
	public void testGetAlbum() {
		assertEquals("No Album", testMP3Song.getAlbum());
	}

	@Test
	public void testSetTag() {
		testMP3Song.setTag("Sample Title", "Sample Artist", "Sample Album");

		assertEquals("Sample Title", testMP3Song.getTitle());
		assertEquals("Sample Artist", testMP3Song.getArtist());
		assertEquals("Sample Album", testMP3Song.getAlbum());
	}

	@Test
	public void testGetFilename() {
		assertEquals("MyMP3", testMP3Song.getFilename());
	}

	@Test
	public void testGetAbsoluteFilename() {
		String absFilename = testMP3Song.getAbsoluteFilename();

		assertTrue(absFilename.endsWith("MyMP3.mp3"));
	}

	@Test
	public void testPlay() {
		testMP3Song.play(100);

		assertEquals(testMP3Song, View.getNowPlaying());
	}

	@Test
	public void testPause() {
		testMP3Song.play(100);
		testMP3Song.pause();
		
		assertEquals(testMP3Song, View.getNowPlaying());
	}

	@Test
	public void testStop() {
		testMP3Song.play(100);
		testMP3Song.stop();

		assertNull(View.getNowPlaying());
	}

	@Test
	public void testCanSave() {
		assertTrue(testMP3Song.canSave());
	}

	@Test
	public void testToString() {
		assertEquals("Cute - Bensound", testMP3Song.toString());
	}

}

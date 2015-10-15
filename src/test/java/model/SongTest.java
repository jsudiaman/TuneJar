// TODO Testing needs a complete rework.
package model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.mpatric.mp3agic.Mp3File;

public class SongTest {

	Song song;

	@Before
	public void setUp() throws Exception {
		song = new Song(new Mp3File(new File("src/test/resources/Queen of the Night.mp3")));
	}

	@After
	public void tearDown() throws Exception {

	}

	@Test
	public void testGetTitle() throws Exception {
		assertEquals("Queen of the Night", song.getTitle());
	}

	@Test
	public void testGetArtist() throws Exception {
		assertEquals("Machinimasound", song.getArtist());
	}

	@Test
	public void testGetAlbum() throws Exception {
		assertEquals("Machinimasound 2009", song.getAlbum());
	}

	@Test
	public void testSetTag() throws Exception {
		song.setTag("Test_Title", "Test_Artist", "Test_Album");
		assertEquals("Test_Title", song.getTitle());
		assertEquals("Test_Artist", song.getArtist());
		assertEquals("Test_Album", song.getAlbum());
		song.setTag("Queen of the Night", "Machinimasound", "Machinimasound 2009");
	}

	@Test
	public void testGetFilename() throws Exception {
		assertEquals(song.getFilename(), "Queen of the Night");
	}

	@Test
	public void testGetAbsoluteFilename() throws Exception {
		assertTrue(song.getAbsoluteFilename().endsWith("Queen of the Night.mp3"));
	}

	@Test
	public void testToString() throws Exception {
		assertEquals("Queen of the Night - Machinimasound", song.toString());
	}

}
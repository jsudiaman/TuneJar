// TODO Testing needs a complete rework.
package model;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.mpatric.mp3agic.Mp3File;

public class PlaylistTest {

	Playlist p;

	@Before
	public void setUp() throws Exception {
		p = new Playlist();
		for (File f : new File("src/test/resources").listFiles()) {
			if (f.toString().endsWith(".mp3")) {
				p.add(new Song(new Mp3File(f)));
			}
		}
	}

	@After
	public void tearDown() throws Exception {
		new File("Untitled.m3u").deleteOnExit();
	}

	@Test
	public void testGetName() throws Exception {
		assertEquals("Untitled", p.getName());
	}

	@Test
	public void testSave() throws Exception {
		p.save();
		assertEquals(3, p.size());
	}
}
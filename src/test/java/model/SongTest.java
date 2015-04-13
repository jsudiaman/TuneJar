package model;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.*;

import com.mpatric.mp3agic.Mp3File;

public class SongTest {

    Song song;
    Song equalSong;
    Song notEqualSong;

    @Before
    public void setUp() throws Exception {
        song = new Song(new Mp3File(new File("src/test/resources/Queen of the Night.mp3")));
        equalSong = new Song(new Mp3File(new File("src/test/resources/Queen of the Night.mp3")));
        notEqualSong = new Song(new Mp3File(new File("src/test/resources/Sunlight.mp3")));
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

    @Test
    public void testEquals() throws Exception {
        assertEquals(equalSong, song);
        assertNotSame(notEqualSong, song);
    }

    @Test
    public void testHashCode() throws Exception {
        assertEquals(equalSong.hashCode(), song.hashCode());
        assertNotSame(notEqualSong.hashCode(), song.hashCode());
    }
}
package model;

import com.mpatric.mp3agic.Mp3File;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;

public class SongTest {

    Song song;

    @Before
    public void setUp() throws Exception {
        song = new Song(new Mp3File(new File("src/test/resources/Queen of the Night.mp3")));
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
        song.setTag("TempTitle", "TempArtist", "TempAlbum");
        Song temp = new Song(new Mp3File(new File("src/test/resources/Queen of the Night.mp3")));
        assertEquals("TempTitle", temp.getTitle());
        assertEquals("TempArtist", temp.getArtist());
        assertEquals("TempAlbum", temp.getAlbum());

        temp.setTag("Queen of the Night", "Machinimasound", "Machinimasound 2009"); // Reset the tags
    }

}
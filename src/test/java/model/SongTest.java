package model;

import com.mpatric.mp3agic.Mp3File;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

public class SongTest {

    Song song;

    @Before
    public void setUp() throws Exception {
        song = new Song(new Mp3File(new File("src/test/resources/Queen of the Night.mp3")));
    }

    @Test
    public void testGetAlbum() throws Exception {
        System.out.println(("Album: " + song.getAlbum()));
    }

    @Test
    public void testGetArtist() throws Exception {
        System.out.println("Artist: " + song.getArtist());
    }

    @Test
    public void testGetTitle() throws Exception {
        System.out.println("Title: " + song.getTitle());
    }

}
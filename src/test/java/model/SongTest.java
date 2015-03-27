package model;

import com.mpatric.mp3agic.Mp3File;
import org.junit.Before;
import org.junit.Test;

public class SongTest {

    Song song;
    String filePath;

    @Before
    public void setUp() throws Exception {
        // Use the absolute path of a valid MP3 file
        // TODO Add a royalty-free mp3 with proper tags to the "resources" folder instead of using an absolute path.
        filePath = "C:\\Users\\Jonathan\\Music\\01 - Blank Space.mp3";
        song = new Song(new Mp3File(filePath));

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
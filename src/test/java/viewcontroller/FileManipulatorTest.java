package viewcontroller;

import model.FileManipulator;
import model.Song;
import org.junit.Test;

import java.io.File;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class FileManipulatorTest {

    @Test
    public void testSongList() throws Exception {
        List<Song> songList = FileManipulator.songList(new File("src/test/resources/"));
        assertNotNull(songList);
        assertEquals(3, songList.size());
        System.out.println("testSongList created a song list with the following contents: ");
        songList.forEach(System.out::println);
    }

}
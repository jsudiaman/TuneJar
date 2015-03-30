package viewcontroller;

import junit.framework.TestCase;
import model.Song;
import org.junit.Test;

import java.io.File;
import java.util.List;

public class FileManipulatorTest extends TestCase {

    @Test
    public void testMp3Set() throws Exception {
        List<Song> songList = FileManipulator.mp3List(new File("src/test/resources/"));
        assertNotNull(songList);
        assertEquals(3, songList.size());
        System.out.println("testMp3Set created a song list with the following contents: ");
        songList.forEach(System.out::println);
    }

}
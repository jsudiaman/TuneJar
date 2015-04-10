package model;

import org.junit.Test;

import java.io.File;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class FileManipulatorTest {

    @Test
    public void testSongSet() throws Exception {
        Set<Song> songSet = FileManipulator.songSet(new File("src/test/resources/"));
        assertNotNull(songSet);
        assertEquals(3, songSet.size());
        System.out.println("testSongSet created a song set with the following contents: ");
        songSet.forEach(System.out::println);
    }

}
package model;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PlaylistTest {

    Playlist testPlaylist;

    @Before
    public void setUp() throws Exception {
        testPlaylist = new Playlist();
        testPlaylist.addAll(FileManipulator.songSet(new File("src/test/resources")));
    }

    @After
    public void tearDown() throws Exception {
        assertTrue(new File("Untitled.m3u").delete());
    }

    @Test
    public void testSaveAsM3U() throws Exception {
        testPlaylist.save();
        BufferedReader reader = new BufferedReader(new FileReader("Untitled.m3u"));
        int i = 0;
        for (String nextLine; (nextLine = reader.readLine()) != null; ) {
            assertTrue(nextLine.endsWith(".mp3"));
            i++;
        }
        assertEquals(3, i);
        reader.close();
    }

}
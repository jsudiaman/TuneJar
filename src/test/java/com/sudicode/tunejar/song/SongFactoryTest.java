package com.sudicode.tunejar.song;

import static org.junit.Assert.*;

import org.junit.Test;

import java.io.File;

public class SongFactoryTest {

    @Test
    public void testCreate() {
        File mp3File = new File("src/test/resources/mp3/AfterDark.mp3");
        File mp4File = new File("src/test/resources/mp4/CrunkKnight.m4a");
        File wavFile = new File("src/test/resources/wav/Cute.wav");
        File directory = new File("src/test/resources/");
        File nonSongFile = new File("src/test/resources/README.md");

        assertTrue(SongFactory.create(mp3File) instanceof Mp3Song);
        assertTrue(SongFactory.create(mp4File) instanceof Mp4Song);
        assertTrue(SongFactory.create(wavFile) instanceof WavSong);
        assertNull(SongFactory.create(directory));
        assertNull(SongFactory.create(nonSongFile));
    }

}

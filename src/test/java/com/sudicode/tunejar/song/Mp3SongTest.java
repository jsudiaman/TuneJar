package com.sudicode.tunejar.song;

public class Mp3SongTest extends SongTest {

    @Override
    public String getSongFile() {
        return getClass().getResource("/mp3/AfterDark.mp3").getFile();
    }

}

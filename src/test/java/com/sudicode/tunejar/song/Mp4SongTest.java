package com.sudicode.tunejar.song;

public class Mp4SongTest extends SongTest {

    @Override
    public String getSongFile() {
        return getClass().getResource("/mp4/CrunkKnight.m4a").getFile();
    }

}

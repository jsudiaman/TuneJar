package com.sudicode.tunejar.song;

import com.sudicode.tunejar.config.Defaults;

public class Mp4SongTest extends SongTest {

    @Override
    public String getSongFile() {
        return getClass().getResource(Defaults.TEST_MP4).getFile();
    }

}

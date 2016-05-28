package com.sudicode.tunejar.song;

import java.io.File;

/**
 * Currently does not support reading or writing of metadata.
 */
public class WavSong extends Song {

    public WavSong(File wavFile) {
        audioFile = wavFile;
    }

    public WavSong(WavSong wavSong) {
        this(wavSong.audioFile);
    }

    @Override
    public boolean canEdit() {
        return false;
    }

    @Override
    public void setTitle(String title) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setArtist(String artist) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setAlbum(String album) {
        throw new UnsupportedOperationException();
    }

}

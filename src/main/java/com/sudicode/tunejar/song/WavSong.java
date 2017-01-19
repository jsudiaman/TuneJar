package com.sudicode.tunejar.song;

import java.io.File;

/**
 * WAV file. Currently does not support reading or writing of metadata.
 */
public final class WavSong extends Song {

    private final File audioFile;

    /**
     * Constructor.
     *
     * @param wavFile The WAV (.wav) file to use.
     */
    WavSong(final File wavFile) {
        audioFile = wavFile;
    }

    /**
     * Constructor.
     *
     * @param wavSong The {@link WavSong} to copy.
     */
    WavSong(final WavSong wavSong) {
        this(wavSong.audioFile);
    }

    @Override
    public boolean canEdit() {
        return false;
    }

    @Override
    public void setTitle(final String title) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setArtist(final String artist) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setAlbum(final String album) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected File getAudioFile() {
        return audioFile;
    }

}

package model;

import java.io.File;
import java.util.ArrayList;

/**
 * An ordered collection of Song objects.
 */
public class Playlist extends ArrayList<Song> {

    private int currentSongIndex;
    private String name;

    public Playlist() {
        super();
        currentSongIndex = 0;
    }

    public void play() {
        // TODO Playlist::play() not yet implemented
    }

    public void pause() {
        // TODO Playlist::pause() not yet implemented
    }

    public void stop() {
        // TODO Playlist::stop() not yet implemented
    }

    public void playPrevSong() {
        // TODO Playlist::playPrevSong() not yet implemented
    }

    public void playNextSong() {
        // TODO Playlist::playNextSong() not yet implemented
    }

    public void setCurrentSongIndex(int index) {
        currentSongIndex = index;
    }

    /*
     * Good reference for the following two methods:
     * http://support.microsoft.com/en-us/kb/249234
     */
    public void loadInM3U(File m3uFile) {
        // TODO Playlist::loadInM3U() not yet implemented
    }

    public void saveAsM3U() {
        // TODO Playlist::saveAsM3U() not yet implemented
    }

}

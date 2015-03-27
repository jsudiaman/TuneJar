package model;

import javafx.beans.property.SimpleStringProperty;

import java.io.File;

public class Song {

    private File songFile;

    private SimpleStringProperty title;
    private SimpleStringProperty artist;
    private SimpleStringProperty album;

    /**
     * Creates a new song by extracting metadata from the specified file.
     *
     * @param songFile The mp3 file containing the song
     */
    public Song(File songFile) {
        // TODO Implement this constructor
    }

    /**
     * Creates a new song.
     *
     * @param title
     * @param artist
     * @param album
     */
    public Song(String title, String artist, String album) {
        this.title = new SimpleStringProperty(title);
        this.artist = new SimpleStringProperty(artist);
        this.album = new SimpleStringProperty(album);
    }

    // ------------------- Getters and Setters ------------------- //

    public String getAlbum() {
        return album.get();
    }

    public void setAlbum(String album) {
        this.album.set(album);
    }

    public String getArtist() {
        return artist.get();
    }

    public void setArtist(String artist) {
        this.artist.set(artist);
    }

    public String getTitle() {
        return title.get();
    }

    public void setTitle(String title) {
        this.title.set(title);
    }

    // ---------------- End Getters and Setters ------------------ //

}

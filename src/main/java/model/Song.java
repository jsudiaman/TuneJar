package model;

import com.mpatric.mp3agic.ID3v1;
import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.Mp3File;
import javafx.beans.property.SimpleStringProperty;
import viewcontroller.MainView;

public class Song {

    // Assign ID3 tag versions to ints
    public final static int ID3_V1 = 1;
    public final static int ID3_V2 = 2;
    public final static int CUSTOM_TAG = 3;

    // Instance members
    private SimpleStringProperty title;
    private SimpleStringProperty artist;
    private SimpleStringProperty album;
    private int ID3TagVersion;
    private Mp3File mp3file;

    /**
     * Creates a new song by extracting metadata from the specified file.
     *
     * @param mp3file The mp3 file containing the song
     */
    public Song(Mp3File mp3file) {
        // Find out which version of ID3 tag is used by the MP3.
        if (mp3file.hasId3v1Tag()) ID3TagVersion = ID3_V1;
        else if (mp3file.hasId3v2Tag()) ID3TagVersion = ID3_V2;
        else ID3TagVersion = CUSTOM_TAG;

        // Read metadata by extracting its tags.
        if (ID3TagVersion == ID3_V1) {
            ID3v1 id3v1tag = mp3file.getId3v1Tag();
            title = new SimpleStringProperty(id3v1tag.getTitle());
            artist = new SimpleStringProperty(id3v1tag.getArtist());
            album = new SimpleStringProperty(id3v1tag.getAlbum());
        } else if (ID3TagVersion == ID3_V2) {
            ID3v2 id3v2tag = mp3file.getId3v2Tag();
            title = new SimpleStringProperty(id3v2tag.getTitle());
            artist = new SimpleStringProperty(id3v2tag.getArtist());
            album = new SimpleStringProperty(id3v2tag.getAlbum());
        } else {
            title = new SimpleStringProperty(mp3file.getFilename());
            artist = new SimpleStringProperty("");
            album = new SimpleStringProperty("");
        }
        this.mp3file = mp3file;
    }

    // ------------------- Getters and Setters ------------------- //
    // TODO Setters should change the ID3 tags if able
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

    public void play() {
        if(MainView.player != null) MainView.player.stop();
        MainView.loadMP3(mp3file);
        MainView.player.play();
    }

    public void pause() {
        // TODO Not yet implemented
    }

    public void stop() {
        // TODO Not yet implemented
    }

    @Override
    public String toString() {
        // TODO Should return a more meaningful string
        return mp3file.getFilename();
    }
}

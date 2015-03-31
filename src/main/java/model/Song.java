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
        if (mp3file.hasId3v2Tag()) ID3TagVersion = ID3_V2;
        else if (mp3file.hasId3v1Tag()) ID3TagVersion = ID3_V1;
        else ID3TagVersion = CUSTOM_TAG;

        // Read metadata by extracting its tags.
        if (ID3TagVersion == ID3_V2) {
            ID3v2 id3v2tag = mp3file.getId3v2Tag();
            title = new SimpleStringProperty(id3v2tag.getTitle());
            artist = new SimpleStringProperty(id3v2tag.getArtist());
            album = new SimpleStringProperty(id3v2tag.getAlbum());
        } else if (ID3TagVersion == ID3_V1) {
            if (mp3file.getFilename().endsWith("Keep up.mp3")) System.out.println("Lock 2 achieved");
            ID3v1 id3v1tag = mp3file.getId3v1Tag();
            title = new SimpleStringProperty(id3v1tag.getTitle());
            artist = new SimpleStringProperty(id3v1tag.getArtist());
            album = new SimpleStringProperty(id3v1tag.getAlbum());
        } else {
            if (mp3file.getFilename().endsWith("Keep up.mp3")) System.out.println("Lock 3 achieved");
            title = new SimpleStringProperty(mp3file.getFilename());
            artist = new SimpleStringProperty("");
            album = new SimpleStringProperty("");
        }
        this.mp3file = mp3file;
        nullFix();
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
        if (MainView.player != null) MainView.player.stop();
        MainView.loadMP3(mp3file);
        MainView.player.play();
    }

    public void pause() {
        // TODO Not yet implemented
    }

    public void stop() {
        // TODO Not yet implemented
    }

    /**
     * Corrects null title, artist, and/or album values.
     */
    public void nullFix() {
        if (title.get() == null) {
            title = new SimpleStringProperty(mp3file.getFilename());
        }
        if (artist.get() == null) {
            artist = new SimpleStringProperty("Unknown");
        }
        if (album.get() == null) {
            album = new SimpleStringProperty("Unknown");
        }
    }

    @Override
    public String toString() {
        return "\"" + title.get() + "\" - \"" + artist.get() + "\" - \"" + album.get() + "\"";
    }

}

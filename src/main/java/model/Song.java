package model;

import com.mpatric.mp3agic.*;
import javafx.beans.property.SimpleStringProperty;
import viewcontroller.MainView;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

/**
 * Helpful documentation for the MP3agic library: https://github.com/mpatric/mp3agic
 */
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
            ID3v1 id3v1tag = mp3file.getId3v1Tag();
            title = new SimpleStringProperty(id3v1tag.getTitle());
            artist = new SimpleStringProperty(id3v1tag.getArtist());
            album = new SimpleStringProperty(id3v1tag.getAlbum());
        } else {
            title = new SimpleStringProperty(mp3file.getFilename());
            artist = new SimpleStringProperty("?");
            album = new SimpleStringProperty("?");
        }
        this.mp3file = mp3file;

        // Correct null title, artist, and/or album values.
        if (title.get() == null) title = new SimpleStringProperty(mp3file.getFilename());
        if (artist.get() == null) artist = new SimpleStringProperty("?");
        if (album.get() == null) album = new SimpleStringProperty("?");
    }

    // ------------------- Getters and Setters ------------------- //
    public String getAlbum() {
        return album.get();
    }

    /**
     * Renames the album. Note that changes will not be reflected in the
     * MP3 itself unless Song::save() is called.
     *
     * @param album New album name
     */
    public void setAlbum(String album) {
        this.album.set(album);
        switch (ID3TagVersion) {
            case ID3_V2:
                mp3file.getId3v2Tag().setAlbum(album);
                break;
            case ID3_V1:
                mp3file.getId3v1Tag().setAlbum(album);
                break;
            default:
                ID3v2 tag = new ID3v24Tag();
                mp3file.setId3v2Tag(tag);
                tag.setAlbum(album);
                break;
        }
    }

    public String getArtist() { 	
        return artist.get();
    }

    public void setArtist(String artist) {
        this.artist.set(artist);
        switch (ID3TagVersion) {
            case ID3_V2:
                mp3file.getId3v2Tag().setArtist(artist);
                break;
            case ID3_V1:
                mp3file.getId3v1Tag().setArtist(artist);
                break;
            default:
                ID3v2 tag = new ID3v24Tag();
                mp3file.setId3v2Tag(tag);
                tag.setArtist(artist);
                break;
        }
    }

    public String getTitle() {
        return title.get();
    }

    public void setTitle(String title) {
        this.title.set(title);
        switch (ID3TagVersion) {
            case ID3_V2:
                mp3file.getId3v2Tag().setTitle(title);
                break;
            case ID3_V1:
                mp3file.getId3v1Tag().setTitle(title);
                break;
            default:
                ID3v2 tag = new ID3v24Tag();
                mp3file.setId3v2Tag(tag);
                tag.setTitle(title);
                break;
        }
    }
    public String getFilename(){
    	return mp3file.getFilename();
    }

    // ---------------- Media Control ------------------ //

    public void play() {
        MainView.stopPlayback();
        MainView.playMP3(mp3file);
    }

    public void pause() {
        // TODO The song should be playing in order to gain this privilege
        MainView.pausePlayback();
    }

    public void stop() {
        // TODO The song should be playing in order to gain this privilege
        MainView.stopPlayback();
    }

    // ---------------- Utilities ------------------ //

    /**
     * A string representation of the song object.
     *
     * @return "Song Title - Artist"
     */
    @Override
    public String toString() {
        return title.get() + " - " + artist.get();
    }

    /**
     * Saves changes to the MP3 file.
     */
    public void save() throws IOException, NotSupportedException, InvalidDataException, UnsupportedTagException {
        String fileName = mp3file.getFilename();
        mp3file.save(fileName + ".tmp"); // Save the new file by appending ".tmp"

        if(!new File(fileName).delete()) { // Delete the old file
            MainView.logger.log(Level.SEVERE, "Failed to delete file: " + fileName);
        }

        if(!new File(fileName + ".tmp").renameTo(new File(fileName))) { // Remove ".tmp" from the new file
            MainView.logger.log(Level.SEVERE, "Failed to rename file: " + fileName);
        }

        mp3file = new Mp3File(new File(fileName)); // Update the mp3file reference
    }

}

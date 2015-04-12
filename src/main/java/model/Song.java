package model;

import static model.DebugUtils.LOGGER;
import static model.DebugUtils.fatalException;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

import javafx.beans.property.SimpleStringProperty;
import viewcontroller.MainView;

import com.mpatric.mp3agic.*;
import com.sun.istack.internal.NotNull;

/**
 * Helpful documentation for the MP3agic library:
 * https://github.com/mpatric/mp3agic
 */
public class Song {

    // Assign ID3 tag versions to integers
    public final static int ID3_V1 = 1;
    public final static int ID3_V2 = 2;
    public final static int CUSTOM_TAG = 3;

    // Instance members
    private SimpleStringProperty title;
    private SimpleStringProperty artist;
    private SimpleStringProperty album;
    private final int ID3TagVersion;
    private Mp3File mp3file;
    private boolean paused;

    /**
     * Creates a new song by extracting metadata from the specified file.
     *
     * @param mp3file
     *            The mp3 file containing the song
     */
    public Song(@NotNull Mp3File mp3file) {
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
            title = new SimpleStringProperty(getFilename());
            artist = new SimpleStringProperty("");
            album = new SimpleStringProperty("");
        }
        this.mp3file = mp3file;

        // Correct null title, artist, and/or album values.
        if (title.get() == null) title = new SimpleStringProperty(getFilename());
        if (artist.get() == null) artist = new SimpleStringProperty("");
        if (album.get() == null) album = new SimpleStringProperty("");

        paused = false;
    }

    // ------------------- Getters and Setters ------------------- //

    public String getTitle() {
        return title.get();
    }

    public String getArtist() {
        return artist.get();
    }

    public String getAlbum() {
        return album.get();
    }

    /**
     * Alters the ID3 tag of the song, or creates a new one if it does not
     * exist.
     *
     * @param newTitle
     *            New title
     * @param newArtist
     *            New artist
     * @param newAlbum
     *            New album
     *
     * @return True iff changes to the song were saved successfully.
     */
    public boolean setTag(String newTitle, String newArtist, String newAlbum) {
        // Remove leading and trailing whitespace
        newTitle = newTitle.trim();
        newArtist = newArtist.trim();
        newAlbum = newAlbum.trim();

        // Replace empty parameters with the old ones
        if (newTitle.equals("")) newTitle = getTitle();
        if (newArtist.equals("")) newArtist = getArtist();
        if (newAlbum.equals("")) newAlbum = getAlbum();

        // Set the instance members
        this.title.set(newTitle);
        this.artist.set(newArtist);
        this.album.set(newAlbum);

        // Change the tag data
        switch (ID3TagVersion) {
        case ID3_V2:
            ID3v2 ID3v2Tag = mp3file.getId3v2Tag();
            ID3v2Tag.setTitle(newTitle);
            ID3v2Tag.setArtist(newArtist);
            ID3v2Tag.setAlbum(newAlbum);
            break;
        case ID3_V1:
            ID3v1 ID3v1Tag = mp3file.getId3v1Tag();
            ID3v1Tag.setTitle(newTitle);
            ID3v1Tag.setArtist(newArtist);
            ID3v1Tag.setAlbum(newAlbum);
            break;
        default:
            ID3v2 tag = new ID3v24Tag();
            mp3file.setId3v2Tag(tag);
            tag.setTitle(newTitle);
            tag.setArtist(newArtist);
            tag.setAlbum(newAlbum);
            break;
        }

        // Save changes to the mp3 file
        try {
            return save();
        } catch (IOException | NotSupportedException | UnsupportedTagException | InvalidDataException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            fatalException(e);
            return false;
        }
    }

    /**
     * Finds the file name of the MP3 without any directory information. Ex. If
     * the MP3 is located in 'C:\Users\Joe\Music\B.mp3', 'B' will be returned.
     *
     * @return The file name
     */
    public String getFilename() {
        String filename = new File(mp3file.getFilename()).getName();
        filename = filename.substring(0, filename.indexOf(".mp3"));
        return filename;
    }

    /**
     * Finds the absolute path of the MP3. Ex. If the MP3 is located in
     * 'C:\Users\Joe\Music\B.mp3', that entire string will be returned.
     *
     * @return The absolute path
     */
    public String getAbsoluteFilename() {
        File file = new File(mp3file.getFilename());
        return file.getAbsolutePath();
    }

    // ---------------- Media Control ------------------ //

    public void play(double volume) {
        if (paused) {
            paused = false;
            MainView.resumePlayback();
        } else {
            MainView.playSong(this, volume);
        }
    }

    public void pause() {
        paused = true;
        MainView.pausePlayback();
    }

    public void stop() {
        paused = false;
        MainView.stopPlayback();
    }

    // ---------------- Saving ------------------ //

    /**
     * Saves changes to the MP3 file.
     *
     * @return True iff changes to the song were saved successfully.
     *
     * @throws IOException
     * @throws NotSupportedException
     * @throws InvalidDataException
     * @throws UnsupportedTagException
     */
    private boolean save() throws IOException, NotSupportedException, InvalidDataException, UnsupportedTagException {
        boolean successful = true;
        String fileName = mp3file.getFilename();
        mp3file.save(fileName + ".tmp"); // Save the new file by appending ".tmp"

        if (!new File(fileName).delete()) { // Delete the old file
            LOGGER.log(Level.SEVERE, "Failed to delete file: " + fileName);
            successful = false;
        }

        if (!new File(fileName + ".tmp").renameTo(new File(fileName))) { // Remove ".tmp" from the new file
            LOGGER.log(Level.SEVERE, "Failed to rename file: " + fileName + ".tmp");
            successful = false;
        }

        mp3file = new Mp3File(new File(fileName)); // Update the mp3file reference
        return successful;
    }

    // ---------------- Overriding Methods ------------------ //

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
     * A song object is equal to another object iff the following is true:
     * <ul>
     * <li>The other object is also a Song.</li>
     * <li>The two songs have the same title, artist, and album.</li>
     * </ul>
     *
     * @param otherObject
     *            Another object
     * @return True iff this object is equal to otherObject
     */
    @Override
    public boolean equals(Object otherObject) {
        if (otherObject instanceof Song) {
            Song otherSong = (Song) otherObject;
            return this.getTitle().equals(otherSong.getTitle()) && this.getArtist().equals(otherSong.getArtist())
                    && this.getAlbum().equals(otherSong.getAlbum());
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return getTitle().hashCode() + getArtist().hashCode() + getAlbum().hashCode();
    }

}

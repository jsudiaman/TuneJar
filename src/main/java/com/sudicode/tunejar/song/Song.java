package com.sudicode.tunejar.song;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.CannotWriteException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

import java.io.File;
import java.io.IOException;

import javafx.beans.property.SimpleStringProperty;

public abstract class Song {

    private static final Logger logger = LoggerFactory.getLogger(Song.class);

    protected SimpleStringProperty title;
    protected SimpleStringProperty artist;
    protected SimpleStringProperty album;

    // Redirect JAudioTagger's JUL to TuneJar's SLF4J
    static {
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();
    }

    /**
     * <p>
     * Constructs a new Song.
     * </p>
     */
    protected Song() {
        title = new SimpleStringProperty("");
        artist = new SimpleStringProperty("");
        album = new SimpleStringProperty("");
    }

    /**
     * @return The audio file.
     */
    protected abstract File getAudioFile();

    /**
     * Finds the absolute path of the audio file. For example, if the audio file
     * is located in 'C:\Users\JohnDoe\Music\B.mp3', then that entire string
     * will be returned.
     *
     * @return The absolute path
     */
    public String getAbsoluteFilename() {
        if (getAudioFile() == null) {
            return "null";
        }

        return getAudioFile().getAbsolutePath();
    }

    /**
     * Checks if the song can be edited. Please note this implies that
     * <b>all</b> fields are editable - Title, Artist, and Album. It also
     * implies that changes can be made to the audio file itself (not just this
     * object). If such an operation is unsupported, it is acceptable to
     * hard-code this method to return <code>false</code>. In that case, setters
     * should also throw an {@link UnsupportedOperationException}, since setters
     * should <b>not</b> be functional if this method returns false.
     *
     * @return True if the song can be edited.
     */
    public boolean canEdit() {
        try {
            setTitle(getTitle());
            setArtist(getArtist());
            setAlbum(getAlbum());
            return true;
        } catch (CannotReadException | IOException | TagException | ReadOnlyFileException | InvalidAudioFrameException
                | CannotWriteException e) {
            logger.error("Unable to edit song: " + toString(), e);
            return false;
        }
    }

    /**
     * Sets the title in both this object and the audio file.
     */
    public void setTitle(String title) throws CannotReadException, IOException, TagException, ReadOnlyFileException,
            InvalidAudioFrameException, CannotWriteException {
        AudioFile f = AudioFileIO.read(getAudioFile());
        Tag tag = f.getTag();
        tag.setField(FieldKey.TITLE, title);
        f.commit();
        this.title.set(title);
    }

    public String getTitle() {
        if (title.get().equals("")) {
            return new SimpleStringProperty(getAudioFile().getName()).get();
        } else {
            return title.get();
        }
    }

    /**
     * Sets the artist in both this object and the audio file.
     */
    public void setArtist(String artist) throws CannotReadException, IOException, TagException, ReadOnlyFileException,
            InvalidAudioFrameException, CannotWriteException {
        AudioFile f = AudioFileIO.read(getAudioFile());
        Tag tag = f.getTag();
        tag.setField(FieldKey.ARTIST, artist);
        f.commit();
        this.artist.set(artist);
    }

    public String getArtist() {
        return artist.get();
    }

    /**
     * Sets the album in both this object and the audio file.
     */
    public void setAlbum(String album) throws CannotReadException, IOException, TagException, ReadOnlyFileException,
            InvalidAudioFrameException, CannotWriteException {
        AudioFile f = AudioFileIO.read(getAudioFile());
        Tag tag = f.getTag();
        tag.setField(FieldKey.ALBUM, album);
        f.commit();
        this.album.set(album);
    }

    public String getAlbum() {
        return album.get();
    }

    /**
     * A string representation of the song object.
     *
     * @return Title - Artist
     */
    public String toString() {
        if (getArtist().equals(""))
            return getTitle();
        else
            return getTitle() + " - " + getArtist();
    }

}

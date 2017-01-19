package com.sudicode.tunejar.song;

import com.sudicode.tunejar.TuneJarException;
import javafx.beans.property.SimpleStringProperty;
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

/**
 * Represents a single audio file that is compatible with TuneJar. Subclasses must override <code>getAudioFile()</code>
 * which will return the file path.
 */
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
     * Constructs a new Song.
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
        } catch (TuneJarException e) {
            logger.error("Unable to edit song: " + toString(), e);
            return false;
        }
    }

    /**
     * Sets the title in both this object and the audio file.
     *
     * @param title The title
     * @throws TuneJarException if an error occurs.
     */
    public void setTitle(final String title) throws TuneJarException {
        try {
            AudioFile f = AudioFileIO.read(getAudioFile());
            Tag tag = f.getTag();
            tag.setField(FieldKey.TITLE, title);
            f.commit();
            this.title.set(title);
        } catch (IOException | CannotReadException | TagException | ReadOnlyFileException
                | InvalidAudioFrameException | CannotWriteException e) {
            throw new TuneJarException(e);
        }
    }

    /**
     * @return The title of the song.
     */
    public String getTitle() {
        if ("".equals(title.get())) {
            return new SimpleStringProperty(getAudioFile().getName()).get();
        } else {
            return title.get();
        }
    }

    /**
     * Sets the artist in both this object and the audio file.
     *
     * @param artist The artist
     * @throws TuneJarException if an error occurs.
     */
    public void setArtist(final String artist) throws TuneJarException {
        try {
            AudioFile f = AudioFileIO.read(getAudioFile());
            Tag tag = f.getTag();
            tag.setField(FieldKey.ARTIST, artist);
            f.commit();
            this.artist.set(artist);
        } catch (IOException | CannotReadException | TagException | ReadOnlyFileException
                | InvalidAudioFrameException | CannotWriteException e) {
            throw new TuneJarException(e);
        }
    }

    /**
     * @return The artist of the song.
     */
    public String getArtist() {
        return artist.get();
    }

    /**
     * Sets the album in both this object and the audio file.
     *
     * @param album The album
     * @throws TuneJarException if an error occurs.
     */
    public void setAlbum(final String album) throws TuneJarException {
        try {
            AudioFile f = AudioFileIO.read(getAudioFile());
            Tag tag = f.getTag();
            tag.setField(FieldKey.ALBUM, album);
            f.commit();
            this.album.set(album);
        } catch (IOException | CannotReadException | TagException | ReadOnlyFileException
                | InvalidAudioFrameException | CannotWriteException e) {
            throw new TuneJarException(e);
        }
    }

    /**
     * @return The album of the song.
     */
    public String getAlbum() {
        return album.get();
    }

    /**
     * A string representation of the song object.
     *
     * @return Title - Artist
     */
    public String toString() {
        if ("".equals(getArtist())) {
            return getTitle();
        } else {
            return getTitle() + " - " + getArtist();
        }
    }

}

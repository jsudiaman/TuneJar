package com.sudicode.tunejar.song;

import com.sudicode.tunejar.TuneJarException;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.CannotWriteException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.TagException;
import org.jaudiotagger.tag.mp4.Mp4FieldKey;
import org.jaudiotagger.tag.mp4.Mp4Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

/**
 * MP4 file.
 */
public final class Mp4Song extends Song {

    private static final Logger logger = LoggerFactory.getLogger(Mp4Song.class);

    private final File audioFile;

    /**
     * Constructor.
     *
     * @param mp4File The MP4 (.mp4/.m4a) file to use.
     */
    Mp4Song(final File mp4File) {
        audioFile = mp4File;

        try {
            AudioFile f = AudioFileIO.read(audioFile);

            // Parse metadata
            Mp4Tag tag = (Mp4Tag) f.getTag();
            title.set(tag.getFirst(Mp4FieldKey.TITLE));
            artist.set(tag.getFirst(Mp4FieldKey.ARTIST));
            album.set(tag.getFirst(Mp4FieldKey.ALBUM));
        } catch (IOException | CannotReadException | InvalidAudioFrameException | ReadOnlyFileException
                | TagException e) {
            logger.error("Unable to parse: " + mp4File, e);
        }
    }

    /**
     * Constructor.
     *
     * @param mp4Song The {@link Mp4Song} to copy.
     */
    Mp4Song(final Mp4Song mp4Song) {
        this(mp4Song.audioFile);
    }

    @Override
    public void setTitle(final String title) throws TuneJarException {
        try {
            AudioFile f = AudioFileIO.read(audioFile);
            Mp4Tag tag = (Mp4Tag) f.getTag();
            tag.setField(tag.createField(Mp4FieldKey.TITLE, title));
            f.commit();
            this.title.set(title);
        } catch (IOException | CannotReadException | InvalidAudioFrameException | ReadOnlyFileException
                | TagException | CannotWriteException e) {
            throw new TuneJarException(e);
        }
    }

    @Override
    public void setArtist(final String artist) throws TuneJarException {
        try {
            AudioFile f = AudioFileIO.read(audioFile);
            Mp4Tag tag = (Mp4Tag) f.getTag();
            tag.setField(tag.createField(Mp4FieldKey.ARTIST, artist));
            f.commit();
            this.artist.set(artist);
        } catch (IOException | CannotReadException | InvalidAudioFrameException | ReadOnlyFileException
                | TagException | CannotWriteException e) {
            throw new TuneJarException(e);
        }
    }

    @Override
    public void setAlbum(final String album) throws TuneJarException {
        try {
            AudioFile f = AudioFileIO.read(audioFile);
            Mp4Tag tag = (Mp4Tag) f.getTag();
            tag.setField(tag.createField(Mp4FieldKey.ALBUM, album));
            f.commit();
            this.album.set(album);
        } catch (IOException | CannotReadException | InvalidAudioFrameException | ReadOnlyFileException
                | TagException | CannotWriteException e) {
            throw new TuneJarException(e);
        }
    }

    @Override
    protected File getAudioFile() {
        return audioFile;
    }

}

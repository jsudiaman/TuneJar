package com.sudicode.tunejar.song;

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

public class Mp4Song extends Song {

    private static final Logger logger = LoggerFactory.getLogger(Mp4Song.class);

    public Mp4Song(File mp4File) {
        audioFile = mp4File;

        try {
            AudioFile f = AudioFileIO.read(audioFile);

            // Parse metadata
            Mp4Tag tag = (Mp4Tag) f.getTag();
            title.set(tag.getFirst(Mp4FieldKey.TITLE));
            artist.set(tag.getFirst(Mp4FieldKey.ARTIST));
            album.set(tag.getFirst(Mp4FieldKey.ALBUM));
        } catch (Exception e) {
            logger.error("Unable to parse: " + mp4File, e);
        }
    }

    public Mp4Song(Mp4Song mp4Song) {
        this(mp4Song.audioFile);
    }

    @Override
    public void setTitle(String title) throws CannotReadException, IOException, TagException, ReadOnlyFileException,
            InvalidAudioFrameException, CannotWriteException {
        AudioFile f = AudioFileIO.read(audioFile);
        Mp4Tag tag = (Mp4Tag) f.getTag();
        tag.setField(tag.createField(Mp4FieldKey.TITLE, title));
        f.commit();
        this.title.set(title);
    }

    @Override
    public void setArtist(String artist) throws CannotReadException, IOException, TagException, ReadOnlyFileException,
            InvalidAudioFrameException, CannotWriteException {
        AudioFile f = AudioFileIO.read(audioFile);
        Mp4Tag tag = (Mp4Tag) f.getTag();
        tag.setField(tag.createField(Mp4FieldKey.ARTIST, artist));
        f.commit();
        this.artist.set(artist);
    }

    @Override
    public void setAlbum(String album) throws CannotReadException, IOException, TagException, ReadOnlyFileException,
            InvalidAudioFrameException, CannotWriteException {
        AudioFile f = AudioFileIO.read(audioFile);
        Mp4Tag tag = (Mp4Tag) f.getTag();
        tag.setField(tag.createField(Mp4FieldKey.ALBUM, album));
        f.commit();
        this.album.set(album);
    }

}

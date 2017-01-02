package com.sudicode.tunejar.song;

import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.TagException;
import org.jaudiotagger.tag.id3.ID3v1Tag;
import org.jaudiotagger.tag.id3.ID3v24Frames;
import org.jaudiotagger.tag.id3.ID3v24Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public class Mp3Song extends Song {

    private static final Logger logger = LoggerFactory.getLogger(Mp3Song.class);

    private final File audioFile;

    public Mp3Song(File mp3File) {
        audioFile = mp3File;

        try {
            MP3File f = (MP3File) AudioFileIO.read(audioFile);

            // Parse metadata
            if (f.hasID3v2Tag()) {
                ID3v24Tag tag = f.getID3v2TagAsv24();
                title.set(tag.getFirst(ID3v24Frames.FRAME_ID_TITLE));
                artist.set(tag.getFirst(ID3v24Frames.FRAME_ID_ARTIST));
                album.set(tag.getFirst(ID3v24Frames.FRAME_ID_ALBUM));
            } else if (f.hasID3v1Tag()) {
                ID3v1Tag tag = f.getID3v1Tag();
                title.set(tag.getFirst(FieldKey.TITLE));
                artist.set(tag.getFirst(FieldKey.ARTIST));
                album.set(tag.getFirst(FieldKey.ALBUM));
            }
        } catch (CannotReadException | IOException | TagException | ReadOnlyFileException
                | InvalidAudioFrameException e) {
            logger.error("Unable to parse: " + mp3File, e);
        }
    }

    public Mp3Song(Mp3Song mp3Song) {
        this(mp3Song.audioFile);
    }

    @Override
    protected File getAudioFile() {
        return audioFile;
    }

}

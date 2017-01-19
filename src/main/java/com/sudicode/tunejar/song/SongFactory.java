package com.sudicode.tunejar.song;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * Creates {@link Song} instances from audio files or other {@link Song Songs}.
 */
public class SongFactory {

    private static final Logger logger = LoggerFactory.getLogger(SongFactory.class);

    /**
     * Illegal.
     */
    private SongFactory() {
    }

    /**
     * Constructs a {@link Song} out of a file.
     *
     * @param file The file to be used.
     * @return The constructed {@link Song}
     * @throws IllegalArgumentException if the file type is not supported
     */
    public static Song create(final File file) {
        if (file.getName().endsWith(".mp3")) {
            logger.debug("From file: " + file);
            return new Mp3Song(file);
        } else if (file.getName().endsWith(".mp4") || file.getName().endsWith(".m4a")) {
            logger.debug("From file: " + file);
            return new Mp4Song(file);
        } else if (file.getName().endsWith(".wav")) {
            logger.debug("From file: " + file);
            return new WavSong(file);
        }

        throw new IllegalArgumentException("Unsupported file type: " + FilenameUtils.getExtension(file.getName()));
    }

    /**
     * Duplicates a {@link Song} by using its copy constructor.
     *
     * @param song The song to be used.
     * @return The duplicate {@link Song}.
     */
    public static Song duplicate(final Song song) {
        if (song instanceof Mp3Song) {
            return new Mp3Song((Mp3Song) song);
        } else if (song instanceof Mp4Song) {
            return new Mp4Song((Mp4Song) song);
        } else if (song instanceof WavSong) {
            return new WavSong((WavSong) song);
        }

        throw new IllegalArgumentException("Cannot duplicate instance of " + song.getClass());
    }

}

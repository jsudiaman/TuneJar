/*
 * TuneJar <http://sudicode.com/tunejar/>
 * Copyright (C) 2016 Jonathan Sudiaman
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.sudicode.tunejar.song;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class Songs {

    private static final Logger logger = LoggerFactory.getLogger(Songs.class);

    private Songs() {}

    /**
     * Constructs a {@link Song} out of a file.
     *
     * @param file The file to be used.
     * @return The constructed {@link Song}, or <code>null</code> if the file type is not supported.
     */
    public static Song create(File file) {
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

        // Unsupported file type.
        return null;
    }

    /**
     * Duplicates a {@link Song} by using its copy constructor.
     *
     * @param song The song to be used.
     * @return The duplicate {@link Song}.
     */
    public static Song duplicate(Song song) {
        if (song instanceof Mp3Song) {
            return new Mp3Song((Mp3Song) song);
        } else if (song instanceof Mp4Song) {
            return new Mp4Song((Mp4Song) song);
        } else if (song instanceof WavSong) {
            return new WavSong((WavSong) song);
        }

        // All implementations of Song should have a copy constructor.
        throw new AssertionError();
    }

}

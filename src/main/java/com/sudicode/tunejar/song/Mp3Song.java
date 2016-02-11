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

import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.id3.ID3v1Tag;
import org.jaudiotagger.tag.id3.ID3v24Frames;
import org.jaudiotagger.tag.id3.ID3v24Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class Mp3Song extends Song {

    private static final Logger logger = LoggerFactory.getLogger(Mp3Song.class);

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
        } catch (Exception e) {
            logger.error("Unable to parse: " + mp3File, e);
        }
    }

    public Mp3Song(Mp3Song mp3Song) {
        this(mp3Song.audioFile);
    }

}

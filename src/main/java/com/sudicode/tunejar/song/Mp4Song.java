// @formatter:off
/*
 * TuneJar <http://sudicode.com/tunejar/>
 * Copyright (C) 2016 Jonathan Sudiaman
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
// @formatter:on

package com.sudicode.tunejar.song;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.mp4.Mp4FieldKey;
import org.jaudiotagger.tag.mp4.Mp4Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

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
    public void setTitle(String title) throws Exception {
        AudioFile f = AudioFileIO.read(audioFile);
        Mp4Tag tag = (Mp4Tag) f.getTag();
        tag.setField(tag.createField(Mp4FieldKey.TITLE, title));
        f.commit();
        this.title.set(title);
    }

    @Override
    public void setArtist(String artist) throws Exception {
        AudioFile f = AudioFileIO.read(audioFile);
        Mp4Tag tag = (Mp4Tag) f.getTag();
        tag.setField(tag.createField(Mp4FieldKey.ARTIST, artist));
        f.commit();
        this.artist.set(artist);
    }

    @Override
    public void setAlbum(String album) throws Exception {
        AudioFile f = AudioFileIO.read(audioFile);
        Mp4Tag tag = (Mp4Tag) f.getTag();
        tag.setField(tag.createField(Mp4FieldKey.ALBUM, album));
        f.commit();
        this.album.set(album);
    }

}

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

import java.io.File;

/**
 * Currently does not support reading or writing of metadata.
 */
public class WavSong extends Song {

    public WavSong(File wavFile) {
        audioFile = wavFile;
    }

    public WavSong(WavSong wavSong) {
        this(wavSong.audioFile);
    }

    @Override
    public boolean canEdit() {
        return false;
    }

    @Override
    public void setTitle(String title) throws Exception {
        throw new AssertionError();
    }

    @Override
    public void setArtist(String artist) throws Exception {
        throw new AssertionError();
    }

    @Override
    public void setAlbum(String album) throws Exception {
        throw new AssertionError();
    }

}

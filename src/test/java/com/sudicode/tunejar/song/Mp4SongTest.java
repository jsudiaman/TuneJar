package com.sudicode.tunejar.song;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.CannotWriteException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.TagException;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

/** Unit test for the {@link Mp4Song} class. */
public class Mp4SongTest {

    private static final String TITLE = "Crunk Knight";
    private static final String ARTIST = "Kevin MacLeod";
    private static final String ALBUM = "Oddities";
    private static final String FILENAME = "src/test/resources/mp4/CrunkKnight.m4a";

    private Mp4Song mp4Song;

    /**
     * Instantiates {@link Mp4Song} using the {@link File} argument constructor.
     */
    @Before
    public void setUp() {
        mp4Song = new Mp4Song(new File(FILENAME));
    }

    /**
     * Tests {@link Mp4Song#getAbsoluteFilename()}. Since the true absolute path
     * is system dependent, using {@link String#endsWith(String)} is sufficient
     * for this test.
     */
    @Test
    public void testGetAbsoluteFilename() {
        assertTrue(mp4Song.getAbsoluteFilename().endsWith(FILENAME));
    }

    /**
     * MP4 files should be editable, so ensure that {@link Mp4Song#canEdit()}
     * returns true. This assumes that the file is a known "working" file.
     */
    @Test
    public void testCanEdit() {
        assertTrue(mp4Song.canEdit());
    }

    /**
     * The title should be set in both the {@link Mp4Song} object and the audio
     * file.
     */
    @Test
    public void testSetTitle() throws CannotReadException, IOException, TagException, ReadOnlyFileException,
            InvalidAudioFrameException, CannotWriteException {
        try {
            mp4Song.setTitle("New Title");
            assertEquals("New Title", mp4Song.title.get());
            assertEquals("New Title", new Mp4Song(mp4Song).title.get());
        } finally {
            mp4Song.setTitle(TITLE);
        }
    }

    /**
     * Test the getter for the title.
     */
    @Test
    public void testGetTitle() {
        assertEquals(TITLE, mp4Song.getTitle());
    }

    /**
     * The artist should be set in both the {@link Mp4Song} object and the audio
     * file.
     */
    @Test
    public void testSetArtist() throws CannotReadException, IOException, TagException, ReadOnlyFileException,
            InvalidAudioFrameException, CannotWriteException {
        try {
            mp4Song.setArtist("New Artist");
            assertEquals("New Artist", mp4Song.artist.get());
            assertEquals("New Artist", new Mp4Song(mp4Song).artist.get());
        } finally {
            mp4Song.setArtist(ARTIST);
        }
    }

    /**
     * Test the getter for the artist.
     */
    @Test
    public void testGetArtist() {
        assertEquals(ARTIST, mp4Song.getArtist());
    }

    /**
     * The album should be set in both the {@link Mp4Song} object and the audio
     * file.
     */
    @Test
    public void testSetAlbum() throws CannotReadException, IOException, TagException, ReadOnlyFileException,
            InvalidAudioFrameException, CannotWriteException {
        try {
            mp4Song.setAlbum("New Album");
            assertEquals("New Album", mp4Song.album.get());
            assertEquals("New Album", new Mp4Song(mp4Song).album.get());
        } finally {
            mp4Song.setAlbum(ALBUM);
        }
    }

    /**
     * Test the getter for the album.
     */
    @Test
    public void testGetAlbum() {
        assertEquals(ALBUM, mp4Song.getAlbum());
    }

    /**
     * The {@link String} representation of an {@link Mp4Song} object is
     * expected to be "Title - Artist".
     */
    @Test
    public void testToString() {
        assertEquals(TITLE + " - " + ARTIST, mp4Song.toString());
    }

}

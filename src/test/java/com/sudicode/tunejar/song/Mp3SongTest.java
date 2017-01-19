package com.sudicode.tunejar.song;

import com.sudicode.tunejar.TuneJarException;
import org.apache.commons.io.FilenameUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

/**
 * Unit test for the {@link Mp3Song} class.
 */
public class Mp3SongTest {

    private static final String TITLE = "After Dark";
    private static final String ARTIST = "Machinimasound";
    private static final String ALBUM = "Machinimasound 2011";
    private static final String FILENAME = "src/test/resources/mp3/AfterDark.mp3";

    private Mp3Song mp3Song;

    /**
     * Instantiates {@link Mp3Song} using the {@link File} argument constructor.
     */
    @Before
    public void setUp() {
        mp3Song = new Mp3Song(new File(FILENAME));
    }

    /**
     * Tests {@link Mp3Song#getAbsoluteFilename()}. Since the true absolute path
     * is system dependent, using {@link String#endsWith(String)} is sufficient
     * for this test.
     */
    @Test
    public void testGetAbsoluteFilename() {
        String absoluteFilename = FilenameUtils.separatorsToUnix(mp3Song.getAbsoluteFilename());
        assertTrue(absoluteFilename.endsWith(FILENAME));
    }

    /**
     * MP3 files should be editable, so ensure that {@link Mp3Song#canEdit()}
     * returns true. This assumes that the file is a known "working" file.
     */
    @Test
    public void testCanEdit() {
        assertTrue(mp3Song.canEdit());
    }

    /**
     * The title should be set in both the {@link Mp3Song} object and the audio
     * file.
     */
    @Test
    public void testSetTitle() throws TuneJarException {
        try {
            mp3Song.setTitle("New Title");
            assertEquals("New Title", mp3Song.title.get());
            assertEquals("New Title", new Mp3Song(mp3Song).title.get());
        } finally {
            mp3Song.setTitle(TITLE);
        }
    }

    /**
     * Test the getter for the title.
     */
    @Test
    public void testGetTitle() {
        assertEquals(TITLE, mp3Song.getTitle());
    }

    /**
     * The artist should be set in both the {@link Mp3Song} object and the audio
     * file.
     */
    @Test
    public void testSetArtist() throws TuneJarException {
        try {
            mp3Song.setArtist("New Artist");
            assertEquals("New Artist", mp3Song.artist.get());
            assertEquals("New Artist", new Mp3Song(mp3Song).artist.get());
        } finally {
            mp3Song.setArtist(ARTIST);
        }
    }

    /**
     * Test the getter for the artist.
     */
    @Test
    public void testGetArtist() {
        assertEquals(ARTIST, mp3Song.getArtist());
    }

    /**
     * The album should be set in both the {@link Mp3Song} object and the audio
     * file.
     */
    @Test
    public void testSetAlbum() throws TuneJarException {
        try {
            mp3Song.setAlbum("New Album");
            assertEquals("New Album", mp3Song.album.get());
            assertEquals("New Album", new Mp3Song(mp3Song).album.get());
        } finally {
            mp3Song.setAlbum(ALBUM);
        }
    }

    /**
     * Test the getter for the album.
     */
    @Test
    public void testGetAlbum() {
        assertEquals(ALBUM, mp3Song.getAlbum());
    }

    /**
     * The {@link String} representation of an {@link Mp3Song} object is
     * expected to be "Title - Artist".
     */
    @Test
    public void testToString() {
        assertEquals(TITLE + " - " + ARTIST, mp3Song.toString());
    }

}

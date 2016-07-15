package com.sudicode.tunejar.song;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.apache.commons.io.FilenameUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

/** Unit test for the {@link WavSong} class. */
public class WavSongTest {

    private static final String TITLE = "Cute.wav";
    private static final String ARTIST = "";
    private static final String ALBUM = "";
    private static final String FILENAME = "src/test/resources/wav/Cute.wav";

    private WavSong wavSong;

    /**
     * Instantiates {@link WavSong} using the {@link File} argument constructor.
     */
    @Before
    public void setUp() {
        wavSong = new WavSong(new File(FILENAME));
    }

    /**
     * Tests {@link WavSong#getAbsoluteFilename()}. Since the true absolute path
     * is system dependent, using {@link String#endsWith(String)} is sufficient
     * for this test.
     */
    @Test
    public void testGetAbsoluteFilename() {
        String absoluteFilename = FilenameUtils.separatorsToUnix(wavSong.getAbsoluteFilename());
        assertTrue(absoluteFilename.endsWith(FILENAME));
    }

    /**
     * WAV files should not be editable, so ensure that
     * {@link WavSong#canEdit()} returns false.
     */
    @Test
    public void testCanEdit() {
        assertFalse(wavSong.canEdit());
    }

    /**
     * Ensure that the setter for the title throws
     * {@link UnsupportedOperationException}.
     */
    @Test
    public void testSetTitle() {
        try {
            wavSong.setTitle("New Title");
            fail();
        } catch (UnsupportedOperationException expected) {
        }
    }

    /**
     * Test the getter for the title.
     */
    @Test
    public void testGetTitle() {
        assertEquals(TITLE, wavSong.getTitle());
    }

    /**
     * Ensure that the setter for the artist throws
     * {@link UnsupportedOperationException}.
     */
    @Test
    public void testSetArtist() {
        try {
            wavSong.setArtist("New Artist");
            fail();
        } catch (UnsupportedOperationException expected) {
        }
    }

    /**
     * Test the getter for the artist.
     */
    @Test
    public void testGetArtist() {
        assertEquals(ARTIST, wavSong.getArtist());
    }

    /**
     * Ensure that the setter for the album throws
     * {@link UnsupportedOperationException}.
     */
    @Test
    public void testSetAlbum() {
        try {
            wavSong.setAlbum("New Album");
            fail();
        } catch (UnsupportedOperationException expected) {
        }
    }

    /**
     * Test the getter for the album.
     */
    @Test
    public void testGetAlbum() {
        assertEquals(ALBUM, wavSong.getAlbum());
    }

    /**
     * The {@link String} representation of an {@link WavSong} object is
     * expected to be "Title".
     */
    @Test
    public void testToString() {
        assertEquals(TITLE, wavSong.toString());
    }

}

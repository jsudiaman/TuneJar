package com.sudicode.tunejar.song;

import static org.junit.Assert.assertEquals;

import com.sudicode.tunejar.config.TestDefaults;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.net.URL;
import java.util.Map;

/**
 * Tests an implementation of the {@link Song} interface. Subclasses are
 * required to supply a song file by overriding {@link SongTest#getSongFile()}.
 */
public abstract class SongTest {

    // NOTE: Variables that begin with '_' should not be modified outside of
    // setUp() and tearDown().
    private String _songFile;
    private Song _song;
    private String _originalTitle;
    private String _originalArtist;
    private String _originalAlbum;

    public abstract String getSongFile();

    @Before
    public final void setUp() throws Exception {
        _songFile = getSongFile();
        File file = new File(_songFile);

        // Download the song if necessary.
        Map<File, URL> map = TestDefaults.SAMPLE_MUSIC_MAP;
        if (!file.exists()) {
            System.out.print("Downloading " + map.get(file) + "...");
            FileUtils.copyURLToFile(map.get(file), file);
            System.out.println("Done.");
        }

        _song = SongFactory.create(file);
        _originalTitle = _song.getTitle();
        _originalArtist = _song.getArtist();
        _originalAlbum = _song.getAlbum();
    }

    @After
    public final void tearDown() throws Exception {
        _song = SongFactory.create(new File(_songFile));
        _song.setTitle(_originalTitle);
        _song.setArtist(_originalArtist);
        _song.setAlbum(_originalAlbum);
    }

    @Test
    public final void testSetTitle() throws Exception {
        // Set the title.
        _song.setTitle("abc123");

        // Assertion: ONLY the title has changed in the metadata.
        Song song = SongFactory.create(new File(_songFile));
        assertEquals("abc123", song.getTitle());
        assertEquals(_originalArtist, song.getArtist());
        assertEquals(_originalAlbum, song.getAlbum());
    }

    @Test
    public final void testSetArtist() throws Exception {
        // Set the artist.
        _song.setArtist("abc123");

        // Assertion: ONLY the artist has changed in the metadata.
        Song song = SongFactory.create(new File(_songFile));
        assertEquals(_originalTitle, song.getTitle());
        assertEquals("abc123", song.getArtist());
        assertEquals(_originalAlbum, song.getAlbum());
    }

    @Test
    public final void testSetAlbum() throws Exception {
        // Set the album.
        _song.setAlbum("abc123");

        // Assertion: ONLY the album has changed in the metadata.
        Song song = SongFactory.create(new File(_songFile));
        assertEquals(_originalTitle, song.getTitle());
        assertEquals(_originalArtist, song.getArtist());
        assertEquals("abc123", song.getAlbum());
    }

}

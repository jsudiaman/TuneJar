package com.sudicode.tunejar.song;

import com.google.common.collect.ImmutableList;
import org.junit.Test;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

public class SongFactoryTest {

    private final File mp3File = new File("src/test/resources/mp3/AfterDark.mp3");
    private final File mp4File = new File("src/test/resources/mp4/CrunkKnight.m4a");
    private final File wavFile = new File("src/test/resources/wav/Cute.wav");
    private final File directory = new File("src/test/resources/");
    private final File nonSongFile = new File("src/test/resources/README.md");

    @Test
    public void testCreate() {
        assertThat(SongFactory.create(mp3File), is(instanceOf(Mp3Song.class)));
        assertThat(SongFactory.create(mp4File), is(instanceOf(Mp4Song.class)));
        assertThat(SongFactory.create(wavFile), is(instanceOf(WavSong.class)));
        assertThat(SongFactory.create(directory), is(nullValue()));
        assertThat(SongFactory.create(nonSongFile), is(nullValue()));
    }

    @Test
    public void testDuplicate() {
        List<Song> songs = ImmutableList.of(new Mp3Song(mp3File), new Mp4Song(mp4File), new WavSong(wavFile));
        List<Song> dupes = songs.stream().map(SongFactory::duplicate).collect(Collectors.toList());

        for (int i = 0; i < songs.size(); i++) {
            Song song = songs.get(i);
            Song dupe = dupes.get(i);
            assertThat(song, is(not(equalTo(dupe))));
            assertThat(song.getTitle(), is(equalTo(dupe.getTitle())));
            assertThat(song.getArtist(), is(equalTo(dupe.getArtist())));
            assertThat(song.getAlbum(), is(equalTo(dupe.getAlbum())));
            assertThat(song.getAbsoluteFilename(), is(equalTo(dupe.getAbsoluteFilename())));
        }

        try {
            SongFactory.duplicate(new Song() {
                @Override
                protected File getAudioFile() {
                    return null;
                }
            });
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
        }
    }

}

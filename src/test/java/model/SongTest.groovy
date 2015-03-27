package model

import com.mpatric.mp3agic.Mp3File

class SongTest extends GroovyTestCase {

    Song song;
    String filePath;

    void setUp() {
        // Use the absolute path of a valid MP3 file
        // TODO Add a royalty-free mp3 with proper tags to the "resources" folder instead of using an absolute path.
        filePath = "C:\\Users\\Jonathan\\Documents\\Data Structures\\Control.mp3";
        song = new Song(new Mp3File(filePath));
    }

    void testGetAlbum() {
        println("Album: " + song.getAlbum());
    }

    void testGetArtist() {
        println("Artist: " + song.getArtist());
    }

    void testGetTitle() {
        println("Title: " + song.getTitle());
    }
}

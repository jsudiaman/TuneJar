package model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;
import viewcontroller.MainView;

/**
 * An ordered collection of Song objects.
 */
public class Playlist extends ArrayList<Song> {

    private int currentSongIndex;
    private String name;

    public Playlist() {
        this("Untitled");
    }

    public Playlist(String name) {
        super();
        currentSongIndex = 0;
        this.name = name;
    }

    public void play() {
    	this.get(currentSongIndex).play();
    }

    public void pause() {
    	this.get(currentSongIndex).pause();
    }

    public void stop() {
    	this.get(currentSongIndex).stop();
    }

    public void playPrevSong() {
    	if(currentSongIndex <= 0) {
    		currentSongIndex--;
    	} else {
            MainView.stopPlayback();
        }
    }

    public void playNextSong() {
    	if(currentSongIndex + 1 >= this.size()) {
    		currentSongIndex++;
    	} else {
            MainView.stopPlayback();
        }
    }

    public void setCurrentSongIndex(int index) {
        currentSongIndex = index;
    }

    public void loadInM3U(File m3uFile) throws UnsupportedTagException, InvalidDataException, IOException {
        BufferedReader reader = new BufferedReader(new FileReader(m3uFile));
        for (String nextLine; (nextLine = reader.readLine()) != null; ) {
            this.add(new Song(new Mp3File(new File(nextLine))));
        }
        reader.close();
    }

    public void saveAsM3U() throws IOException {
    	BufferedWriter writer = new BufferedWriter(new FileWriter(name + ".m3u", false));
        for (Song song : this) {
            writer.write(song.getAbsoluteFilename());
            writer.newLine();
        }
    	writer.close();
    }

}

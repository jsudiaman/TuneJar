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

/**
 * An ordered collection of Song objects.
 */
public class Playlist extends ArrayList<Song> {

    private int currentSongIndex;
    private String name;

    public Playlist() {
        super();
        currentSongIndex = 0;
        String name = "playlistman";
    }

    public void play() {
        // TODO Playlist::play() not yet implemented
    	this.get(currentSongIndex).play();
    }

    public void pause() {
        // TODO Playlist::pause() not yet implemented
    	this.get(currentSongIndex).pause();
    }

    public void stop() {
        // TODO Playlist::stop() not yet implemented
    	this.get(currentSongIndex).stop();
    }

    public void playPrevSong() {
    	if(currentSongIndex != 0){
    		currentSongIndex--;
    	}
        // TODO Playlist::playPrevSong() not yet implemented
    }

    public void playNextSong() {
    	if(currentSongIndex+1 != this.size()){
    		currentSongIndex ++;
    	}
        // TODO Playlist::playNextSong() not yet implemented
    }

    public void setCurrentSongIndex(int index) {
        currentSongIndex = index;
    }

    /*
     * Good reference for the following two methods:
     * http://support.microsoft.com/en-us/kb/249234
     */
    public void loadInM3U(File m3uFile) throws UnsupportedTagException, InvalidDataException, IOException {
        // TODO Playlist::loadInM3U() not yet implemented
        BufferedReader reader = new BufferedReader(new FileReader(m3uFile));
        for (String nextLine; (nextLine = reader.readLine()) != null; ) {
            this.add(new Song(new Mp3File(new File(nextLine))));
           }
        reader.close();
    }

    public void saveAsM3U() throws IOException {
        // TODO Playlist::saveAsM3U() not yet implemented
    	BufferedWriter writer = new BufferedWriter(new FileWriter(name+".m3u", false));
    	for(int i = 0; i < this.size(); i++){
    		writer.write(this.get(i).getFilename());
    		writer.newLine();
    	}
    	writer.close();
    }

}

package model;

import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;
import javafx.beans.property.SimpleStringProperty;

import java.io.*;
import java.util.ArrayList;

/**
 * An ordered collection of Song objects.
 */
public class Playlist extends ArrayList<Song> {

    private SimpleStringProperty name;

    public Playlist() {
        this("Untitled");
    }

    public Playlist(String name) {
        super();
        this.name = new SimpleStringProperty(name);
    }

    public String getName() {
        return name.get();
    }

    public void saveAsM3U() throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(name.get() + ".m3u", false));
        for (Song song : this) {
            writer.write(song.getAbsoluteFilename());
            writer.newLine();
        }
        writer.close();
    }

    public void loadInM3U(File m3uFile) throws UnsupportedTagException, InvalidDataException, IOException {
        BufferedReader reader = new BufferedReader(new FileReader(m3uFile));
        for (String nextLine; (nextLine = reader.readLine()) != null; ) {
            this.add(new Song(new Mp3File(new File(nextLine))));
        }
        reader.close();
    }

}

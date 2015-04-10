package model;

import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;
import javafx.beans.property.SimpleStringProperty;

import java.io.*;
import java.util.ArrayList;
import java.util.logging.Level;

import static model.DebugUtils.LOGGER;

/**
 * An ordered collection of Song objects.
 */
public class Playlist extends ArrayList<Song> {

    private SimpleStringProperty name;
    private boolean savable;

    public Playlist() {
        this("Untitled", false);
    }

    /**
     * Creates a new instance of Playlist.
     *
     * @param name The name of the playlist
     * @param savable If enabled, the playlist will be saved as a .m3u file in the local directory.
     */
    public Playlist(String name, boolean savable) throws IllegalArgumentException {
        super();
        this.name = new SimpleStringProperty(name);
        this.savable = savable;
        if (savable) save();
    }

    /**
     * Creates a playlist from a .m3u file.
     *
     * @param m3uFile A .m3u file
     */
    public Playlist(File m3uFile) {
        this(m3uFile.getName().substring(0, m3uFile.getName().indexOf(".m3u")), false);
        try {
            BufferedReader reader = new BufferedReader(new FileReader(m3uFile));
            for (String nextLine; (nextLine = reader.readLine()) != null; ) {
                add(new Song(new Mp3File(new File(nextLine))));
            }
            reader.close();
        } catch (UnsupportedTagException | InvalidDataException | IOException e) {
            e.printStackTrace();
        }
        savable = true;
    }

    public String getName() {
        return name.get();
    }

    @Override
    public boolean add(Song s) {
        boolean successful = super.add(s);
        if(successful && savable) {
            LOGGER.log(Level.INFO, "Adding \"" + s.toString() + "\" to playlist: " + name.get());
            save();
        }
        return successful;
    }

    /**
     * Save the playlist as a .m3u file.
     */
    public void save() {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(name.get() + ".m3u", false));
            for (Song song : this) {
                writer.write(song.getAbsoluteFilename());
                writer.newLine();
            }
            writer.close();
            LOGGER.log(Level.INFO, "Successfully saved playlist: " + name.get() + ".m3u");
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to save playlist: " + name.get() + ".m3u", e);
        }
    }

}

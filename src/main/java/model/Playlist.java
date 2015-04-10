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

    // --------------- Constructors --------------- //

    /**
     * Creates a new instance of Playlist that is not savable
     * and is named "Untitled".
     */
    public Playlist() {
        this.name = new SimpleStringProperty("Untitled");
        this.savable = false;
    }

    /**
     * Creates a new instance of Playlist that is not savable.
     *
     * @param name The name of the playlist
     */
    public Playlist(String name) {
        this.name = new SimpleStringProperty(name);
        this.savable = false;
    }

    /**
     * Creates a new instance of Playlist.
     *
     * @param name The name of the playlist
     * @param savable If enabled, the playlist will be saved as a .m3u file in the local directory.
     *
     * @throws IOException The playlist could not be saved successfully
     */
    public Playlist(String name, boolean savable) throws IOException {
        super();
        this.name = new SimpleStringProperty(name);
        this.savable = savable;
        if (savable) save();
    }

    /**
     * Creates a playlist from a .m3u file.
     *
     * @param m3uFile A .m3u file
     *
     * @throws IOException Failed to read the .m3u file
     */
    public Playlist(File m3uFile) throws IOException {
        this(m3uFile.getName().substring(0, m3uFile.getName().indexOf(".m3u")), false);
        BufferedReader reader = new BufferedReader(new FileReader(m3uFile));
        for (String nextLine; (nextLine = reader.readLine()) != null; ) {
            try {
                add(new Song(new Mp3File(new File(nextLine))));
            } catch (UnsupportedTagException | InvalidDataException e) {
                LOGGER.log(Level.SEVERE, "Failed to add song: " + nextLine, e);
            }
        }
        reader.close();

        savable = true;
    }

    // --------------- Getters and Setters --------------- //

    public String getName() {
        return name.get();
    }

    // --------------- Saving --------------- //

    /**
     * Save the playlist as a .m3u file.
     */
    public void save() throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(name.get() + ".m3u", false));
        for (Song song : this) {
            writer.write(song.getAbsoluteFilename());
            writer.newLine();
        }
        writer.close();
        LOGGER.log(Level.INFO, "Successfully saved playlist: " + name.get() + ".m3u");
    }

    // --------------- Overriding Methods --------------- //

    @Override
    public boolean add(Song s) {
        boolean successful = super.add(s);
        if(successful && savable) {
            LOGGER.log(Level.INFO, "Adding \"" + s.toString() + "\" to playlist: " + name.get());
            try {
                save();
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Failed to add song: " + s.toString(), e);
            }
        }
        return successful;
    }

}

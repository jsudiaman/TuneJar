package model;

import static model.DebugUtils.LOGGER;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;

import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;

import javafx.beans.property.SimpleStringProperty;

/**
 * An ordered collection of Song objects.
 */
// TODO Implement List instead of extending ArrayList 
// TODO Use a thread-safe data structure
public class Playlist extends ArrayList<Song> {

	private final SimpleStringProperty name;

	// --------------- Constructors --------------- //

	/**
	 * Creates a new instance of Playlist that is named "Untitled".
	 */
	public Playlist() {
		this.name = new SimpleStringProperty("Untitled");
	}

	/**
	 * Creates a new instance of Playlist.
	 *
	 * @param name
	 *            The name of the playlist
	 */
	public Playlist(String name) {
		this.name = new SimpleStringProperty(name);
	}

	/**
	 * Creates a playlist from a .m3u file.
	 *
	 * @param m3uFile
	 *            A .m3u file
	 *
	 * @throws IOException
	 *             Failed to read the .m3u file
	 */
	public Playlist(File m3uFile) throws IOException {
		// Take the filename to be the name of the playlist.
		this.name = new SimpleStringProperty(m3uFile.getName().substring(0, m3uFile.getName().indexOf(".m3u")));

		// Add each song line by line.
		BufferedReader reader = new BufferedReader(new FileReader(m3uFile));
		for (String nextLine; (nextLine = reader.readLine()) != null;) {
			try {
				add(new Song(new Mp3File(new File(nextLine))));
			} catch (UnsupportedTagException | InvalidDataException | IOException e) {
				LOGGER.log(Level.SEVERE, "Failed to add song: " + nextLine, e);
			}
		}

		reader.close();
	}

	// --------------- Getters and Setters --------------- //

	public void setName(String newName) {
		name.set(newName);
	}

	public String getName() {
		return name.get();
	}

	// --------------- Saving --------------- //

	/**
	 * Save the playlist as a .m3u file.
	 *
	 * @throws IOException
	 *             Failed to save the playlist
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

	// --------------- Method Overriding --------------- //
	/**
	 * Similar to java.util.Arraylist.add(E e), but uses the copy constructor
	 * instead of directly adding the argument.
	 */
	@Override
	public boolean add(Song s) {
		return super.add(new Song(s));
	}

	/**
	 * Adds the contents of the song collection to this playlist. Uses the copy
	 * constructor instead of directly adding items from the collection.
	 * 
	 * @param songs
	 *            A song collection
	 * 
	 * @return True iff at least one song in the collection was added to this
	 *         playlist.
	 */
	@Override
	public boolean addAll(Collection<? extends Song> songs) {
		boolean added = false;
		for (Song s : songs) {
			if (add(s)) {
				added = true;
			}
		}
		return added;
	}

}

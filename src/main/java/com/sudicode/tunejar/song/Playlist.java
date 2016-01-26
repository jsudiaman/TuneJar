package com.sudicode.tunejar.song;

import com.sudicode.tunejar.config.Defaults;
import javafx.beans.property.SimpleStringProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * An ordered collection of Song objects.
 */
public class Playlist implements List<Song> {

	private static final Logger LOGGER = LoggerFactory.getLogger(Playlist.class);

	private final List<Song> list = new ArrayList<>();
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
	 * @param name The name of the playlist
	 */
	public Playlist(String name) {
		this.name = new SimpleStringProperty(name);
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
	 * @throws IOException Failed to save the playlist
	 */
	public void save() throws IOException {
		File outFile = Paths.get(Defaults.PLAYLISTS_FOLDER, name.get() + ".m3u").toFile();
		BufferedWriter writer = new BufferedWriter(new FileWriter(outFile, false));
		for (Song song : this) {
			writer.write(song.getAbsoluteFilename());
			writer.newLine();
		}
		writer.close();
		LOGGER.info("Successfully saved playlist: " + name.get() + ".m3u");
	}

	// --------------- Method Overriding --------------- //

	/**
	 * Uses the copy constructor instead of directly adding the argument.
	 */
	@Override
	public boolean add(Song s) {
		return list.add(Songs.duplicate(s));
	}

	/**
	 * Adds the contents of the song collection to this playlist. Uses the copy
	 * constructor instead of directly adding items from the collection.
	 *
	 * @param songs A song collection
	 * @return True iff at least one song in the collection was added to this
	 * playlist.
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

	// --------------- Delegation --------------- //

	@Override
	public int size() {
		return list.size();
	}

	@Override
	public boolean isEmpty() {
		return list.isEmpty();
	}

	@Override
	public boolean contains(Object o) {
		return list.contains(o);
	}

	@Override
	public Iterator<Song> iterator() {
		return list.iterator();
	}

	@Override
	public Object[] toArray() {
		return list.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return list.toArray(a);
	}

	@Override
	public boolean remove(Object o) {
		return list.remove(o);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return list.containsAll(c);
	}

	@Override
	public boolean addAll(int index, Collection<? extends Song> c) {
		return list.addAll(c);
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		return list.removeAll(c);
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return list.retainAll(c);
	}

	@Override
	public void clear() {
		list.clear();
	}

	@Override
	public Song get(int index) {
		return list.get(index);
	}

	@Override
	public Song set(int index, Song element) {
		return list.set(index, element);
	}

	@Override
	public void add(int index, Song element) {
		list.add(index, element);
	}

	@Override
	public Song remove(int index) {
		return list.remove(index);
	}

	@Override
	public int indexOf(Object o) {
		return list.indexOf(o);
	}

	@Override
	public int lastIndexOf(Object o) {
		return list.lastIndexOf(o);
	}

	@Override
	public ListIterator<Song> listIterator() {
		return list.listIterator();
	}

	@Override
	public ListIterator<Song> listIterator(int index) {
		return list.listIterator(index);
	}

	@Override
	public List<Song> subList(int fromIndex, int toIndex) {
		return list.subList(fromIndex, toIndex);
	}

}

package tunejar.song;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.beans.property.SimpleStringProperty;
import tunejar.config.Defaults;

/**
 * An ordered collection of Song objects.
 */
public class Playlist implements List<Song> {

	private static final Logger LOGGER = LogManager.getLogger();

	private final List<Song> list = Collections.synchronizedList(new ArrayList<Song>());
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
	 * @throws TimeoutException
	 * @throws InterruptedException
	 */
	public Playlist(File m3uFile) throws IOException, TimeoutException, InterruptedException {
		// Take the filename to be the name of the playlist.
		this.name = new SimpleStringProperty(m3uFile.getName().substring(0, m3uFile.getName().lastIndexOf(".m3u")));

		// Add each song line by line.
		try (BufferedReader reader = new BufferedReader(new FileReader(m3uFile))) {
			ExecutorService executor = Executors.newWorkStealingPool();
			for (String nextLine; (nextLine = reader.readLine()) != null;) {
				String file = nextLine;
				executor.submit(() -> {
					try {
						add(Songs.create(new File(file)));
					} catch (Exception e) {
						LOGGER.error("Failed to add song: " + file, e);
					}
				});
			}

			// Block until all of the songs have been added.
			executor.shutdown();
			if (!executor.awaitTermination(Defaults.GET_SONGS_TIMEOUT, TimeUnit.SECONDS))
				throw new TimeoutException();
		}
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

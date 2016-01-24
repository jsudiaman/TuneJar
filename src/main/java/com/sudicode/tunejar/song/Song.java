package com.sudicode.tunejar.song;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;

import javafx.beans.property.SimpleStringProperty;

public abstract class Song {

	private static final Logger LOGGER = LogManager.getLogger();

	protected SimpleStringProperty title;
	protected SimpleStringProperty artist;
	protected SimpleStringProperty album;
	protected File audioFile;

	/**
	 * <p>
	 * Constructs a new Song.
	 * </p>
	 * 
	 * <p>
	 * Note that this constructor does not initialize <code>audioFile</code>. To
	 * avoid possible NPEs, care should be taken to ensure that at the very
	 * least, <code>audioFile</code> is set to a meaningful value
	 * <b>immediately</b> after this constructor is called.
	 * </p>
	 */
	protected Song() {
		title = new SimpleStringProperty("");
		artist = new SimpleStringProperty("");
		album = new SimpleStringProperty("");
	}

	/**
	 * Finds the absolute path of the audio file. For example, if the audio file
	 * is located in 'C:\Users\JohnDoe\Music\B.mp3', then that entire string
	 * will be returned.
	 *
	 * @return The absolute path
	 */
	public String getAbsoluteFilename() {
		if (audioFile == null) {
			return "null";
		}

		return audioFile.getAbsolutePath();
	}

	/**
	 * Checks if the song can be edited. Please note this implies that
	 * <b>all</b> fields are editable - Title, Artist, and Album. It also
	 * implies that changes can be made to the audio file itself (not just this
	 * object). If such an operation is unsupported, it is acceptable to
	 * hard-code this method to return <code>false</code>. In that case, setters
	 * should also throw an {@link AssertionError}, since setters should
	 * <b>not</b> be accessible if this method returns false.
	 * 
	 * @return True if the song can be edited.
	 */
	public boolean canEdit() {
		try {
			setTitle(getTitle());
			setArtist(getArtist());
			setAlbum(getAlbum());
			return true;
		} catch (Exception e) {
			LOGGER.error("Unable to edit song: " + toString(), e);
			return false;
		}
	}

	/**
	 * Sets the title in both this object and the audio file.
	 * 
	 * @param title
	 * @throws Exception
	 */
	public void setTitle(String title) throws Exception {
		AudioFile f = AudioFileIO.read(audioFile);
		Tag tag = f.getTag();
		tag.setField(FieldKey.TITLE, title);
		f.commit();
		this.title.set(title);
	}

	public String getTitle() {
		if (title.get().equals("")) {
			return new SimpleStringProperty(audioFile.getName()).get();
		} else {
			return title.get();
		}
	}

	/**
	 * Sets the artist in both this object and the audio file.
	 * 
	 * @param artist
	 * @throws Exception
	 */
	public void setArtist(String artist) throws Exception {
		AudioFile f = AudioFileIO.read(audioFile);
		Tag tag = f.getTag();
		tag.setField(FieldKey.ARTIST, artist);
		f.commit();
		this.artist.set(artist);
	}

	public String getArtist() {
		return artist.get();
	}

	/**
	 * Sets the album in both this object and the audio file.
	 * 
	 * @param album
	 * @throws Exception
	 */
	public void setAlbum(String album) throws Exception {
		AudioFile f = AudioFileIO.read(audioFile);
		Tag tag = f.getTag();
		tag.setField(FieldKey.ALBUM, album);
		f.commit();
		this.album.set(album);
	}

	public String getAlbum() {
		return album.get();
	}

	/**
	 * A string representation of the song object.
	 *
	 * @return Title - Artist
	 */
	public String toString() {
		if (getArtist().equals(""))
			return getTitle();
		else
			return getTitle() + " - " + getArtist();
	}

}

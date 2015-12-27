package tunejar.song;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mpatric.mp3agic.ID3v1;
import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.ID3v24Tag;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.NotSupportedException;
import com.mpatric.mp3agic.UnsupportedTagException;

import javafx.beans.property.SimpleStringProperty;
import tunejar.config.ID3Version;
import tunejar.player.Player;

public class Mp3Song implements Song {

	private static final Logger LOGGER = LogManager.getLogger();

	private SimpleStringProperty title;
	private SimpleStringProperty artist;
	private SimpleStringProperty album;
	private boolean paused;
	private ID3Version id3Version;
	private Mp3File mp3file;

	/**
	 * Creates a new song by extracting metadata from the specified file.
	 *
	 * @param mp3file
	 *            The mp3 file containing the song
	 */
	public Mp3Song(Mp3File mp3file) {
		// Find out which version of ID3 tag is used by the MP3.
		if (mp3file.hasId3v2Tag()) {
			id3Version = ID3Version.ID3_V2;
		} else if (mp3file.hasId3v1Tag()) {
			id3Version = ID3Version.ID3_V1;
		} else {
			id3Version = ID3Version.CUSTOM_TAG;
		}

		// Read metadata by extracting its tags.
		if (id3Version == ID3Version.ID3_V2) {
			ID3v2 id3v2tag = mp3file.getId3v2Tag();
			title = new SimpleStringProperty(id3v2tag.getTitle());
			artist = new SimpleStringProperty(id3v2tag.getArtist());
			album = new SimpleStringProperty(id3v2tag.getAlbum());
		} else if (id3Version == ID3Version.ID3_V1) {
			ID3v1 id3v1tag = mp3file.getId3v1Tag();
			title = new SimpleStringProperty(id3v1tag.getTitle());
			artist = new SimpleStringProperty(id3v1tag.getArtist());
			album = new SimpleStringProperty(id3v1tag.getAlbum());
		} else {
			title = new SimpleStringProperty(getFilename());
			artist = new SimpleStringProperty("");
			album = new SimpleStringProperty("");
		}
		this.mp3file = mp3file;

		// Correct null title, artist, and/or album values.
		if (title.get() == null) {
			title = new SimpleStringProperty(getFilename());
		}
		if (artist.get() == null) {
			artist = new SimpleStringProperty("");
		}
		if (album.get() == null) {
			album = new SimpleStringProperty("");
		}

		paused = false;
	}

	/**
	 * Copy constructor.
	 * 
	 * @param s
	 *            {@link Mp3Song} object to copy
	 */
	public Mp3Song(Mp3Song s) {
		this(s.mp3file);
	}

	@Override
	public void play() {
		if (paused) {
			paused = false;
			Player.getInstance().resumePlayback();
		} else {
			Player.getInstance().load(this);
		}
	}

	@Override
	public void pause() {
		paused = true;
		Player.getInstance().pausePlayback();
	}

	@Override
	public void stop() {
		paused = false;
		Player.getInstance().stopPlayback();
	}

	/**
	 * Finds the absolute path of the MP3. Ex. If the MP3 is located in
	 * 'C:\Users\Joe\Music\B.mp3', that entire string will be returned.
	 *
	 * @return The absolute path
	 */
	@Override
	public String getAbsoluteFilename() {
		File file = new File(mp3file.getFilename());
		return file.getAbsolutePath();
	}

	@Override
	public boolean canEdit() {
		try {
			save();
			return true;
		} catch (Exception e) {
			LOGGER.error("Unable to edit song: " + toString(), e);
			return false;
		}
	}

	@Override
	public void setTitle(String title) {
		this.title.set(title);
		switch (id3Version) {
		case ID3_V2:
			ID3v2 ID3v2Tag = mp3file.getId3v2Tag();
			ID3v2Tag.setTitle(title);
			break;
		case ID3_V1:
			ID3v1 ID3v1Tag = mp3file.getId3v1Tag();
			ID3v1Tag.setTitle(title);
			break;
		default:
			ID3v2 tag = new ID3v24Tag();
			mp3file.setId3v2Tag(tag);
			tag.setTitle(title);
			id3Version = ID3Version.ID3_V2;
			break;
		}

		try {
			save();
		} catch (Exception e) {
			LOGGER.error("Unable to set title.", e);
		}
	}

	@Override
	public String getTitle() {
		return title.get();
	}

	@Override
	public void setArtist(String artist) {
		this.artist.set(artist);
		switch (id3Version) {
		case ID3_V2:
			ID3v2 ID3v2Tag = mp3file.getId3v2Tag();
			ID3v2Tag.setArtist(artist);
			break;
		case ID3_V1:
			ID3v1 ID3v1Tag = mp3file.getId3v1Tag();
			ID3v1Tag.setArtist(artist);
			break;
		default:
			ID3v2 tag = new ID3v24Tag();
			mp3file.setId3v2Tag(tag);
			tag.setArtist(artist);
			id3Version = ID3Version.ID3_V2;
			break;
		}

		try {
			save();
		} catch (Exception e) {
			LOGGER.error("Unable to set artist.", e);
		}
	}

	@Override
	public String getArtist() {
		return artist.get();
	}

	@Override
	public void setAlbum(String album) {
		this.album.set(album);
		switch (id3Version) {
		case ID3_V2:
			ID3v2 ID3v2Tag = mp3file.getId3v2Tag();
			ID3v2Tag.setAlbum(album);
			break;
		case ID3_V1:
			ID3v1 ID3v1Tag = mp3file.getId3v1Tag();
			ID3v1Tag.setAlbum(album);
			break;
		default:
			ID3v2 tag = new ID3v24Tag();
			mp3file.setId3v2Tag(tag);
			tag.setAlbum(album);
			id3Version = ID3Version.ID3_V2;
			break;
		}

		try {
			save();
		} catch (Exception e) {
			LOGGER.error("Unable to set album.", e);
		}
	}

	@Override
	public String getAlbum() {
		return album.get();
	}

	/**
	 * A string representation of the song object.
	 *
	 * @return "Song Title - Artist"
	 */
	@Override
	public String toString() {
		return getTitle() + " - " + getArtist();
	}

	/**
	 * Finds the file name of the MP3 without any directory information. Ex. If
	 * the MP3 is located in 'C:\Users\Joe\Music\B.mp3', 'B' will be returned.
	 *
	 * @return The file name
	 */
	private String getFilename() {
		try {
			String filename = new File(mp3file.getFilename()).getName();
			filename = filename.substring(0, filename.lastIndexOf(".mp3"));
			return filename;
		} catch (NullPointerException e) {
			return "null";
		}
	}

	/**
	 * Saves changes to the MP3 file.
	 *
	 * @throws IOException
	 * @throws NotSupportedException
	 * @throws InvalidDataException
	 * @throws UnsupportedTagException
	 */
	private void save() throws IOException, NotSupportedException, InvalidDataException, UnsupportedTagException {
		try {
			String tempname = mp3file.getFilename() + ".tmp";
			mp3file.save(mp3file.getFilename() + ".tmp");
			Files.move(Paths.get(tempname), Paths.get(mp3file.getFilename()), StandardCopyOption.REPLACE_EXISTING);
			mp3file = new Mp3File(new File(mp3file.getFilename()));
		} finally {
			Files.deleteIfExists(Paths.get(mp3file.getFilename() + ".tmp"));
		}
	}

}

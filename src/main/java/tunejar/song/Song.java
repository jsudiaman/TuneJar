package tunejar.song;

import java.io.File;

import javafx.beans.property.SimpleStringProperty;
import tunejar.player.Player;

public abstract class Song {

	protected SimpleStringProperty title;
	protected SimpleStringProperty artist;
	protected SimpleStringProperty album;
	protected boolean paused;
	protected File audioFile;

	static {
		// Disable JAudioTagger's logger. (Note: In this case, fully qualified
		// names are used to indicate that it is java.util.Logging and NOT
		// Apache Log4j)
		java.util.logging.Logger.getLogger("org.jaudiotagger").setLevel(java.util.logging.Level.OFF);
	}

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
		paused = false;
	}

	/**
	 * Plays the song.
	 */
	public void play() {
		if (paused) {
			paused = false;
			Player.getInstance().resumePlayback();
		} else {
			Player.getInstance().load(this);
		}
	}

	/**
	 * Pauses the song.
	 */
	public void pause() {
		paused = true;
		Player.getInstance().pausePlayback();
	}

	/**
	 * Stops the song.
	 */
	public void stop() {
		paused = false;
		Player.getInstance().stopPlayback();
	}

	/**
	 * Finds the name of the audio file without any information about its
	 * directory or file extension. For example, if the audio file is located in
	 * 'C:\Users\JohnDoe\Music\B.mp3', then 'B' will be returned.
	 *
	 * @return The file name
	 */
	public String getFilename() {
		if (audioFile == null) {
			return "null";
		}

		String filename = audioFile.getName();
		filename = filename.substring(0, filename.lastIndexOf('.'));
		return filename;
	}

	/**
	 * Finds the absolute path of the audio file. For example, if the audio file
	 * is located in 'C:\Users\JohnDoe\Music\B.mp3', then that entire string
	 * will be returned.
	 *
	 * @return The absolute path
	 */
	public String getAbsoluteFilename() {
		return audioFile.getAbsolutePath();
	}

	/**
	 * Checks if the song can be edited. Please note this implies that
	 * <b>all</b> fields are editable - Title, Artist, and Album. It also
	 * implies that changes can be made to the audio file itself (not just this
	 * object). If such an operation is unsupported, it is acceptable to
	 * hard-code this method to return <code>false</code>. In that case, setters
	 * should also throw an {@link AssertionError}, since setters should
	 * <b>not</b> be accessed if this method returns false.
	 * 
	 * @return True if the song can be edited.
	 */
	public abstract boolean canEdit();

	/**
	 * Sets the title in both this object and the audio file.
	 * 
	 * @param title
	 * @throws Exception
	 */
	public abstract void setTitle(String title) throws Exception;

	public String getTitle() {
		if (title.get().equals("") && getFilename() != null) {
			return new SimpleStringProperty(getFilename()).get();
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
	public abstract void setArtist(String artist) throws Exception;

	public String getArtist() {
		return artist.get();
	}

	/**
	 * Sets the album in both this object and the audio file.
	 * 
	 * @param album
	 * @throws Exception
	 */
	public abstract void setAlbum(String album) throws Exception;

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

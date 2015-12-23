package tunejar.song;

import javafx.beans.property.SimpleStringProperty;

public abstract class Song {

	protected SimpleStringProperty title;
	protected SimpleStringProperty artist;
	protected SimpleStringProperty album;
	protected boolean paused;

	public abstract void play();

	public abstract void pause();

	public abstract void stop();

	public abstract String getAbsoluteFilename();

	public abstract boolean canEdit();

	public abstract void setTitle(String title);

	public abstract String getTitle();

	public abstract void setArtist(String artist);

	public abstract String getArtist();

	public abstract void setAlbum(String album);

	public abstract String getAlbum();

	public abstract String toString();

}

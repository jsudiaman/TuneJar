package tunejar.song;

public interface Song {

	public void play(double value);

	public void pause();

	public void stop();

	public String getFilename();

	public String getAbsoluteFilename();

	public void setMetadata(String title, String artist, String album);

	public boolean canSave();

	public String getTitle();

	public String getArtist();

	public String getAlbum();

}

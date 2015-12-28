package tunejar.song;

public interface Song {

	public void play();

	public void pause();

	public void stop();

	public String getAbsoluteFilename();

	public boolean canEdit();

	public void setTitle(String title);

	public String getTitle();

	public void setArtist(String artist);

	public String getArtist();

	public void setAlbum(String album);

	public String getAlbum();

}

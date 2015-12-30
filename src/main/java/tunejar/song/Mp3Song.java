package tunejar.song;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.id3.ID3v1Tag;
import org.jaudiotagger.tag.id3.ID3v24Frames;
import org.jaudiotagger.tag.id3.ID3v24Tag;

public class Mp3Song extends Song {

	private static final Logger LOGGER = LogManager.getLogger();

	public Mp3Song(File mp3File) {
		audioFile = mp3File;

		try {
			MP3File f = (MP3File) AudioFileIO.read(audioFile);

			// Parse metadata
			if (f.hasID3v2Tag()) {
				ID3v24Tag tag = f.getID3v2TagAsv24();
				title.set(tag.getFirst(ID3v24Frames.FRAME_ID_TITLE));
				artist.set(tag.getFirst(ID3v24Frames.FRAME_ID_ARTIST));
				album.set(tag.getFirst(ID3v24Frames.FRAME_ID_ALBUM));
			} else if (f.hasID3v1Tag()) {
				ID3v1Tag tag = f.getID3v1Tag();
				title.set(tag.getFirst(FieldKey.TITLE));
				artist.set(tag.getFirst(FieldKey.ARTIST));
				album.set(tag.getFirst(FieldKey.ALBUM));
			}
		} catch (Exception e) {
			LOGGER.error("Unable to parse: " + mp3File, e);
		}
	}

	public Mp3Song(Mp3Song mp3Song) {
		this(mp3Song.audioFile);
	}

	@Override
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

	@Override
	public void setTitle(String title) throws Exception {
		AudioFile f = AudioFileIO.read(audioFile);
		Tag tag = f.getTag();
		tag.setField(FieldKey.TITLE, title);
		f.commit();
		this.title.set(title);
	}

	@Override
	public void setArtist(String artist) throws Exception {
		AudioFile f = AudioFileIO.read(audioFile);
		Tag tag = f.getTag();
		tag.setField(FieldKey.ARTIST, artist);
		f.commit();
		this.artist.set(artist);
	}

	@Override
	public void setAlbum(String album) throws Exception {
		AudioFile f = AudioFileIO.read(audioFile);
		Tag tag = f.getTag();
		tag.setField(FieldKey.ALBUM, album);
		f.commit();
		this.album.set(album);
	}

}

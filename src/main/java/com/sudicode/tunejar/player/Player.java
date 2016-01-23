package com.sudicode.tunejar.player;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.HashMultiset;
import com.sudicode.tunejar.config.Defaults;
import com.sudicode.tunejar.config.Options;
import com.sudicode.tunejar.song.Playlist;
import com.sudicode.tunejar.song.Song;
import com.sudicode.tunejar.song.Songs;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.media.Media;
import javafx.scene.media.MediaException;
import javafx.scene.media.MediaPlayer;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

public class Player extends Application {

	// Static
	private static Player instance;
	private static final Logger LOGGER = LogManager.getLogger();

	// GUI
	private MediaPlayer mediaPlayer;
	private Song nowPlaying;
	private Stage primaryStage;
	private Scene scene;
	private PlayerController controller;

	// Data
	private AtomicBoolean initialized = new AtomicBoolean(false);
	private Playlist masterPlaylist;
	private Set<File> directories;
	private Options options;

	/**
	 * Main method.
	 *
	 * @param args
	 *            The command line arguments
	 */
	public static void main(String[] args) {
		// Delete old log files.
		try {
			for (int i = 0; i < Defaults.MAX_LOOPS; i++) {
				Path logsFolder = Paths.get(Defaults.LOG_FOLDER);
				String[] files = logsFolder.toAbsolutePath().toFile().list((dir, name) -> name.endsWith(".xml"));
				if (files == null) {
					LOGGER.error("Log file cleanup failed.");
					break;
				}
				if (files.length <= Defaults.LOG_FILE_LIMIT) {
					break;
				}
				Arrays.sort(files);
				Files.delete(logsFolder.resolve(files[0]));
			}
		} catch (IOException e) {
			LOGGER.error("Log file cleanup failed.", e);
		}

		launch(args);
	}

	/**
	 * Starts the program.
	 *
	 * @param primaryStage
	 *            The stage that will hold the interface
	 */
	@Override
	public void start(Stage primaryStage) {
		try {
			init(primaryStage);
		} catch (Exception e) {
			LOGGER.catching(Level.FATAL, e);
			exitWithAlert(e);
		}
	}

	/**
	 * Handles program initialization.
	 *
	 * @param primaryStage
	 *            The stage that will hold the interface
	 * @throws IOException
	 *             Failed to load the FXML, or could not load/save a file.
	 */
	private void init(Stage primaryStage) throws IOException {
		// Initialization.
		setInstance(this);
		setOptions(new Options());

		// Load the FXML file and display the interface.
		this.setPrimaryStage(primaryStage);
		URL location = getClass().getResource(Defaults.PLAYER_FXML);
		FXMLLoader fxmlLoader = new FXMLLoader();
		Parent root = fxmlLoader.load(location.openStream());

		setScene(new Scene(root, 1000, 600));
		String theme = Defaults.THEME_MAP.get(getOptions().getTheme());
		getScene().getStylesheets().add(theme);
		LOGGER.debug("Loaded theme: " + theme);

		primaryStage.setTitle("TuneJar");
		primaryStage.setScene(getScene());
		primaryStage.getIcons().add(new Image(getClass().getResourceAsStream(Defaults.ICON)));

		// Load the directories. If none are present, prompt the user for one.
		directories = readDirectories();
		if (directories.isEmpty()) {
			File directory = initialDirectory(primaryStage);
			if (directory != null) {
				directories.add(directory);
			}
		}

		setController(fxmlLoader.getController());

		// Create and display a playlist containing all songs from each
		// directory.
		refresh();
		getController().getPlaylistMenu().loadPlaylist(getMasterPlaylist());

		// Save the directories.
		writeDirectories();

		// Load in all playlists from the working directory.
		Collection<Playlist> playlistSet = null;
		try {
			playlistSet = getPlaylists();
		} catch (NullPointerException e) {
			LOGGER.fatal("Failed to load playlists from the working directory.", e);
			exitWithAlert(e);
		}
		if (playlistSet != null) {
			playlistSet.forEach(getController().getPlaylistMenu()::loadPlaylist);
		}
		getController().focus(getController().getPlaylistTable(), 0);
		getController().getVolumeSlider().setValue(getOptions().getVolume());

		// Finally, sort the song table.
		String[] sortBy = getOptions().getSortOrder();
		getController().getSongTable().getSortOrder().clear();
		List<TableColumn<Song, ?>> sortOrder = getController().getSongTable().getSortOrder();
		for (String s : sortBy) {
			switch (s) {
			case "title":
				sortOrder.add(getController().getTitleColumn());
				break;
			case "artist":
				sortOrder.add(getController().getArtistColumn());
				break;
			case "album":
				sortOrder.add(getController().getAlbumColumn());
				break;
			default:
				break;
			}
		}

		initialized.set(true);
	}

	/**
	 * The master playlist takes in all music files that can be found in
	 * available directories.
	 */
	public void refresh() {
		getPrimaryStage().hide();
		setMasterPlaylist(new Playlist("All Music"));

		if (directories != null) {
			// Create one future for each directory.
			Collection<Future<Collection<Song>>> futures = HashMultiset.create();
			ExecutorService executor = Executors.newWorkStealingPool();
			LOGGER.info("Found directories: " + directories);
			LOGGER.info("Populating the master playlist...");
			for (File directory : directories) {
				futures.add(executor.submit(() -> getSongs(directory)));
			}
			executor.shutdown();

			// Get each future and add its contents to the master playlist.
			for (Future<Collection<Song>> fut : futures) {
				try {
					getMasterPlaylist().addAll(fut.get(Defaults.TIMEOUT, TimeUnit.SECONDS));
				} catch (InterruptedException e) {
					LOGGER.error("Interrupted.", e);
					Thread.currentThread().interrupt();
				} catch (TimeoutException e) {
					LOGGER.error("Timed out.", e);
				} catch (ExecutionException e) {
					LOGGER.catching(e);
				}
			}
		}
		LOGGER.info("Refresh successful");
		getPrimaryStage().show();
	}

	// ------------------- Media Player Controls ------------------- //

	/**
	 * Loads a song into the media player, then plays it.
	 *
	 * @param song
	 *            The song to play
	 */
	public void playSong(Song song) {
		if (getNowPlaying() == song) {
			resumeSong();
			return;
		} else if (getNowPlaying() != null) {
			stopSong();
		}
		setNowPlaying(song);
		String uriString = new File(song.getAbsoluteFilename()).toURI().toString();
		try {
			mediaPlayer = new MediaPlayer(new Media(uriString));
			LOGGER.debug("Loaded song: " + uriString);
			setVolume(getController().getVolumeSlider().getValue());
			LOGGER.info("Playing: " + getNowPlaying());
			mediaPlayer.play();
		} catch (MediaException e) {
			getController().getStatus().setText("Failed to play the song.");
			LOGGER.catching(Level.ERROR, e);
		}
	}

	/**
	 * Resumes the media player.
	 */
	public void resumeSong() {
		if (mediaPlayer != null && getNowPlaying() != null) {
			LOGGER.info("Resuming: " + getNowPlaying());
			mediaPlayer.play();
		}
	}

	/**
	 * Pauses the media player.
	 */
	public void pauseSong() {
		if (mediaPlayer != null && getNowPlaying() != null) {
			LOGGER.info("Pausing: " + getNowPlaying());
			mediaPlayer.pause();
		}
	}

	/**
	 * Stops the media player.
	 */
	public void stopSong() {
		if (mediaPlayer != null && getNowPlaying() != null) {
			LOGGER.info("Stopping: " + getNowPlaying());
			mediaPlayer.stop();
		}
		setNowPlaying(null);
	}

	// ------------------- File Manipulation ------------------- //

	/**
	 * Adds a user-selected directory to the directory collection.
	 */
	public void addDirectory() {
		File directory = chooseDirectory(getPrimaryStage());
		if (directory == null) {
			return;
		}
		directories.add(directory);
		try {
			writeDirectories();
		} catch (Exception e) {
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setTitle("Failed");
			alert.setHeaderText("Failed to add the directory.");
			alert.showAndWait();
			LOGGER.catching(Level.ERROR, e);
		}
		refresh();
	}

	/**
	 * Allows the user to choose and remove a directory from the directory set.
	 * 
	 * @return True iff a directory was successfully removed.
	 */
	public boolean removeDirectory() {
		if (directories.isEmpty()) {
			getController().getStatus().setText("No folders found.");
			return false;
		}

		// Create and display dialog box.
		List<File> choices = new ArrayList<>();
		choices.addAll(directories);
		ChoiceDialog<File> dialog = new ChoiceDialog<>(choices.get(0), choices);
		dialog.setTitle("Remove Folder");
		dialog.setHeaderText("Which folder would you like to remove?");
		dialog.setContentText("Choose a folder:");
		Optional<File> result = dialog.showAndWait();

		// Remove the chosen folder unless the user pressed "cancel".
		if (result.isPresent()) {
			directories.remove(result.get());
			writeDirectories();
			getController().getStatus().setText("Directory removed.");
			LOGGER.info("Directory removed. Remaining directories:" + directories);
			return true;
		}
		return false;
	}

	/**
	 * Prompts the user for a directory.
	 *
	 * @param stage
	 *            The stage that will hold the dialog box
	 * @return The directory specified by the user, or null if the user cancels
	 */
	private File chooseDirectory(Stage stage) {
		DirectoryChooser chooser = new DirectoryChooser();
		chooser.setTitle("Where are your songs?");
		return chooser.showDialog(stage);
	}

	/**
	 * Prompts the user for a directory.
	 *
	 * @param stage
	 *            The stage that will hold the dialog box
	 * @return A directory chosen by the user, or null if the user cancels
	 */
	private File initialDirectory(Stage stage) {
		// Alert the user that no directories were found
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("Welcome!");
		alert.setHeaderText(null);
		alert.setContentText("Hi there! It seems like you don't have any directories set up. "
				+ "That usually happens when you run this for the first time. "
				+ "If that's the case, let's find your songs!");
		alert.showAndWait();

		// Begin building up a data structure to store directories
		File chosenDirectory = chooseDirectory(stage);
		if (chosenDirectory == null) {
			LOGGER.info("User pressed 'cancel' when asked to choose a directory.");
			return null;
		} else {
			return chosenDirectory;
		}
	}

	/**
	 * Reads directories from the options file.
	 * 
	 * @return A set containing the directories
	 */
	private Set<File> readDirectories() {
		return getOptions().getDirectories();
	}

	/**
	 * Writes directories to the options file.
	 */
	private void writeDirectories() {
		getOptions().setDirectories(directories);
	}

	/**
	 * Takes in a directory and recursively searches for all music files
	 * contained within that directory. The files are then constructed as Song
	 * objects to be wrapped up in a collection.
	 *
	 * @param directory
	 * @return A collection containing all the Song objects.
	 */
	private Collection<Song> getSongs(File directory) {
		// Initialization
		Collection<Song> songs = HashMultiset.create();
		Collection<Future<Song>> futures = HashMultiset.create();
		ExecutorService executor = Executors.newWorkStealingPool();

		// If the directory is null, or not a directory, return an empty
		// collection
		if (directory == null || !directory.isDirectory()) {
			LOGGER.error("Failed to access directory: " + directory + ", skipping...");
			return songs;
		}

		try (Stream<Path> str = Files.walk(directory.toPath())) {
			// Depth first search for all supported music files
			str.filter(path -> FilenameUtils.getExtension(path.toString()).matches("mp3|mp4|m4a|wav"))
					.forEach(path -> futures.add(executor.submit(() -> Songs.create(path.toFile()))));
			executor.shutdown();

			// Add them to the song collection
			for (Future<Song> fut : futures) {
				songs.add(fut.get(Defaults.TIMEOUT, TimeUnit.SECONDS));
			}
		} catch (IOException e) {
			LOGGER.error("Failed to access directory: " + directory, e);
		} catch (InterruptedException e) {
			LOGGER.error("Interrupted.", e);
			Thread.currentThread().interrupt();
		} catch (TimeoutException e) {
			LOGGER.error("Timed out.", e);
		} catch (ExecutionException e) {
			LOGGER.catching(e);
		}

		return songs;
	}

	/**
	 * Searches the working directory for .m3u files and creates a playlist out
	 * of each one. All of the created playlists are then wrapped into a
	 * collection and returned.
	 *
	 * @return All of the created playlists
	 */
	private Collection<Playlist> getPlaylists() {
		getPrimaryStage().hide();

		// Initialization
		Collection<Playlist> multiset = HashMultiset.create();
		FilenameFilter filter = (FilenameFilter) (dir, name) -> name.endsWith(".m3u");
		File[] fileList = new File(Defaults.PLAYLISTS_FOLDER).listFiles(filter);
		if (fileList == null) {
			LOGGER.error("Unable to access the working directory.");
			return multiset;
		}

		// Iterate through each file in the working directory.
		for (File f : fileList) {
			Playlist playlist = new Playlist(f);
			multiset.add(playlist);
		}

		getPrimaryStage().show();
		return multiset;
	}

	// ------------------- Exception Handling ------------------- //

	/**
	 * Displays a dialog box explaining what happened. Once the dialog box is
	 * closed, the program exits with exit code -1.
	 *
	 * @param e
	 *            An exception that should end the program
	 */
	private void exitWithAlert(Exception e) {
		Alert alert = new Alert(Alert.AlertType.ERROR);

		// Store the stack trace in a string.
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);

		// Create an alert to let the user know what happened.
		alert.setTitle("Fatal Error!");
		alert.setHeaderText(e.getClass().toString().substring(6) + ": " + e.getMessage());

		// Store the stack trace string in a textarea hidden by a "Show/Hide
		// Details" button.
		TextArea textArea = new TextArea(sw.toString());
		textArea.setEditable(false);
		textArea.setWrapText(false);
		textArea.setMaxWidth(Double.MAX_VALUE);
		textArea.setMaxHeight(Double.MAX_VALUE);

		GridPane.setVgrow(textArea, Priority.ALWAYS);
		GridPane.setHgrow(textArea, Priority.ALWAYS);
		GridPane gridPane = new GridPane();
		gridPane.setMaxWidth(Double.MAX_VALUE);
		gridPane.add(textArea, 0, 0);

		// Display the alert, then exit the program.
		alert.getDialogPane().setExpandableContent(gridPane);
		alert.showAndWait();
		System.exit(-1);
	}

	// ------------------- Getters and Setters ------------------- //

	/**
	 * Sets up the media player to perform a specified action at the end of
	 * every song.
	 *
	 * @param action
	 *            An action wrapped in a Runnable
	 */
	public void setEndOfSongAction(Runnable action) {
		mediaPlayer.setOnEndOfMedia(action);
	}

	public Song getNowPlaying() {
		return nowPlaying;
	}

	private void setNowPlaying(Song nowPlaying) {
		this.nowPlaying = nowPlaying;
	}

	public void setVolume(double value) {
		if (mediaPlayer != null) {
			mediaPlayer.setVolume(value);
		}
	}

	public Playlist getMasterPlaylist() {
		return masterPlaylist;
	}

	private void setMasterPlaylist(Playlist masterPlaylist) {
		this.masterPlaylist = masterPlaylist;
	}

	private static void setInstance(Player instance) {
		Player.instance = instance;
	}

	protected static Player getPlayer() {
		return instance;
	}

	protected PlayerController getController() {
		return controller;
	}

	private void setController(PlayerController controller) {
		this.controller = controller;
	}

	public Stage getPrimaryStage() {
		return primaryStage;
	}

	private void setPrimaryStage(Stage primaryStage) {
		this.primaryStage = primaryStage;
	}

	public Scene getScene() {
		return scene;
	}

	private void setScene(Scene scene) {
		this.scene = scene;
	}

	public boolean isInitialized() {
		return initialized.get();
	}

	public Options getOptions() {
		return options;
	}

	private void setOptions(Options options) {
		this.options = options;
	}

}

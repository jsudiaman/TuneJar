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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.ConcurrentHashMultiset;
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
	private MediaPlayer player;
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
		options = new Options();

		// Load the FXML file and display the interface.
		this.primaryStage = primaryStage;
		URL location = getClass().getResource(Defaults.PLAYER_FXML);
		FXMLLoader fxmlLoader = new FXMLLoader();
		Parent root = fxmlLoader.load(location.openStream());

		scene = new Scene(root, 1000, 600);
		String theme = Defaults.THEME_MAP.get(options.getTheme());
		scene.getStylesheets().add(theme);
		LOGGER.debug("Loaded theme: " + theme);

		primaryStage.setTitle("TuneJar");
		primaryStage.setScene(scene);
		primaryStage.getIcons().add(new Image(getClass().getResourceAsStream(Defaults.ICON)));

		// Load the directories. If none are present, prompt the user for one.
		directories = readDirectories();
		if (directories.isEmpty()) {
			File directory = initialDirectory(primaryStage);
			if (directory != null) {
				directories.add(directory);
			}
		}

		controller = fxmlLoader.getController();

		// Create and display a playlist containing all songs from each
		// directory.
		refresh();
		controller.getPlaylistMenu().loadPlaylist(masterPlaylist);

		// Save the directories.
		writeDirectories();

		// Load in all playlists from the working directory.
		Collection<Playlist> playlistSet = null;
		try {
			playlistSet = getPlaylists();
		} catch (IOException | NullPointerException e) {
			LOGGER.fatal("Failed to load playlists from the working directory.", e);
			exitWithAlert(e);
		}
		if (playlistSet != null) {
			playlistSet.forEach(controller.getPlaylistMenu()::loadPlaylist);
		}
		controller.focus(controller.getPlaylistTable(), 0);
		controller.getVolumeSlider().setValue(options.getVolume());

		// Finally, sort the song table.
		String[] sortBy = options.getSortOrder();
		controller.getSongTable().getSortOrder().clear();
		List<TableColumn<Song, ?>> sortOrder = controller.getSongTable().getSortOrder();
		for (String s : sortBy) {
			switch (s) {
			case "title":
				sortOrder.add(controller.getTitleColumn());
				break;
			case "artist":
				sortOrder.add(controller.getArtistColumn());
				break;
			case "album":
				sortOrder.add(controller.getAlbumColumn());
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
		primaryStage.hide();
		masterPlaylist = new Playlist("All Music");

		// Then add all songs found in the directories to the master playlist.
		if (directories != null) {
			ExecutorService executor = Executors.newWorkStealingPool();
			LOGGER.info("Found directories: " + directories);
			LOGGER.info("Populating the master playlist...");
			for (File directory : directories) {
				executor.submit(() -> masterPlaylist.addAll(getSongs(directory)));
			}
			executor.shutdown();
			try {
				if (!executor.awaitTermination(Defaults.GET_SONGS_TIMEOUT, TimeUnit.SECONDS)) {
					LOGGER.warn("Executor timed out.");
					controller.getStatus().setText("Timed out, some songs may be missing.");
				}
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				LOGGER.error("Thread was interrupted.", e);
				controller.getStatus().setText("Interrupted, some songs may be missing.");
			}
		}
		LOGGER.info("Refresh successful");
		primaryStage.show();
	}

	// ------------------- Media Player Controls ------------------- //

	/**
	 * Loads a song into the media player, then plays it.
	 *
	 * @param song
	 *            The song to play
	 */
	public void playSong(Song song) {
		if (nowPlaying == song) {
			resumeSong();
			return;
		} else if (nowPlaying != null) {
			stopSong();
		}
		nowPlaying = song;
		String uriString = new File(song.getAbsoluteFilename()).toURI().toString();
		try {
			player = new MediaPlayer(new Media(uriString));
			LOGGER.debug("Loaded song: " + uriString);
			setVolume(controller.getVolumeSlider().getValue());
			LOGGER.info("Playing: " + nowPlaying);
			player.play();
		} catch (MediaException e) {
			controller.getStatus().setText("Failed to play the song.");
			LOGGER.catching(Level.ERROR, e);
		}
	}

	/**
	 * Resumes the media player.
	 */
	public void resumeSong() {
		if (player != null && nowPlaying != null) {
			LOGGER.info("Resuming: " + nowPlaying);
			player.play();
		}
	}

	/**
	 * Pauses the media player.
	 */
	public void pauseSong() {
		if (player != null && nowPlaying != null) {
			LOGGER.info("Pausing: " + nowPlaying);
			player.pause();
		}
	}

	/**
	 * Stops the media player.
	 */
	public void stopSong() {
		if (player != null && nowPlaying != null) {
			LOGGER.info("Stopping: " + nowPlaying);
			player.stop();
		}
		nowPlaying = null;
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
		player.setOnEndOfMedia(action);
	}

	public Song getNowPlaying() {
		return nowPlaying;
	}

	public void setVolume(double value) {
		if (player != null) {
			player.setVolume(value);
		}
	}

	public Playlist getMasterPlaylist() {
		return masterPlaylist;
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

	public Stage getPrimaryStage() {
		return primaryStage;
	}

	public Scene getScene() {
		return scene;
	}

	public boolean isInitialized() {
		return initialized.get();
	}

	public Options getOptions() {
		return options;
	}

	// ------------------- File Manipulation ------------------- //

	/**
	 * Adds a user-selected directory to the directory collection.
	 */
	public void addDirectory() {
		File directory = chooseDirectory(primaryStage);
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
			controller.getStatus().setText("No folders found.");
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
			controller.getStatus().setText("Directory removed.");
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
		return options.getDirectories();
	}

	/**
	 * Writes directories to the options file.
	 */
	private void writeDirectories() {
		options.setDirectories(directories);
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
		Collection<Song> songs = ConcurrentHashMultiset.create();
		ExecutorService executor = Executors.newWorkStealingPool();

		// If the directory is null, or not a directory, return an empty
		// collection
		if (directory == null || !directory.isDirectory()) {
			LOGGER.error("Failed to access directory: " + (directory == null ? "null" : directory) + ", skipping...");
			return songs;
		}

		// If the file list is null, return an empty collection
		File[] files = directory.listFiles();
		if (files == null)
			return songs;

		// Iterate through each file in the directory.
		for (File f : files) {
			if (f.isDirectory()) {
				songs.addAll(getSongs(f));
			} else {
				executor.submit(() -> {
					try {
						Song song = Songs.create(f);
						if (song != null)
							songs.add(song);
					} catch (Exception e) {
						LOGGER.error("Failed to construct a song object from file: " + f, e);
					}
				});
			}
		}

		executor.shutdown();
		try {
			if (!executor.awaitTermination(Defaults.GET_SONGS_TIMEOUT, TimeUnit.SECONDS)) {
				LOGGER.warn("Executor timed out.");
				controller.getStatus().setText("Timed out, some songs may be missing.");
			}
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			LOGGER.error("Thread was interrupted.", e);
			controller.getStatus().setText("Interrupted, some songs may be missing.");
		}
		return songs;
	}

	/**
	 * Searches the working directory for .m3u files and creates a playlist out
	 * of each one. All of the created playlists are then wrapped into a
	 * collection and returned.
	 *
	 * @return All of the created playlists
	 *
	 * @throws IOException
	 *             Unable to access the working directory
	 */
	private Collection<Playlist> getPlaylists() throws IOException {
		primaryStage.hide();

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
			try {
				Playlist playlist = new Playlist(f);
				multiset.add(playlist);
			} catch (TimeoutException e) {
				LOGGER.error("Executor timed out.", e);
				controller.getStatus().setText("Timed out, some playlists may be missing.");
			} catch (InterruptedException e) {
				LOGGER.error("Thread was interrupted.", e);
				controller.getStatus().setText("Interrupted, some playlists may be missing.");
			}
		}

		primaryStage.show();
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

}

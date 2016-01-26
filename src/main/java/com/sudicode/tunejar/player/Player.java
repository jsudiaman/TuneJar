package com.sudicode.tunejar.player;

import com.google.common.collect.HashMultiset;
import com.sudicode.tunejar.config.Defaults;
import com.sudicode.tunejar.config.Options;
import com.sudicode.tunejar.song.Playlist;
import com.sudicode.tunejar.song.Song;
import com.sudicode.tunejar.song.Songs;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
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
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
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

public class Player extends Application {

	// Static
	private static Player instance;
	private static final Logger LOGGER = LoggerFactory.getLogger(Player.class);

	// GUI
	private MediaPlayer mediaPlayer;
	private Song nowPlaying;
	private Stage primaryStage;
	private Scene scene;
	private PlayerController controller;

	// Data
	private AtomicBoolean initialized;
	private Playlist masterPlaylist;
	private Set<File> directories;
	private Options options;

	/**
	 * Deletes old log files, then starts the application.
	 *
	 * @param args The command line arguments
	 */
	public static void main(String[] args) {
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
	 * @param primaryStage The stage that will hold the interface
	 */
	@Override
	public void start(Stage primaryStage) {
		try {
			init(primaryStage);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			exitWithAlert(e);
		}
	}

	/**
	 * Handles program initialization.
	 *
	 * @param stage The stage that will hold the interface
	 * @throws IOException Failed to load the FXML, or could not load/save a file.
	 */
	private void init(Stage stage) throws IOException {
		// Initialization.
		setInstance(this);
		setOptions(new Options());

		// Load the FXML file and display the interface.
		this.setPrimaryStage(stage);
		URL location = getClass().getResource(Defaults.PLAYER_FXML);
		FXMLLoader fxmlLoader = new FXMLLoader();
		Parent root = fxmlLoader.load(location.openStream());

		setScene(new Scene(root, 1000, 600));
		setController(fxmlLoader.getController());
		String theme = Defaults.THEME_MAP.get(getOptions().getTheme());
		getScene().getStylesheets().add(theme);
		LOGGER.debug("Loaded theme: " + theme);

		getPrimaryStage().setTitle("TuneJar");
		getPrimaryStage().setScene(getScene());
		getPrimaryStage().getIcons().add(new Image(getClass().getResourceAsStream(Defaults.ICON)));
		getPrimaryStage().show();

		// Load the directories. If none are present, prompt the user for one.
		directories = readDirectories();
		if (directories.isEmpty()) {
			File directory = initialDirectory(getPrimaryStage());
			if (directory != null) {
				directories.add(directory);
			}
		}
		writeDirectories();

		// Set the sort order.
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

		// Create and display a playlist containing all songs from each
		// directory.
		refresh();
	}

	/**
	 * First, adds all music files that can be found in available directories to
	 * the master playlist. Then loads all available playlists from the working
	 * directory.
	 */
	public void refresh() {
		Task<?> refresher = new Refresher();
		refresher.progressProperty().addListener((obs, oldVal, newVal) ->
				getController().getStatus().setText(refresher.getMessage() + new DecimalFormat("#0%").format(newVal))
		);
		ExecutorService executor = Executors.newSingleThreadExecutor();
		Future<?> future = executor.submit(refresher);
		executor.shutdown();
		new Thread(() -> {
			try {
				future.get(Defaults.TIMEOUT, TimeUnit.SECONDS);
			} catch (ExecutionException | InterruptedException | TimeoutException e) {
				LOGGER.error(e.getMessage(), e);
				Platform.runLater(() ->
						getController().getStatus().setText("An error has occurred: " + e.getClass().getSimpleName())
				);
			}
		}).start();
	}

	/**
	 * Inner class designed to handle expensive operations invoked by the
	 * <code>refresh()</code> method.
	 */
	private class Refresher extends Task<Void> {
		/**
		 * The main task associated with the <code>refresh()</code> method. This
		 * is an expensive call, so it is <b>not</b> recommended to run it on
		 * the GUI thread.
		 */
		@Override
		protected Void call() throws Exception {
			refreshMasterPlaylist();
			Collection<Playlist> playlists = getPlaylists();

			// Refresh the view.
			Platform.runLater(() -> {
				if (!isInitialized()) {
					getController().getPlaylistMenu().loadPlaylist(getMasterPlaylist());
					playlists.forEach(getController().getPlaylistMenu()::loadPlaylist);
					getController().getVolumeSlider().setValue(getOptions().getVolume());
				} else {
					getController().getPlaylistList().set(0, getMasterPlaylist());
				}
				getController().refreshTables();
				getController().focus(getController().getPlaylistTable(), 0);
				getController().getStatus().setText("");
				setInitialized(true);
			});

			return null;
		}

		/**
		 * Clears the master playlist, then constructs a new one out of all
		 * supported audio files found in the set of directories.
		 */
		private void refreshMasterPlaylist() throws InterruptedException, ExecutionException {
			setMasterPlaylist(new Playlist("All Music"));
			if (directories != null) {
				LOGGER.info("Found directories: " + directories);
				LOGGER.info("Populating the master playlist...");

				Collection<Future<Song>> sFutures = getFutures(directories);
				long workDone = 0;
				long max = sFutures.size();
				updateMessage("Updating songs... ");
				for (Future<Song> song : sFutures) {
					getMasterPlaylist().add(song.get());
					updateProgress(++workDone, max);
				}
			}
		}

		/**
		 * Constructs playlists out of all m3u files found in the playlists
		 * folder. The constructed playlists are then wrapped into a collection.
		 *
		 * @return The collection of constructed playlists.
		 */
		private Collection<Playlist> getPlaylists() throws InterruptedException, ExecutionException {
			Collection<Playlist> playlists = HashMultiset.create();
			if (!isInitialized()) {
				Collection<Future<Playlist>> pFutures = HashMultiset.create();
				ExecutorService outerExec = Executors.newWorkStealingPool();

				// Iterate through each file in the working directory.
				FilenameFilter filter = (dir, name) -> name.endsWith(".m3u");
				File[] fileList = new File(Defaults.PLAYLISTS_FOLDER).listFiles(filter);
				if (fileList == null) {
					LOGGER.error("Unable to access the working directory.");
				} else {
					for (File f : fileList) {
						pFutures.add(outerExec.submit(() -> createPlaylist(f)));
					}
				}
				outerExec.shutdown();
				for (Future<Playlist> playlist : pFutures) {
					playlists.add(playlist.get());
				}
			}
			return playlists;
		}

		/**
		 * Creates a playlist out of an m3u file.
		 */
		private Playlist createPlaylist(File m3uFile) throws IOException, InterruptedException, ExecutionException {
			Playlist playlist;
			playlist = new Playlist(m3uFile.getName().substring(0, m3uFile.getName().lastIndexOf(".m3u")));
			Collection<Future<Song>> sFutures = HashMultiset.create();

			// Get each song, line by line.
			try (BufferedReader reader = new BufferedReader(new FileReader(m3uFile))) {
				ExecutorService innerExec = Executors.newWorkStealingPool();
				for (String nextLine; (nextLine = reader.readLine()) != null; ) {
					final String s = nextLine;
					sFutures.add(innerExec.submit(() -> Songs.create(new File(s))));
				}
				innerExec.shutdown();
			}

			// Add each song to the playlist.
			long workDone = 0;
			long max = sFutures.size();
			for (Future<Song> song : sFutures) {
				updateMessage("Updating " + playlist.getName() + "...");
				playlist.add(song.get());
				updateProgress(++workDone, max);
			}
			return playlist;
		}

	}

	// ------------------- Media Player Controls ------------------- //

	/**
	 * Loads a song into the media player, then plays it.
	 *
	 * @param song The song to play
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
			LOGGER.error(e.getMessage(), e);
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
			LOGGER.error(e.getMessage(), e);
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
	 * @param stage The stage that will hold the dialog box
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
	 * @param stage The stage that will hold the dialog box
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
	 * Traverses each directory, obtaining all supported audio files. Each audio
	 * file found is wrapped in a Future Song, which is then added to a
	 * collection.
	 *
	 * @return The collection of Future Songs
	 */
	private Collection<Future<Song>> getFutures(Collection<File> directories) {
		// Initialization
		Collection<Future<Song>> futures = HashMultiset.create();
		ExecutorService executor = Executors.newWorkStealingPool();

		// Loop through directories
		for (File directory : directories) {
			if (directory == null || !directory.isDirectory()) {
				LOGGER.error("Failed to access directory: " + directory + ", skipping...");
				continue;
			}

			// Depth first search through each directory for supported files
			try (Stream<Path> str = Files.walk(directory.toPath())) {
				str.filter(path -> FilenameUtils.getExtension(path.toString()).matches("mp3|mp4|m4a|wav"))
						.forEach(path -> futures.add(executor.submit(() -> Songs.create(path.toFile()))));
			} catch (IOException e) {
				LOGGER.error("Failed to access directory: " + directory, e);
			}
		}
		executor.shutdown();

		return futures;
	}

	// ------------------- Exception Handling ------------------- //

	/**
	 * Displays a dialog box explaining what happened. Once the dialog box is
	 * closed, the program exits with exit code -1.
	 *
	 * @param e An exception that should end the program
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
	 * @param action An action wrapped in a Runnable
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

	public void setInitialized(boolean initialized) {
		if (this.initialized == null)
			this.initialized = new AtomicBoolean(initialized);
		else
			this.initialized.set(initialized);
	}

	public boolean isInitialized() {
		if (this.initialized == null)
			this.initialized = new AtomicBoolean();
		return initialized.get();
	}

	public Options getOptions() {
		return options;
	}

	private void setOptions(Options options) {
		this.options = options;
	}

}

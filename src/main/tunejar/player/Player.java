package tunejar.player;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.ConcurrentHashMultiset;
import com.google.common.collect.HashMultiset;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.media.Media;
import javafx.scene.media.MediaException;
import javafx.scene.media.MediaPlayer;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import tunejar.config.Constants;
import tunejar.menu.PlaylistMenu;
import tunejar.song.Playlist;
import tunejar.song.Song;
import tunejar.song.SongFactory;

public class Player extends Application {

	// Singleton Object
	private static Player instance;

	private static final Logger LOGGER = LogManager.getLogger();

	// GUI
	private MediaPlayer player;
	private Song nowPlaying;
	private Stage primaryStage;
	private PlayerController controller;

	// Data
	private Playlist masterPlaylist;
	private Set<File> directories;

	public Player() {
		if (instance != null)
			throw new IllegalStateException("An instance of this object already exists.");
	}

	/**
	 * Calls {@link Application#launch(String...)}.
	 *
	 * @param args
	 *            The command line arguments
	 */
	public static void main(String[] args) {
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
		setInstance(this);
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
		// Load the FXML file and display the interface.
		this.primaryStage = primaryStage;
		URL location = getClass().getResource("Player.fxml");
		FXMLLoader fxmlLoader = new FXMLLoader();
		Parent root = fxmlLoader.load(location.openStream());

		Scene scene = new Scene(root, 1000, 600);
		scene.getStylesheets().add(getClass().getResource("DarkTheme.css").toString());

		primaryStage.setTitle("TuneJar");
		primaryStage.setScene(scene);
		primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("icon.png")));

		// Load the directories. If none are present, prompt the user for one.
		try {
			directories = readDirectories();
		} catch (FileNotFoundException e) {
			directories = new HashSet<>();
			File directory = initialDirectory(primaryStage);
			if (directory != null) {
				directories.add(directory);
			}
		}

		controller = fxmlLoader.getController();

		// Create and display a playlist containing all songs from each
		// directory.
		refresh();
		PlaylistMenu.getInstance().loadPlaylist(masterPlaylist);

		// Save the directories.
		try {
			writeFiles(directories);
			controller.getStatus().setText("");
		} catch (IOException e) {
			LOGGER.error("Failed to save directories.", e);
			controller.getStatus().setText("Failed to save directories.");
		}

		// Finally, load in all playlists from the working directory.
		Collection<Playlist> playlistSet = null;
		try {
			playlistSet = getPlaylists();
		} catch (IOException | NullPointerException e) {
			LOGGER.fatal("Failed to load playlists from the working directory.", e);
			exitWithAlert(e);
		}
		if (playlistSet != null) {
			playlistSet.forEach(PlaylistMenu.getInstance()::loadPlaylist);
		}
		controller.focus(controller.getPlaylistTable(), 0);
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
				executor.awaitTermination(Constants.GET_SONGS_TIMEOUT, TimeUnit.SECONDS);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				LOGGER.error("Thread was interrupted.", e);
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
	public void load(Song song) {
		if (nowPlaying != null) {
			nowPlaying.stop();
		}
		nowPlaying = song;
		String uriString = new File(song.getAbsoluteFilename()).toURI().toString();
		try {
			player = new MediaPlayer(new Media(uriString));
			LOGGER.debug("Loaded: " + uriString);
		} catch (MediaException e) {
			controller.getStatus().setText("Failed to play the song.");
			LOGGER.catching(Level.ERROR, e);
		}
		player.setVolume(PlayerController.getInstance().getVolumeSlider().getValue());
		LOGGER.info("Playing: " + nowPlaying);
		player.play();
	}

	/**
	 * Resumes the media player.
	 */
	public void resumePlayback() {
		if (player != null && nowPlaying != null) {
			LOGGER.info("Resuming: " + nowPlaying);
			player.play();
		}
	}

	/**
	 * Pauses the media player.
	 */
	public void pausePlayback() {
		if (player != null && nowPlaying != null) {
			LOGGER.info("Pausing: " + nowPlaying);
			player.pause();
		}
	}

	/**
	 * Stops the media player.
	 */
	public void stopPlayback() {
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

	public static Player getInstance() {
		return instance;
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
			writeFiles(directories);
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
			try {
				writeFiles(directories);
				controller.getStatus().setText("Directory removed.");
				LOGGER.info("Directory removed. Remaining directories:" + directories);
				return true;
			} catch (IOException e) {
				controller.getStatus().setText("Failed to remove directory.");
				LOGGER.catching(Level.ERROR, e);
				return false;
			}
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
	 * Read in a list of directories, line by line, from a text file.
	 *
	 * @return A collection containing all of the specified directories
	 * @throws IOException
	 *             The file cannot be found or accessed
	 */
	private Set<File> readDirectories() throws IOException {
		Set<File> dirSet = new HashSet<>();

		// Read in the directories line by line.
		BufferedReader reader = new BufferedReader(new FileReader(Constants.DIRECTORY_FILENAME));
		for (String nextLine; (nextLine = reader.readLine()) != null;) {
			dirSet.add(new File(nextLine));
		}

		// Close the text file.
		reader.close();
		return dirSet;
	}

	/**
	 * Output the contents of a collection of files, line by line.
	 *
	 * @param files
	 *            A collection of files
	 * @throws IOException
	 *             Unable to write the output to the file
	 */
	private void writeFiles(Collection<File> files) throws IOException {
		BufferedWriter writer = new BufferedWriter(new FileWriter(Constants.DIRECTORY_FILENAME, false));
		for (File f : files) {
			writer.write(f.getAbsoluteFile().toString());
			writer.newLine();
		}
		writer.close();
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
						Song song = SongFactory.getInstance().fromFile(f);
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
			executor.awaitTermination(Constants.GET_SONGS_TIMEOUT, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			LOGGER.error("Thread was interrupted.", e);
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
		File[] fileList = new File(".").listFiles();
		if (fileList == null) {
			LOGGER.error("Unable to access the working directory.");
			return multiset;
		}

		// Iterate through each file in the working directory.
		for (File f : fileList) {
			if (f.toString().endsWith(".m3u")) {
				Playlist playlist = new Playlist(f);
				multiset.add(playlist);
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

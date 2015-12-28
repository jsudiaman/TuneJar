package tunejar.menu;

import java.io.File;
import java.io.FileFilter;
import java.net.MalformedURLException;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.scene.control.MenuItem;
import tunejar.config.Defaults;
import tunejar.config.Options;
import tunejar.player.Player;
import tunejar.player.PlayerController;

public class ThemeMenu {

	private static final ThemeMenu INSTANCE = new ThemeMenu();
	private static final Logger LOGGER = LogManager.getLogger();

	public void init() {
		File[] themeFiles = new File(Defaults.THEME_DIR)
				.listFiles((FileFilter) file -> file.getName().endsWith(".css"));
		if (themeFiles != null) {
			for (File themeFile : themeFiles) {
				String themeName = themeFile.getName().substring(0, themeFile.getName().lastIndexOf(".css"));
				MenuItem nextItem = new MenuItem(themeName);
				nextItem.setOnAction(event -> {
					try {
						Player.getInstance().getScene().getStylesheets().set(0, themeFile.toURI().toURL().toString());
						Options.getInstance().setTheme(themeName);
					} catch (MalformedURLException e) {
						LOGGER.catching(Level.ERROR, e);
						PlayerController.getInstance().getStatus().setText("Failed to change theme.");
					}
				});
				PlayerController.getInstance().getThemeMenu().getItems().add(nextItem);
			}
		} else {
			LOGGER.error("List of theme files was null.");
			PlayerController.getInstance().getStatus().setText("Failed to change theme.");
		}
	}

	public static ThemeMenu getInstance() {
		return INSTANCE;
	}

}

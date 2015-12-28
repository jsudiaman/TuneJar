package tunejar.menu;

import javafx.scene.control.MenuItem;
import tunejar.config.Defaults;
import tunejar.config.Options;
import tunejar.player.Player;
import tunejar.player.PlayerController;

public class ThemeMenu {

	private static final ThemeMenu INSTANCE = new ThemeMenu();

	public void init() {
		for (String theme : Defaults.THEME_MAP.keySet()) {
			MenuItem nextItem = new MenuItem(theme);
			nextItem.setOnAction(event -> {
				Player.getInstance().getScene().getStylesheets().set(0, Defaults.THEME_MAP.get(theme));
				Options.getInstance().setTheme(theme);
			});
			PlayerController.getInstance().getThemeMenu().getItems().add(nextItem);
		}
	}

	public static ThemeMenu getInstance() {
		return INSTANCE;
	}

}

package com.sudicode.tunejar.menu;

import com.sudicode.tunejar.config.Defaults;
import com.sudicode.tunejar.player.PlayerController;
import javafx.scene.control.MenuItem;

public class ThemeMenu extends PlayerMenu {

    public ThemeMenu(PlayerController controller) {
        super(controller);
    }

    public void init() {
        for (String theme : Defaults.THEME_MAP.keySet()) {
            MenuItem nextItem = new MenuItem(theme);
            nextItem.setOnAction(event -> {
                controller.getPlayer().getScene().getStylesheets().set(0, Defaults.THEME_MAP.get(theme));
                controller.getPlayer().getOptions().setTheme(theme);
            });
            controller.getThemeSelector().getItems().add(nextItem);
        }
    }

}

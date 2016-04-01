package com.sudicode.tunejar.menu;

import com.sudicode.tunejar.config.Defaults;
import com.sudicode.tunejar.player.PlayerController;

import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.ToggleGroup;

public class ThemeMenu extends PlayerMenu {

    public ThemeMenu(PlayerController controller) {
        super(controller);
    }

    public void init() {
        ToggleGroup group = new ToggleGroup();
        for (String theme : Defaults.THEME_MAP.keySet()) {
            RadioMenuItem nextItem = new RadioMenuItem(theme);
            nextItem.setOnAction(event -> {
                controller.getPlayer().getScene().getStylesheets().set(0, Defaults.THEME_MAP.get(theme));
                controller.getPlayer().getOptions().setTheme(theme);
            });
            nextItem.setToggleGroup(group);
            if (nextItem.getText().equals(controller.getPlayer().getOptions().getTheme())) {
                group.selectToggle(nextItem);
            }
            controller.getThemeSelector().getItems().add(nextItem);
        }
    }

}

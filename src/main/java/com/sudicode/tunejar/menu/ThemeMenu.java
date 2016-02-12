/*
 * TuneJar <http://sudicode.com/tunejar/>
 * Copyright (C) 2016 Jonathan Sudiaman
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

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

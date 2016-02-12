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

import com.sudicode.tunejar.player.PlayerController;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;

public class VolumeMenu extends PlayerMenu {

    private static final Logger logger = LoggerFactory.getLogger(VolumeMenu.class);

    public VolumeMenu(PlayerController controller) {
        super(controller);
    }

    public void init() {
        DoubleProperty dProp = controller.getVolumeSlider().valueProperty();
        dProp.addListener((obs, oldV, newV) -> controller.getPlayer().setVolume(newV.doubleValue()));

        // Preserve SSD life by only writing upon slider release.
        BooleanProperty bProp = controller.getVolumeSlider().valueChangingProperty();
        bProp.addListener((obs, oldV, newV) -> {
            logger.trace("Volume valueChangingProperty listener invoked. New value: {}", newV);
            if (!newV) {
                logger.trace("Saving new volume ({}) to options file.", controller.getVolumeSlider().getValue());
                controller.getPlayer().getOptions().setVolume(controller.getVolumeSlider().getValue());
            }
        });
        controller.getVolumeSlider().setOnMouseReleased((event) -> {
            logger.trace("Volume onMouseReleased function invoked.");
            logger.trace("Saving new volume ({}) to options file.", controller.getVolumeSlider().getValue());
            controller.getPlayer().getOptions().setVolume(controller.getVolumeSlider().getValue());
        });
    }

}

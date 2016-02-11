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

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;

public class VolumeMenu extends PlayerMenu {

    public VolumeMenu(PlayerController controller) {
        super(controller);
    }

    public void init() {
        DoubleProperty dProp = controller.getVolumeSlider().valueProperty();
        dProp.addListener((obs, oldV, newV) -> controller.getPlayer().setVolume(newV.doubleValue()));

        // Preserve SSD life by only writing upon slider release.
        BooleanProperty bProp = controller.getVolumeSlider().valueChangingProperty();
        bProp.addListener((obs, oldV, newV) -> {
            if (!newV)
                controller.getPlayer().getOptions().setVolume(controller.getVolumeSlider().getValue());
        });
    }

}

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

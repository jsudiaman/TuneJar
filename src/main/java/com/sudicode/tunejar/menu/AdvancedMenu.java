package com.sudicode.tunejar.menu;

import com.sudicode.tunejar.player.PlayerController;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.prefs.BackingStoreException;

public class AdvancedMenu extends PlayerMenu {

    private static final Logger logger = LoggerFactory.getLogger(AdvancedMenu.class);

    public AdvancedMenu(PlayerController controller) {
        super(controller);
    }

    public void reset() {
        Alert resetAlert = new Alert(AlertType.CONFIRMATION);
        resetAlert.setTitle("Reset TuneJar");
        resetAlert.setHeaderText("Confirm Reset");
        resetAlert.setContentText("Preferences will be reset, playlists will be cleared, and TuneJar will restart. Are you sure you would like to reset?");

        resetAlert.showAndWait().ifPresent(buttonType -> {
            if (buttonType.equals(ButtonType.OK)) {
                try {
                    controller.getPlayer().getOptions().clear();
                    logger.info("Options cleared.");
                    controller.getPlayer().restart();
                } catch (BackingStoreException e) {
                    logger.error("Failed to reset TuneJar.", e);
                    controller.getStatus().setText("Failed to reset TuneJar.");
                }
            }
        });
    }

}

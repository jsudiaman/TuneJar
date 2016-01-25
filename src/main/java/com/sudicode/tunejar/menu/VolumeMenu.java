package com.sudicode.tunejar.menu;

import com.sudicode.tunejar.player.PlayerController;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.value.ChangeListener;

public class VolumeMenu extends PlayerMenu {

	public VolumeMenu(PlayerController controller) {
		super(controller);
	}

	public void init() {
		DoubleProperty dProp = controller.getVolumeSlider().valueProperty();
		dProp.addListener((obs, oldV, newV) -> controller.getPlayer().setVolume(newV.doubleValue()));

		// Preserve SSD life by only writing upon slider release.
		BooleanProperty bProp = controller.getVolumeSlider().valueChangingProperty();
		bProp.addListener((ChangeListener<Boolean>) (obs, oldV, newV) -> {
			if (!newV)
				controller.getPlayer().getOptions().setVolume(controller.getVolumeSlider().getValue());
		});
	}

}

package com.sudicode.tunejar.menu;

import com.sudicode.tunejar.player.PlayerController;

import javafx.beans.value.ChangeListener;

public class VolumeMenu extends PlayerMenu {

	public VolumeMenu(PlayerController controller) {
		super(controller);
	}

	public void init() {
		controller.getVolumeSlider().valueProperty()
				.addListener((obs, oldV, newV) -> controller.getPlayer().setVolume(newV.doubleValue()));

		// Preserve SSD life by only writing upon slider release.
		controller.getVolumeSlider().valueChangingProperty()
				.addListener((ChangeListener<Boolean>) (obs, oldV, newV) -> {
					if (!newV)
						controller.getPlayer().getOptions().setVolume(controller.getVolumeSlider().getValue());
				});
	}

}

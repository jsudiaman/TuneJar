package tunejar.menu;

import javafx.beans.value.ChangeListener;
import tunejar.config.Options;
import tunejar.player.Player;
import tunejar.player.PlayerController;

public class VolumeMenu {

	private static final VolumeMenu INSTANCE = new VolumeMenu();

	public void init() {
		PlayerController.getInstance().getVolumeSlider().valueProperty()
				.addListener((obs, oldV, newV) -> Player.getInstance().setVolume(newV.doubleValue()));

		// Preserve SSD life by only writing upon slider release.
		PlayerController.getInstance().getVolumeSlider().valueChangingProperty()
				.addListener((ChangeListener<Boolean>) (obs, oldV, newV) -> {
					if (!newV)
						Options.getInstance().setVolume(PlayerController.getInstance().getVolumeSlider().getValue());
				});
	}

	public static VolumeMenu getInstance() {
		return INSTANCE;
	}

}

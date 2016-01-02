package tunejar.menu;

import tunejar.player.PlayerController;

public abstract class PlayerMenu {

	protected PlayerController controller;

	protected PlayerMenu(PlayerController controller) {
		this.controller = controller;
	}

}

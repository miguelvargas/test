package com.mvw.command;

import com.mvw.controller.CommandController;
import com.mvw.event.CommandEvent;

public abstract class Command {

	private CommandController controller;

	public CommandController getController() {
		return controller;
	}

	public void setController(CommandController controller) {
		this.controller = controller;
	}

	/*
	 * This method will be called when a configured event is fired.
	 */
	public abstract void execute(CommandEvent event);

}

package com.mvw.command;

import com.mvw.event.CommandEvent;

public interface Command {
	/*
	 * This method will be called when a configured event is fired.
	 */
	public void execute(CommandEvent event);

}

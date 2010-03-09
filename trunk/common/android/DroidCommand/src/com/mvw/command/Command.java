package com.mvw.command;

import com.mvw.event.CommandEvent;

public interface Command {
	public Object execute(CommandEvent event);

}

package com.mvw.controller;

import com.mvw.command.Command;
import com.mvw.event.CommandEvent;

public class CommandMapping {
	private CommandEvent event;
	private Command command;
	
	public CommandMapping(Command cmd, CommandEvent _event) {
		event = _event;
		command = cmd;
	}
	public CommandEvent getEvent() {
		return event;
	}
	public void setEvent(CommandEvent event) {
		this.event = event;
	}
	public Command getCommand() {
		return command;
	}
	public void setCommand(Command command) {
		this.command = command;
	}
}

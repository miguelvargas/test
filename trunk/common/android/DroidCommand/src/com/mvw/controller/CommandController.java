package com.mvw.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.mvw.command.Command;
import com.mvw.event.CommandEvent;
import com.mvw.event.EventDispatcher;

/*
 * Controller for routing events to listeners and commands
 */
@SuppressWarnings("unchecked")
public class CommandController extends EventDispatcher {

	ExecutorService threadPool;

	Map<String, List<Class>> commandMaps = new HashMap<String, List<Class>>();
	public CommandController() {
		threadPool = getExecutorService();
	}

	/*
	 * Custom threadpool manager, default users cachedThreadPool
	 */
	public CommandController(ExecutorService pool) {
		threadPool = pool;
	}

	/*
	 * Adds the command to the controller
	 */
	public void addCommand(Class cmd, String eventType) {
		List<Class> commands = commandMaps.get(eventType);
		if (commands == null) {
			commands = new ArrayList<Class>();
			commandMaps.put(eventType, commands);
		}
		commands.add(cmd); // add to the map
	}

	public boolean removeCommand(Class cmd, String eventType) {
		if (!commandMaps.containsKey(eventType)) {
			return false;
		}
		List<Class> cmdlist = commandMaps.get(eventType);
		if (cmdlist == null) {
			return false;
		}
		return cmdlist.remove(cmd);

	}
	
	public void dispatchEvent(final CommandEvent event) {
		super.dispatchEvent(event);
		
		// now process all commands
		// Log.d(Tag,"dispatching event "+ event.getType());
		List<Class> commandClasses = commandMaps.get(event.getType());
		if (commandClasses == null || commandClasses.size() == 0) {
			// Log.d(Tag,"no commands registered, skip any action on event "+event.getType());
			return;
		}
		// run each commmand defined for this type of event
		for (final Class cmdClass : commandClasses) {
			try {
				final Command command = (Command) cmdClass.newInstance();
				command.setController(this);
				if (event.isAsync()) {
					threadPool.execute(new Runnable() {
						@Override
						public void run() {
							command.execute(event);
						}
					});
				} else {
					command.execute(event);
				}
			} catch (InstantiationException e) {
				throw new RuntimeException(e);
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		}
	}

	private ExecutorService getExecutorService() {
		return Executors.newCachedThreadPool();
	}

	public void addCommand(Class cmd, CommandEvent event) {
		addCommand(cmd, event.getType());

	}

	public void removeCommand(Class cmd, CommandEvent event) {
		removeCommand(cmd, event.getType());
	}

}

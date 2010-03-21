package com.mvw.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.mvw.command.Command;
import com.mvw.event.CommandEvent;
import com.mvw.event.EventListener;

/*
 * Controller for routing events to listeners and commands
 */
@SuppressWarnings("unchecked")
public class CommandController {

	private ExecutorService threadPool;

	private Map<String, List<Class>> commandMaps = new HashMap<String, List<Class>>();
	private Map<String, List<EventListener>> listeners = new HashMap<String, List<EventListener>>();

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

	/*
	 * Clients will call this to launch an event, then we will call the correct
	 * command
	 */
	public void dispatchEvent(final CommandEvent event) {

		// first call all listeners
		List<EventListener> elist = listeners.get(event.getType());
		if (elist != null && elist.size() > 0) {
			for (EventListener listener : elist) {
				listener.onEvent(event);
			}
		}

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

	/*
	 * Threadpool stategy, simple to start with
	 */

	private ExecutorService getExecutorService() {
		return Executors.newCachedThreadPool();
	}

	public void addCommand(Class cmd, CommandEvent event) {
		addCommand(cmd, event.getType());

	}

	public void removeCommand(Class cmd, CommandEvent event) {
		removeCommand(cmd, event.getType());
	}

	/*
	 * add a listener for an event type
	 */
	public void addEventListener(EventListener listner, String type) {

		synchronized (listeners) {
			List<EventListener> list = listeners.get(type);
			if (list == null) {
				list = new ArrayList<EventListener>();
				listeners.put(type, list);
			}
			list.add(listner);
		}
	}

	/*
	 * Remove a listener for an eventtype Note: Removes only one, call for each
	 * one that was added
	 * 
	 * Returns: true/false - if the listener was removed
	 */
	public boolean removeEventListener(EventListener listener, String type) {
		synchronized (listeners) {
			if (!listeners.containsKey(type)) {
				return false;
			}
			List<EventListener> list = listeners.get(type);
			return list.remove(listener);
		}
	}

}

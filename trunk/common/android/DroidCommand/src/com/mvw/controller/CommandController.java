package com.mvw.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.mvw.command.Command;
import com.mvw.event.CommandEvent;

@SuppressWarnings("unchecked")
public class CommandController {

	String Tag = CommandController.class.getSimpleName();
	
	ExecutorService threadPool;
	
	private Map<String, List<Class>> commandMaps = new HashMap<String , List<Class>>();
	
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
		commands.add(cmd); //add to the map
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
	 * Clients will call this to launch an event, then we will call the correct command
	 */
	public void dispatchEvent(final CommandEvent event) {
	//	Log.d(Tag,"dispatching event "+ event.getType());
		List<Class> commands = commandMaps.get(event.getType());
		if (commands == null) {
	//		Log.e(Tag, "Skipping event, no command map found, maybe this should not happen");
			return;
		}
		if (commands.size() == 0) {
	//		Log.d(Tag,"no commands registered, skip any action on event "+event.getType());
			return;
		}
		// run each commmand defined for this type of event
		for (final Class cmd : commands) {
			if (event.isAsync()) {
				threadPool.execute(new Runnable(){
					@Override
					public void run() {
						Command command;
						try {
							command = (Command) cmd.newInstance();
							command.execute(event);
						} catch (InstantiationException e) {
							e.printStackTrace();
						} catch (IllegalAccessException e) {
							e.printStackTrace();
						}
					}
				});
			} 
			else {
				Command command;
				try {
					command = (Command) cmd.newInstance();
					command.execute(event);
				} catch (InstantiationException e) {
					// TODO Auto-generated catch block
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
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

	
}

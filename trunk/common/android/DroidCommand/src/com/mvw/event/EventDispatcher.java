package com.mvw.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventDispatcher {

	private Map<String, List<EventListener>> listeners = new HashMap<String, List<EventListener>>();

	public EventDispatcher() {
		super();
	}

	public void dispatchEvent(final CommandEvent event) {
		synchronized (listeners) {
			// first call all listeners
			List<EventListener> elist = listeners.get(event.getType());
			if (elist != null && elist.size() > 0) {
				for (EventListener listener : elist) {
					listener.onEvent(event);
				}
			}
		}
	}

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
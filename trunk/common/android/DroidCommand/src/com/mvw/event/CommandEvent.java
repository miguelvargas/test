package com.mvw.event;


public abstract class CommandEvent {
	public abstract String getType();

	// default to async
	private SYNC_TYPE syncType = SYNC_TYPE.Async;

	public boolean isAsync() {
		if (syncType == SYNC_TYPE.Async) {
			return true;
		} else {
			return false;
		}
	}

	public CommandEvent() {
	}

	public CommandEvent(SYNC_TYPE syncType) {
		super();
		this.syncType = syncType;
	}

	public enum SYNC_TYPE {
		Async, Sync
	}

}

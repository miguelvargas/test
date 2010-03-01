package com.mvw.service;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.Intent;
import android.util.Log;



public abstract class BaseTimerService extends Service implements IBaseTimerService {
	
	private String serviceName;
	private long updateInterval = 20000;
	private Timer timer = new Timer();
	
	
	protected BaseTimerService(String serviceName) {
		super();
		this.serviceName = serviceName;
	}

	public long getUpdateInterval() {
		return updateInterval;
	}

	public void setUpdateInterval(long updateInterval) {
		this.updateInterval = updateInterval;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		_shutdownService();
	}

	
	


	@Override
	public void onCreate() {
		super.onCreate();
		_startupService();
	}
	
	@Override
	public void onStart(Intent intent, int startId) {
		Log.i(getClass().getSimpleName(), "onStart");
		super.onStart(intent, startId);
	}


	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i(getClass().getSimpleName(), "onStartCommand");
		return super.onStartCommand(intent, flags, startId);
	}


	private void _startupService() {
		timer.scheduleAtFixedRate(
				new TimerTask() {
					@Override
					public void run() {
						runUpdate();
						
					}}
				, 0, updateInterval);
		Log.i(getClass().getSimpleName(), serviceName+ " Service Timer started!!!");
	}
	
	private void _shutdownService() {
		if (timer != null) timer.cancel();
		Log.i(getClass().getSimpleName(), "Timer stopped!!!");
	}
	
	protected abstract void runUpdate();


}

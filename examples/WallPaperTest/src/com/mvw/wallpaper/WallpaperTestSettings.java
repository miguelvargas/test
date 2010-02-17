package com.mvw.wallpaper;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.util.Log;

public class WallpaperTestSettings extends PreferenceActivity 
implements SharedPreferences.OnSharedPreferenceChangeListener {
	
	private static final String   TAG   = WallpaperTestSettings.class.getSimpleName ();
	
	
@Override
protected void onCreate(Bundle icicle) {
    super.onCreate(icicle);
    getPreferenceManager().setSharedPreferencesName(
            WallpaperTestService.PREFERENCES);
    addPreferencesFromResource(R.xml.settings);
    try {
    getPreferenceManager().getSharedPreferences()
            .registerOnSharedPreferenceChangeListener(this);
    } catch (Exception e) {
    	Log.e(TAG, e.toString());
    }
}

@Override
protected void onPause() {
	super.onPause();
	try {
		getPreferenceManager().getSharedPreferences()
	    .unregisterOnSharedPreferenceChangeListener(this);
		}
		catch (Exception e) {
		 Log.e(TAG, e.toString());
		}
}

@Override
protected void onResume() {
	super.onResume();
	try {
	getPreferenceManager().getSharedPreferences()
    .registerOnSharedPreferenceChangeListener(this);
	}
	catch (Exception e) {
	 Log.e(TAG, e.toString());
 }
}


public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
	
	this.finish();
}

}



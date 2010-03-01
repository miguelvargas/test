package com.mvw.hudson.activity;

import android.os.Bundle;
import android.preference.PreferenceActivity;

import com.mvw.hudson.R;

public class HudsonPreferenceActivity extends PreferenceActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
	}
	
	

}

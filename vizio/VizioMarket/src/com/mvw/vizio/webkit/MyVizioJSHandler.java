package com.mvw.vizio.webkit;

import android.content.Context;
import android.telephony.TelephonyManager;

import com.mvw.vizio.activity.MarketHome;

public class MyVizioJSHandler {
	public String getPhoneNum() {
		TelephonyManager telephony = (TelephonyManager)MarketHome.getThisContext().getSystemService(Context.TELEPHONY_SERVICE); 
		return telephony.getLine1Number();
	}
	public String getId() {
		TelephonyManager telephony = (TelephonyManager)MarketHome.getThisContext().getSystemService(Context.TELEPHONY_SERVICE); 
		return telephony.getDeviceId();
	}

}

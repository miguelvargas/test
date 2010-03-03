package com.mvw.vizio.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.mvw.vizio.R;
import com.mvw.vizio.webkit.APKWebViewClient;
import com.mvw.vizio.webkit.MyVizioJSHandler;

public class MarketHome extends Activity {
    private WebView browser;

    private static Context thisContext;
    
	public static Context getThisContext() {
		return thisContext;
	}
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	thisContext = getApplicationContext();
        setContentView(R.layout.main);
        browser = (WebView) findViewById(R.id.marketBrowser);
        WebSettings settings = browser.getSettings();
        settings.setJavaScriptEnabled(true);
        // clear cache
        browser.clearCache(true);
        // add this webchrome for more chrome functionality
//        browser.setWebChromeClient(new WebChromeClient());
        // Not needed yet, but we can add custom javascript functions to this browser
        browser.setWebViewClient(new APKWebViewClient());
        
        browser.addJavascriptInterface(new MyVizioJSHandler(), "vizio");

        // load a page to get things started
        browser.loadUrl("http://www.androidworks.com:8080/test/");

    }
}
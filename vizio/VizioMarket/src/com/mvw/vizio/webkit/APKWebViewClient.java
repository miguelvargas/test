package com.mvw.vizio.webkit;

import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class APKWebViewClient extends WebViewClient {

	@Override
	public boolean shouldOverrideUrlLoading(WebView view, String url) {
		boolean should = super.shouldOverrideUrlLoading(view, url);
		Log.d(this.getClass().getSimpleName(), "should overried = "+should);
		view.loadUrl(url);
		return true;
	}

}

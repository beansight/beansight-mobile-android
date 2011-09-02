package com.beansight.android;


import com.beansight.android.api.BeansightApi;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebViewActivity extends Activity {

	public static final String FRAGMENT = "?access_token=";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        
        String url = BeansightApi.DOMAIN + "/api/authenticate?";
        
        WebView webView = (WebView)findViewById(R.id.webkitWebView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                int start = url.indexOf(FRAGMENT);
                if (start > -1) {
                    // You can use the accessToken for api calls now.
                    String accessToken = url.substring(start + FRAGMENT.length(), url.length());
        			
                    Log.v("WebViewActivity", "OAuth complete, token: [" + accessToken + "].");
                	
                    SharedPreferences prefs = getSharedPreferences(BeansightApplication.BEANSIGHT_PREFS, 0);
                    Editor editor = prefs.edit();
                    editor.putString("access_token", accessToken);
                    editor.commit();
                    
                    Intent homeActivity = new Intent(WebViewActivity.this, HomeActivity.class);
                    startActivity(homeActivity);
                    finish();
                }
            }
            
            public void onPageFinished(WebView view, String url) {
                int start = url.indexOf(FRAGMENT);
                if (start > -1) {
                	// remove cookies to logout
                	CookieManager.getInstance().removeAllCookie();
                    Log.v("WebViewActivity", "removed cookie");
                }
            }
        });
        webView.loadUrl(url);
	}
	
}

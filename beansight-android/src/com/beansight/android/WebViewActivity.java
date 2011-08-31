package com.beansight.android;


import com.beansight.android.api.BeansightApi;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class WebViewActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        
        String url = BeansightApi.domain + "/api/authenticate?";
        
        WebView webView = (WebView)findViewById(R.id.webkitWebView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                String fragment = "?access_token=";
                int start = url.indexOf(fragment);
                if (start > -1) {
                    // You can use the accessToken for api calls now.
                    String accessToken = url.substring(start + fragment.length(), url.length());
        			
                    Log.v("WebViewActivity", "OAuth complete, token: [" + accessToken + "].");
                	
                    SharedPreferences prefs = getSharedPreferences(BeansightApplication.BEANSIGHT_PREFS, 0);
                    Editor editor = prefs.edit();
                    editor.putString("access_token", accessToken);
                    editor.commit();
                    
                    Intent homeActivity = new Intent(WebViewActivity.this, HomeActivity.class);
                    startActivity(homeActivity);
                }
            }
        });
        webView.loadUrl(url);
	}
	
}

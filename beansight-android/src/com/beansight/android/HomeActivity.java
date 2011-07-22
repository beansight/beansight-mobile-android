package com.beansight.android;

import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import com.beansight.android.api.BeansightApi;
import com.beansight.android.models.InsightListItem;
import com.beansight.android.models.InsightListItemResponse;

public class HomeActivity extends Activity {
	
	private String accessToken;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.insight_vote);
        
		SharedPreferences prefs = getSharedPreferences(BeansightApplication.BEANSIGHT_PREFS, 0);
		accessToken = prefs.getString("access_token", null);
        
		
		TextView insightText = (TextView)findViewById(R.id.insightText);
		
		InsightListItemResponse insightListItemResponse = null;
		try {
		insightListItemResponse = BeansightApi.list(accessToken, null, null, null, null, null, null, null, null);
			// if not authenticated, load the WebView Activity
			if (insightListItemResponse != null && !insightListItemResponse.getMeta().isAuthenticated()) {
				startActivity( new Intent(this, WebViewActivity.class) );
			}
		} catch (IOException e) {
			e.printStackTrace();
			// FIXME should handle this better ...
		}
		
		if (insightListItemResponse != null && insightListItemResponse.getMeta().isAuthenticated() && insightListItemResponse.getResponse() != null) {
			for (InsightListItem item : insightListItemResponse.getResponse()) {
				insightText.setText(item.getContent());
			}
		}

    }
   
}
package com.beansight.android;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.beansight.android.api.BeansightApi;
import com.beansight.android.models.InsightListItem;
import com.beansight.android.models.InsightListItemResponse;

public class HomeActivity extends Activity implements View.OnClickListener{
	
	/** store the list of downloaded insights */
	private List<InsightListItem> insightList;
	/** the number of insight to ask at every list calls */
	private static final int INSIGHT_NUMBER = 30;
	/** iterator pointing to the currently displayed insight */
	private int currentInsightIndex = 0;
	
	private TextView insightText;
	
	private String accessToken;

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.insight_vote);
        
		SharedPreferences prefs = getSharedPreferences(BeansightApplication.BEANSIGHT_PREFS, 0);
		accessToken = prefs.getString("access_token", null);
        
		insightList = new ArrayList<InsightListItem>();
		// get an iterator
		fetchNextInsights();
		
		View b;
		b = (View) findViewById(R.id.buttonAgree);
        b.setOnClickListener(this);
		b = (View) findViewById(R.id.buttonDisagree);
        b.setOnClickListener(this);
        
        insightText = (TextView)findViewById(R.id.insightText);

        next();
    }
    
    /** call Beansight API to get the next insight, and append them to the list */
    private void fetchNextInsights() {
		InsightListItemResponse insightListItemResponse = null;
		
		try {
		insightListItemResponse = BeansightApi.list(accessToken, insightList.size(), INSIGHT_NUMBER, null, null, null, null, null, null);
			// if not authenticated, load the WebView Activity
			if (insightListItemResponse != null && !insightListItemResponse.getMeta().isAuthenticated()) {
				startActivity( new Intent(this, WebViewActivity.class) );
			}
		} catch (IOException e) {
			e.printStackTrace();
			// FIXME should handle this better ...
		}
		
        // populate the insight list
		if (insightListItemResponse != null) {
			insightList.addAll(insightListItemResponse.getResponse());
		}
    }

    private void agree() {
    	Toast.makeText(this, "agree", Toast.LENGTH_SHORT).show();
    	next();
    }
    
    private void disagree() {
    	Toast.makeText(this, "disagree", Toast.LENGTH_SHORT).show();
    	next();
    }
    
    private void next() {
    	if(currentInsightIndex + 1 < insightList.size()) {
    		currentInsightIndex++;
    		insightText.setText(insightList.get(currentInsightIndex).getContent());
    	} else {
    		fetchNextInsights();
    		next();
    	}
    }
    
    private void previous() {
    	if(currentInsightIndex - 1 > 0) {
    		currentInsightIndex--;
    		insightText.setText(insightList.get(currentInsightIndex).getContent());
    	}
    }
    
	public void onClick(View v) {
		switch (v.getId()) {
        case R.id.buttonAgree:
        	agree();
        	break;
        case R.id.buttonDisagree:
        	disagree();
        	break;
		}
	}
	
   
}
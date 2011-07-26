package com.beansight.android;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import com.beansight.android.api.BeansightApi;
import com.beansight.android.api.responses.InsightListResponse;
import com.beansight.android.models.InsightListItem;

public class HomeActivity extends Activity implements View.OnClickListener{

	private Context cxt;
	private InsightListPagerAdapter pagerAdapter;
	private ViewPager pager;
	
	/** store the list of downloaded insights */
	private List<InsightListItem> insightList;
	/** the number of insight to ask at every list calls */
	private static final int INSIGHT_NUMBER = 30;
	/** iterator pointing to the currently displayed insight */
	private int currentInsightIndex = 0;
	
	private String accessToken;

	public enum VoteState {
		AGREE, DISAGREE
	}
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.insight_vote);
        
        cxt = this;
        
		SharedPreferences prefs = getSharedPreferences(BeansightApplication.BEANSIGHT_PREFS, 0);
		accessToken = prefs.getString("access_token", null);
        
		View b;
		b = (View) findViewById(R.id.buttonAgree);
		b.setOnClickListener(this);
		b = (View) findViewById(R.id.buttonDisagree);
		b.setOnClickListener(this);

		insightList = new ArrayList<InsightListItem>();
		// get an iterator
		fetchNextInsights();
		
        
        pagerAdapter = new InsightListPagerAdapter();
        pager = (ViewPager) findViewById(R.id.insightPager);
        pager.setAdapter(pagerAdapter);
        pager.setOnPageChangeListener(new MyPageChangeListener());
    }
    
    /** call Beansight API to get the next insight, and append them to the list */
    private void fetchNextInsights() {
		InsightListResponse insightListItemResponse = null;
		
		try {
			insightListItemResponse = BeansightApi.list(accessToken, insightList.size(), INSIGHT_NUMBER, "incoming", null, "non-voted", null, null, false);
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
    	vote(VoteState.AGREE);
    }
    
    private void disagree() {
    	vote(VoteState.DISAGREE);
    }
    
    private void vote(VoteState state) {
//    	InsightVoteResponse insightVoteResponse = null;
//		try {
//			if(state == VoteState.AGREE) {
//				insightVoteResponse = BeansightApi.agree(accessToken, insightList.get(currentInsightIndex).getId());
//			} else if(state == VoteState.DISAGREE) {
//				insightVoteResponse = BeansightApi.disagree(accessToken, insightList.get(currentInsightIndex).getId());
//			}
//		} catch (NotAuthenticatedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//		Toast.makeText(this, insightVoteResponse.getResponse().getVoteState(), Toast.LENGTH_SHORT).show();    	
		next();
    }
    
    private void next() {
    	if(currentInsightIndex + 1 < insightList.size()) {
    		pager.setCurrentItem(currentInsightIndex + 1);
    	}
    }
    
    private void previous() {
    	if(currentInsightIndex - 1 > 0) {
    		pager.setCurrentItem(currentInsightIndex - 1);
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

	// see http://geekyouup.blogspot.com/2011/07/viewpager-example-from-paug.html
	private class InsightListPagerAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return insightList.size();
		}

		@Override
		public Object instantiateItem(View collection, int position) {
			// position is the position of the element that will come after the newly displayed element
			if( position + 1 < insightList.size() ) {
				fetchNextInsights();
			}
			
			TextView tv = new TextView(cxt);
			tv.setText(insightList.get(position).getInsightText());
			tv.setTextSize(30);
			
			((ViewPager) collection).addView(tv,0);
			
			return tv;
		}

		@Override
		public void destroyItem(View container, int arg1, Object view) {
			((ViewPager) container).removeView((TextView) view);
		}


		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view==((TextView)object);
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(View arg0) {}

		@Override
		public void finishUpdate(View arg0) {}

	}
	
	private class MyPageChangeListener extends ViewPager.SimpleOnPageChangeListener {
        @Override
        public void onPageSelected(int position) {
        	currentInsightIndex = position;
        }
	}

}
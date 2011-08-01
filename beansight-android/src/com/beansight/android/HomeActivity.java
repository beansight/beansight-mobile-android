package com.beansight.android;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.beansight.android.api.BeansightApi;
import com.beansight.android.api.NotAuthenticatedException;
import com.beansight.android.api.responses.InsightListResponse;
import com.beansight.android.api.responses.InsightVoteResponse;
import com.beansight.android.models.InsightListItem;

public class HomeActivity extends Activity {

	private Context cxt;
	private ViewPager pager;
	
	/** store the list of downloaded insights */
	private List<InsightListItem> insightList;
	/** the number of insight to ask at every list calls */
	private static final int INSIGHT_NUMBER = 10;
	/** starts downloading new insight when we are INSIGHT_NUMBER_START_DOWNLOAD insights far from the end of the list */
	private static final int INSIGHT_NUMBER_START_DOWNLOAD = 5;
	/** iterator pointing to the currently displayed insight */
	private int currentInsightIndex = 0;
	
	private RadioButton radioAgree;
	private RadioButton radioDisagree;
	private RadioGroup radioGroup;
	
	/** is the system waiting for new insights to come ? */
	private boolean fetchingNewInsights = false;
	
	private String accessToken;

	public enum VotePosition {
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

		insightList = new ArrayList<InsightListItem>();
		fetchNextInsights();
		
		radioGroup = (RadioGroup) findViewById(R.id.agreeDisagreeButtons);
		
		// attach click listeners to radio buttons
		radioAgree = (RadioButton) findViewById(R.id.buttonAgree);
		radioAgree.setOnClickListener( new OnClickListener() {
		    public void onClick(View v) {
		        agree();
		    }
		});
		radioDisagree = (RadioButton) findViewById(R.id.buttonDisagree);
		radioDisagree.setOnClickListener( new OnClickListener() {
			public void onClick(View v) {
				disagree();
			}
		});
		
		// attach the listeners to the previous and next buttons
		((ImageButton) findViewById(R.id.buttonPrevious)).setOnClickListener( new OnClickListener() {
		    public void onClick(View v) {
		        previous();
		    }
		});
		((ImageButton) findViewById(R.id.buttonNext)).setOnClickListener( new OnClickListener() {
		    public void onClick(View v) {
		        next();
		    }
		});
    }
    
    private void openConnectScreen() {
    	startActivity( new Intent(this, WebViewActivity.class) );
    }
    
    private void fetchNextInsights() {
    	fetchingNewInsights = true;
    	new ListTask().execute(insightList.size());
    }

    private void agree() {
    	vote(VotePosition.AGREE);
    }
    
    private void disagree() {
    	vote(VotePosition.DISAGREE);
    }
    
    private void vote(VotePosition state) {
    	String vote = "agree";
    	if(state == VotePosition.DISAGREE) {
    		vote = "disagree";
    	}
		insightList.get(currentInsightIndex).setLastCurrentUserVote(vote);

		new VoteTask().execute(state);

		next();
    }
    
    private void next() {
    	changePage(currentInsightIndex + 1);
    }
    
    private void previous() {
    	changePage(currentInsightIndex - 1);
    }
    
    private void changePage(int pageNumber) {
    	if(pageNumber > 0 && pageNumber < insightList.size()) {
    		pager.setCurrentItem(pageNumber);
    	}
    }
    
    private void setButtonsVoteState(int pageNumber) {
    	radioGroup.clearCheck();
    	String state = insightList.get(pageNumber).getLastCurrentUserVote();
    	if(state.equals("agree")) {
    		radioAgree.setChecked(true);
    	} else if(state.equals("disagree")) {
    		radioDisagree.setChecked(true);
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
			if( position + INSIGHT_NUMBER_START_DOWNLOAD >= insightList.size() && !fetchingNewInsights ) {
				fetchNextInsights();
			}
			
			// inflate a view from the XML layouts
			LayoutInflater inflater = (LayoutInflater)cxt.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View insightContentView = inflater.inflate(R.layout.insight_content, null);
			// set the insight text
			TextView tv = (TextView) insightContentView.findViewById(R.id.insightText);
			tv.setText(insightList.get(position).getInsightText());
			
			((ViewPager) collection).addView(insightContentView,0);
			
			return insightContentView;
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
        	setButtonsVoteState(position);
        }
	}

	/** Create a thread to call the Beansight List API to get the next insight, and append them to the list */
	private class ListTask extends AsyncTask<Integer, Void, InsightListResponse> {
		/**
		 * @param from : 
		 */
	    protected InsightListResponse doInBackground(Integer... from) {
			InsightListResponse insightListItemResponse = null;
			try {
				insightListItemResponse = BeansightApi.list(accessToken, from[0], INSIGHT_NUMBER, "incoming", null, "non-voted", null, null, false);
			} catch (IOException e) {
				e.printStackTrace();
			}

			return insightListItemResponse;
	    }
	    
	    protected void onPostExecute(InsightListResponse response) {
	    	// if not authenticated, load the WebView Activity
	    	if (response != null && !response.getMeta().isAuthenticated()) {
	    		openConnectScreen();
	    		return;
	    	}
	    	
	    	//TODO : this is temporary, we should display a waiting screen (fragment?) 
	    	boolean createPager = false;
	    	if( insightList.isEmpty() ) {
	    		createPager = true;
	    	}
	    	
	        // populate the insight list
			if (response != null) {
				insightList.addAll(response.getResponse());
			}
			
			if(createPager) {
	    		InsightListPagerAdapter pagerAdapter = new InsightListPagerAdapter();
	            pager = (ViewPager) findViewById(R.id.insightPager);
	            pager.setAdapter(pagerAdapter);
	            pager.setOnPageChangeListener(new MyPageChangeListener());
			}
			
			fetchingNewInsights = false;
	    }
	}
	
	/** Create a thread and vote the given position on the given insight */
	private class VoteTask extends AsyncTask<VotePosition, Void, InsightVoteResponse> {
		@Override
		protected InsightVoteResponse doInBackground(VotePosition... state) {
	    	InsightVoteResponse insightVoteResponse = null;
//			try {
//				if(state[0] == VotePosition.AGREE) {
//					insightVoteResponse = BeansightApi.agree(accessToken, insightList.get(currentInsightIndex).getId());
//				} else if(state[0] == VotePosition.DISAGREE) {
//					insightVoteResponse = BeansightApi.disagree(accessToken, insightList.get(currentInsightIndex).getId());
//				}
//			} catch (NotAuthenticatedException e) {
//				e.printStackTrace();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
			return insightVoteResponse;
		}
	}
	
}
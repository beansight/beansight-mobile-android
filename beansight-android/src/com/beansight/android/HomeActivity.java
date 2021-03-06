package com.beansight.android;

import greendroid.app.GDActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.beansight.android.api.BeansightApi;
import com.beansight.android.api.NotAuthenticatedException;
import com.beansight.android.api.ServerErrorException;
import com.beansight.android.api.responses.InsightListResponse;
import com.beansight.android.api.responses.InsightVoteResponse;
import com.beansight.android.api.responses.Meta;
import com.beansight.android.api.responses.UserProfileResponse;
import com.beansight.android.models.InsightListItem;

public class HomeActivity extends GDActivity {

	// Views
	private Context cxt;
	private ViewPager pager;
	private RadioButton radioAgree;
	private RadioButton radioDisagree;
	private RadioGroup radioGroup;
	private ProgressDialog loadingInsightsDialog;
	
	// Finals
	/** the number of insight to ask at every list calls */
	private static final int INSIGHT_NUMBER = 10;
	/** starts downloading new insight when we are INSIGHT_NUMBER_START_DOWNLOAD insights far from the end of the list */
	private static final int INSIGHT_NUMBER_START_DOWNLOAD = 5;
	/** number of milliseconds to wait before the automatic switch to the next prediction */
	private static final int NEXT_INSIGHT_TIME = 1000;

	private static final int DIALOG_LOADING_INSIGHTS_ID = 0;
	
	// Data
	/** store the list of downloaded insights */
	private List<InsightListItem> insightList;
	/** iterator pointing to the currently displayed insight */
	private int currentInsightIndex = 0;
	/** access token */
	private String accessToken;
	/** current user */
	private String userName;

	// State
	/** is the system waiting for new insights to come ? */
	private boolean fetchingNewInsights = false;
	

	public enum VotePosition {
		AGREE, DISAGREE
	}
	
	protected Dialog onCreateDialog(int id) {
	    Dialog dialog;
	    switch(id) {
	    case DIALOG_LOADING_INSIGHTS_ID:
	    	// show a loading dialog
	    	String alertTitle = "";
	    	if(userName != null) {
	    		alertTitle = userName;
	    	}
	    	dialog = ProgressDialog.show(this, alertTitle, getResources().getText(R.string.loading_insights), true);
	        break;
	    default:
	        dialog = null;
	    }
	    return dialog;
	}
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setActionBarContentView(R.layout.insight_vote);
        
		SharedPreferences prefs = getSharedPreferences(BeansightApplication.BEANSIGHT_PREFS, 0);
		accessToken = prefs.getString("access_token", null);
		userName 	= prefs.getString("userName", null);
		
    	insightList = new ArrayList<InsightListItem>();
		
	    cxt = this;
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
		
        pager = (ViewPager) findViewById(R.id.insightPager);
        pager.setAdapter(new InsightListPagerAdapter());
        pager.setOnPageChangeListener(new MyPageChangeListener());

    	// if no access token, then ask a login
    	if(accessToken == null) {
    		openConnectScreen();
    		return;
    	}
        
		// get the last state (for example if the activity has been restarted because of an orientation change)
		final ActivityData data = (ActivityData) getLastNonConfigurationInstance();
	    if (data != null) { 
	    	insightList = data.insightList;
	    	currentInsightIndex = data.currentInsightIndex;
	    } 
	    if(insightList.isEmpty()) {
	    	// show a loading dialog
	    	// Prevent from changing orientation during the presence of the dialog box
	    	setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
	    	showDialog(DIALOG_LOADING_INSIGHTS_ID);
	    	fetchNextInsights();
	    }
    }
    
    /** Called when the activity comes to foreground */
    @Override
	public void onRestart() {
    	super.onRestart();
    	// if no access token, then ask a login
    	if(accessToken == null) {
    		openConnectScreen();
    	}
	}
    
    @Override
    public Object onRetainNonConfigurationInstance() {
    	// save the currently loaded insights before activity is destroy
        final ActivityData data = generateActivityData();
        return data;
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.insight_vote_menu, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
        case R.id.menu_logout:
            logout();
            return true;
        default:
        	return super.onOptionsItemSelected(item);
        }
    }
    
    private void notLoggedError() {
    	Toast.makeText(this, R.string.error_notauthenticated, Toast.LENGTH_LONG).show();
    	logout();
    }
    
    private void serverError() {
    	Toast.makeText(this, R.string.server_error, Toast.LENGTH_LONG).show();
    }
    
    private void logout() {
    	SharedPreferences prefs = getSharedPreferences(BeansightApplication.BEANSIGHT_PREFS, 0);
        Editor editor = prefs.edit();
        editor.putString("access_token", 			null);
        editor.putString("userName", 				null);
        editor.putString("avatarSmall", 			null);
        editor.putString("avatarMedium", 			null);
        editor.putString("avatarLarge", 			null);
        editor.commit();
        
        // reload:
        Intent intent = getIntent();
        finish();
        startActivity(intent);
	}

	private void openConnectScreen() {
    	startActivity( new Intent(this, WebViewActivity.class) );
        finish();
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

		VoteTaskParams params = new VoteTaskParams(state, insightList.get(currentInsightIndex).getId());
		new VoteTask().execute(params);

		new WaitAndGoNextTask().execute(currentInsightIndex);
		
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
				insightListItemResponse = BeansightApi.list(accessToken, from[0], INSIGHT_NUMBER, "updated", null, "non-voted", null, null, "user");
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ServerErrorException e) {
				e.printStackTrace();
			}

			return insightListItemResponse;
	    }
	    
	    protected void onPostExecute(InsightListResponse response) {
	    	fetchingNewInsights = false;
	    	
    		Log.d("Beansight", "Get Insight List");

	    	// there was a problem with the API, display a message
	    	if(response == null ) {
	    		Log.e("Beansight", "ListTask server error");
	    		serverError();
	    		return;
	    	}
	    	// if not authenticated, load the WebView Activity
	    	if (response != null && !response.getMeta().isAuthenticated()) {
	    		openConnectScreen();
	    		return;
	    	}
	    	
	    	boolean refreshPager = false;
	    	if(insightList.isEmpty()) {
	    		refreshPager = true;
	    	}
	    	
	    	removeDialog(DIALOG_LOADING_INSIGHTS_ID);
	    	// set the orientation to normal
	    	setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
	    	
	        // populate the insight list
			if (response != null) {
				insightList.addAll(response.getResponse());
			}
			
			if(refreshPager) {
				pager.getAdapter().notifyDataSetChanged();
			}
			
			if(userName == null) {
				userName = response.getMeta().getUserName();
				SharedPreferences prefs = getSharedPreferences(BeansightApplication.BEANSIGHT_PREFS, 0);
	            Editor editor = prefs.edit();
	            editor.putString("userName", response.getMeta().getUserName());
	            editor.commit();
			}
			
	    }
	}
	
	private class VoteTaskParams {
		public VoteTaskParams(VotePosition position, String id) {
			this.position = position;
			this.id = id;
		}

		private VotePosition position;
		private String id;
		
		public VotePosition getPosition() {
			return position;
		}
		public String getId() {
			return id;
		}
	}
	
	/** Create a thread and vote the given position on the given insight */
	private class VoteTask extends AsyncTask<VoteTaskParams, Void, InsightVoteResponse> {
		@Override
		protected InsightVoteResponse doInBackground(VoteTaskParams... params) {
	    	InsightVoteResponse insightVoteResponse = null;
			try {
				if(params[0].getPosition() == VotePosition.AGREE) {
					insightVoteResponse = BeansightApi.agree(accessToken, params[0].getId());
				} else if(params[0].getPosition() == VotePosition.DISAGREE) {
					insightVoteResponse = BeansightApi.disagree(accessToken, params[0].getId());
				}
			} catch (NotAuthenticatedException e) {
				e.printStackTrace();
				// if not authenticated
				insightVoteResponse = new InsightVoteResponse();
				Meta meta = new Meta();
				meta.setAuthenticated(false);
				insightVoteResponse.setMeta(meta);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ServerErrorException e) {
				e.printStackTrace();
			}
			return insightVoteResponse;
		}
		
	    protected void onPostExecute(InsightVoteResponse response) {
	    	// there was a problem with the API, display a message
	    	if(response == null ) {
	    		Log.e("Beansight", "VoteTask server error");
	    		serverError();
	    		return;
	    	}
	    	
	    	if(!response.getMeta().isAuthenticated()) {
	    		notLoggedError();
	    	}
	    }
	}

	/** Create a thread asking for the current user's info. */
	private class CurrentUserTask extends AsyncTask<Void, Void, UserProfileResponse> {
		@Override
		protected UserProfileResponse doInBackground(Void... nothing ) {
	    	UserProfileResponse userProfileResponse = null;
			try {
				userProfileResponse = BeansightApi.me(accessToken);
			} catch (NotAuthenticatedException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ServerErrorException e) {
				e.printStackTrace();
			}
			return userProfileResponse;
		}
		
	    protected void onPostExecute(UserProfileResponse response) {
	    	// there was a problem with the API, display a message
	    	if(response == null ) {
	    		Log.e("Beansight", "CurrentUserTask server error");
	    		serverError();
	    		return;
	    	}
	    	
            SharedPreferences prefs = getSharedPreferences(BeansightApplication.BEANSIGHT_PREFS, 0);
            Editor editor = prefs.edit();
            editor.putString("avatarSmall", 			response.getResponse().getAvatarSmall());
            editor.putString("avatarMedium", 			response.getResponse().getAvatarMedium());
            editor.putString("avatarLarge", 			response.getResponse().getAvatarLarge());
            editor.commit();
	    }
	}
	
	private class WaitAndGoNextTask extends AsyncTask<Integer, Void, Integer> {
		@Override
		protected Integer doInBackground(Integer... votePageIndex ) {
			try {
				Thread.sleep(NEXT_INSIGHT_TIME);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return votePageIndex[0];
		}
		
	    protected void onPostExecute(Integer votePageIndex) {
	    	if(currentInsightIndex == votePageIndex.intValue()) {
	    		next();
	    	}
	    }
	}
	
	private class ActivityData {
		public List<InsightListItem> insightList;
		public int currentInsightIndex;
	}
	
	/** get the data to save before the activity is restarted */
	private ActivityData generateActivityData() {
		ActivityData data = new ActivityData();
		data.insightList = insightList;
		data.currentInsightIndex = currentInsightIndex;
		return data;
	}
	
}
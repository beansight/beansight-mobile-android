package com.beansight.android.api;


import java.io.IOException;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;

import android.util.Log;

import com.beansight.android.api.responses.InsightDetailResponse;
import com.beansight.android.api.responses.InsightListResponse;
import com.beansight.android.api.responses.InsightVoteResponse;
import com.beansight.android.api.responses.UserProfileResponse;
import com.beansight.android.http.Http;
import com.beansight.android.http.Http.HttpRequestBuilder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class BeansightApi {

	final private static HttpClient client = new DefaultHttpClient(); 
	private static String domain = "http://www.beansight.com"; // 92.243.10.157
	
	private static HttpRequestBuilder generateRequest(String apiAction, String accessToken) {
		String url = String.format("%s/api/" + apiAction, domain);
		return Http.get(url).use(client).data("access_token", accessToken);
	}
	
	public static InsightDetailResponse show(String accessToken, String id) throws NotAuthenticatedException, IOException {
		Log.v("BeansightApi.show", String.format("access_token=%s id=%s" , accessToken, id));
		
		InsightDetailResponse insightDetailResponse = null;
		String result = generateRequest("insights/show", accessToken)
			.data("id", id)
			.asString();
		Gson gson = new Gson();
		insightDetailResponse = gson.fromJson(result, InsightDetailResponse.class);
		
		return insightDetailResponse;
	}
	
	public static InsightListResponse list(String accessToken, Integer from,
			Integer number, String sort, Integer category,
			String vote, String topic, Boolean closed, String language) throws IOException {
		
		HttpRequestBuilder httpRequestbuilder = generateRequest("insights/list", accessToken);
		if(from!=null) {
			httpRequestbuilder.data("from", from.toString());
		}
		if(number!=null) {
			httpRequestbuilder.data("number", number.toString());
		}
		if(sort!=null) {
			httpRequestbuilder.data("sort", sort);
		}
		if(category!=null) {
			httpRequestbuilder.data("category", category.toString());
		}
		if(vote!=null) {
			httpRequestbuilder.data("vote", vote);
		}
		if(topic!=null) {
			httpRequestbuilder.data("topic", topic);
		}
		if(closed!=null) {
			httpRequestbuilder.data("closed", closed.toString());
		}
		if( language != null ) {
			httpRequestbuilder.data("language", language.toString());			
		}
		
		InsightListResponse insightListResponse = null;
		String result = "";
		try {
			result = httpRequestbuilder.asString();
		} catch (NotAuthenticatedException e) {
			// can't happen : list access isn't a protected resource
		}
		Gson gson = new GsonBuilder()
	     //.setDateFormat(DateFormat.LONG)
	     .create();
		insightListResponse = gson.fromJson(result, InsightListResponse.class);
	        
		return insightListResponse;
	}
	
	
	public static InsightVoteResponse agree(String accessToken, String id) throws NotAuthenticatedException, IOException {
		InsightVoteResponse insightVoteResponse = null;
		String result = generateRequest("insights/agree", accessToken)
			.data("id", id)
			.asString();
		insightVoteResponse = new Gson().fromJson(result, InsightVoteResponse.class);
		return insightVoteResponse;
	}
	
	public static InsightVoteResponse disagree(String accessToken, String id) throws NotAuthenticatedException, IOException {
		InsightVoteResponse insightVoteResponse = null;
		String result = generateRequest("insights/disagree", accessToken)
			.data("id", id)
			.asString();
		insightVoteResponse = new Gson().fromJson(result, InsightVoteResponse.class);
		return insightVoteResponse;
	}
	
	public static UserProfileResponse me(String accessToken) throws IOException, NotAuthenticatedException {
		UserProfileResponse response = null;
		String result = generateRequest("users/me", accessToken).asString();
		response = new Gson().fromJson(result, UserProfileResponse.class);
		return response;
	}
}

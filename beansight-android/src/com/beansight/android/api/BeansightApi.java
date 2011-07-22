package com.beansight.android.api;


import java.io.IOException;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;

import android.util.Log;

import com.beansight.android.http.Http;
import com.beansight.android.http.Http.HttpRequestBuilder;
import com.beansight.android.models.InsightDetailResponse;
import com.beansight.android.models.InsightListItemResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class BeansightApi {

	final private static HttpClient client = new DefaultHttpClient(); 
	private static String domain = "http://www.beansight.com";
	
	
	public static InsightDetailResponse show(String accessToken, String id) throws NotAuthenticatedException {
		Log.v("BeansightApi.show", String.format("access_token=%s id=%s" , accessToken, id));
		
		InsightDetailResponse insightDetailResponse = null;
		String url = String.format("%s/api/insights/show", domain);
		try {
			String result = Http.get(url).use(client)
				.data("access_token", accessToken)
				.data("id", id)
				.asString();
			Gson gson = new Gson();
			insightDetailResponse = gson.fromJson(result, InsightDetailResponse.class);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return insightDetailResponse;
	}
	
	public static InsightListItemResponse list(String accessToken, Integer from,
			Integer number, String sort, Integer category,
			String vote, String topic, Boolean closed, Boolean created) throws IOException {
		
		String url = String.format("%s/api/insights/list", domain);
		HttpRequestBuilder httpRequestbuilder = Http.get(url).use(client).data("access_token", accessToken);
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
		if(created!=null) {
			httpRequestbuilder.data("created", created.toString());
		}
		
		InsightListItemResponse insightListResponse = null;
		String result = "";
		try {
			result = httpRequestbuilder.asString();
		} catch (NotAuthenticatedException e) {
			// can't happen : list access isn't a protected resource
		}
		Gson gson = new GsonBuilder()
	     //.setDateFormat(DateFormat.LONG)
	     .create();
		insightListResponse = gson.fromJson(result, InsightListItemResponse.class);
	        
		return insightListResponse;
	}
	
}

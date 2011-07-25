package com.beansight.android.api.responses;

import java.util.List;

import com.beansight.android.models.InsightListItem;

public class InsightListResponse {

	private List<InsightListItem> response;
	private Meta meta;
	
	
	public List<InsightListItem> getResponse() {
		return response;
	}

	public void setResponse(List<InsightListItem> response) {
		this.response = response;
	}

	public Meta getMeta() {
		return meta;
	}

	public void setMeta(Meta meta) {
		this.meta = meta;
	}
}

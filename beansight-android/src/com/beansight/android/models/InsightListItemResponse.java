package com.beansight.android.models;

import java.util.List;

public class InsightListItemResponse {

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

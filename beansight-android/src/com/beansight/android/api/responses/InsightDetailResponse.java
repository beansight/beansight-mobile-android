package com.beansight.android.api.responses;

import com.beansight.android.models.InsightDetail;

public class InsightDetailResponse {

	private InsightDetail response;
	private Meta meta;
	
	
	public InsightDetail getResponse() {
		return response;
	}

	public void setResponse(InsightDetail response) {
		this.response = response;
	}

	public Meta getMeta() {
		return meta;
	}

	public void setMeta(Meta meta) {
		this.meta = meta;
	}
}

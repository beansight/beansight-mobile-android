package com.beansight.android.api.responses;

import com.beansight.android.models.InsightVote;

public class InsightVoteResponse {

	private InsightVote response;
	private Meta meta;
	
	
	public InsightVote getResponse() {
		return response;
	}

	public void setResponse(InsightVote response) {
		this.response = response;
	}

	public Meta getMeta() {
		return meta;
	}

	public void setMeta(Meta meta) {
		this.meta = meta;
	}
}

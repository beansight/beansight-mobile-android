package com.beansight.android.api.responses;

public class Response<T> {

	private T response;
	private Meta meta;
	
	public T getResponse() {
		return response;
	}

	public void setResponse(T response) {
		this.response = response;
	}

	public Meta getMeta() {
		return meta;
	}

	public void setMeta(Meta meta) {
		this.meta = meta;
	}
}

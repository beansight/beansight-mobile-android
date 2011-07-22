package com.beansight.android.api;

public class NotAuthenticatedException extends Exception {

	public NotAuthenticatedException() {
		super();
	}

	public NotAuthenticatedException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}

	public NotAuthenticatedException(String detailMessage) {
		super(detailMessage);
	}

	public NotAuthenticatedException(Throwable throwable) {
		super(throwable);
	}

}

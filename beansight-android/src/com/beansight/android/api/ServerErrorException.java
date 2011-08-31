package com.beansight.android.api;

public class ServerErrorException extends Exception {

	public ServerErrorException() {
		super();
	}

	public ServerErrorException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}

	public ServerErrorException(String detailMessage) {
		super(detailMessage);
	}

	public ServerErrorException(Throwable throwable) {
		super(throwable);
	}

}

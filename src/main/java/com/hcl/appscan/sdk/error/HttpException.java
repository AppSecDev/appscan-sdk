package com.hcl.appscan.sdk.error;

import java.io.IOException;

import com.hcl.appscan.sdk.Messages;

public class HttpException extends IOException {

	private static final long serialVersionUID = 1L;

	public HttpException(int responseCode, String message) {
		super(Messages.getMessage("error.http",  responseCode, message)); //$NON-NLS-1$
	}

	public HttpException(String message, Throwable throwable) {
		super(message, throwable);
	}
}

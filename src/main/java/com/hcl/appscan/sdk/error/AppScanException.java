/**
 * © Copyright IBM Corporation 2016.
 * © Copyright HCL Technologies Ltd. 2017. 
 * LICENSE: Apache License, Version 2.0 https://www.apache.org/licenses/LICENSE-2.0
 */

package com.hcl.appscan.sdk.error;

public class AppScanException extends Exception {

	private static final long serialVersionUID = 1L;

	public AppScanException(String message) {
		super(message);  
	}

	public AppScanException(String message, Throwable throwable) {
		super(message, throwable);
	}
}

/**
 * © Copyright IBM Corporation 2016.
 * © Copyright HCL Technologies Ltd. 2017. 
 * LICENSE: Apache License, Version 2.0 https://www.apache.org/licenses/LICENSE-2.0
 */

package com.hcl.appscan.sdk.error;

public class ScannerException extends Exception {

	private static final long serialVersionUID = 1L;

	/**
	 * Constructor.
	 * @param message The detail message.
	 * @param cause The cause of the exception.
	 */
	public ScannerException(String message, Throwable cause) {
		super(message, cause);
	}
	
	/**
	 * Constructor.
	 * @param message The detail message.
	 */
	public ScannerException(String message) {
		super(message);
	}

	/**
	 * Constructor.
	 * @param cause The cause of the exception.
	 */
	public ScannerException(Throwable cause) {
		super(cause);
	}
}

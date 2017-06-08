/**
 * © Copyright IBM Corporation 2016.
 * © Copyright HCL Technologies Ltd. 2017. 
 * LICENSE: Apache License, Version 2.0 https://www.apache.org/licenses/LICENSE-2.0
 */

package com.hcl.appscan.sdk.logging;

/**
 * Interface for reporting progress.
 */
public interface IProgress {

	/**
	 * Sets the status.
	 * @param status The status message.
	 */
	public void setStatus(Message status);
	
	/**
	 * Sets an error status.
	 * @param e The exception that caused the error.
	 */
	public void setStatus(Throwable e);
	
	/**
	 * Sets an error status.
	 * @param status The status message.
	 * @param e The exception that caused the error.
	 */
	public void setStatus(Message status, Throwable e);
}

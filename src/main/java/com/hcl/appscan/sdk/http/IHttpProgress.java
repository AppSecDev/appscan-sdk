/**
 * © Copyright IBM Corporation 2016.
 * © Copyright HCL Technologies Ltd. 2017. 
 * LICENSE: Apache License, Version 2.0 https://www.apache.org/licenses/LICENSE-2.0
 */

package com.hcl.appscan.sdk.http;

/**
 * Interface for reporting HTTP progress.
 */
public interface IHttpProgress {

	/**
	 * Set progress percentage.
	 * 
	 * @param percentage The percentage of the progress.
	 */
	void setProgress(int percentage);

	/**
	 * Reset the progress.
	 */
	void resetProgress();
	
	/**
	 * End progress output
	 */
	void endProgress();
}

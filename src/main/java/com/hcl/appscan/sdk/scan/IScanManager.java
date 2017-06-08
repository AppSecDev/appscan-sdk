/**
 * © Copyright IBM Corporation 2016.
 * © Copyright HCL Technologies Ltd. 2017. 
 * LICENSE: Apache License, Version 2.0 https://www.apache.org/licenses/LICENSE-2.0
 */

package com.hcl.appscan.sdk.scan;

import java.util.Map;

import com.hcl.appscan.sdk.error.AppScanException;
import com.hcl.appscan.sdk.logging.IProgress;
import com.hcl.appscan.sdk.scan.IScanServiceProvider;

public interface IScanManager {
	
	/**
	 * Performs any steps necessary to prepare for a scan.
	 * @param progress Tracks the progress of preparation.
	 * @param properties Additional properties to be used.
	 * @throws AppScanException
	 */
	void prepare(IProgress progress, Map<String, String> properties)  throws AppScanException;
	
	/**
	 * Runs analysis.
	 * @param progress Tracks the progress of analysis.
	 * @param properties Additional properties to be used in analysis.
	 * @param provider A provider of scanning services.
	 * @throws AppScanException
	 */
	void analyze(IProgress progress, Map<String, String> properties, IScanServiceProvider provider) throws AppScanException;
	
	/**
	 * Adds an item to be scanned.
	 * @param target
	 */
	void addScanTarget(ITarget target);	
}

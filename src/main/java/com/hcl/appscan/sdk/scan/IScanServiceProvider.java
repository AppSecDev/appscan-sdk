/**
 * © Copyright IBM Corporation 2016.
 * © Copyright HCL Technologies Ltd. 2017. 
 * LICENSE: Apache License, Version 2.0 https://www.apache.org/licenses/LICENSE-2.0
 */

package com.hcl.appscan.sdk.scan;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.apache.wink.json4j.JSONException;
import org.apache.wink.json4j.JSONObject;

import com.hcl.appscan.sdk.auth.IAuthenticationProvider;

/**
 * A provider of scanning services.
 */
public interface IScanServiceProvider {

	/**
	 * Creates and executes a scan.
	 * 
	 * @param type The type of scan to execute. For example DynamicAnalyzer, MobileAnalyzer, or StaticAnalyzer.
	 * @param params A Map of scan parameters.
	 * @return The id of the submitted scan, if successful.  Otherwise, null.
	 */
	public String createAndExecuteScan(String type, Map<String, String> params);
	
	/**
	 * Submits a file for scanning.
	 * 
	 * @param file The file to submit.
	 * @return The id of the submitted file.
	 * @throws IOException
	 */
	public String submitFile(File file) throws IOException;

	/**
	 * Gets the detailed description of a scan in JSON format.
	 * 
	 * @param scanId The id of the scan to retrieve the description.
	 * @return JSONObject The detailed description in JSON.
	 */
	public JSONObject getScanDetails(String scanId) throws IOException, JSONException;
	
	/**
	 * Gets the {@link IAuthenticationProvider} used to authenticate with a scanning service.
	 * 
	 * @return
	 */
	public IAuthenticationProvider getAuthenticationProvider();
}

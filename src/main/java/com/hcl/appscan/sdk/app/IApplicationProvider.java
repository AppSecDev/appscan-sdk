/**
 * © Copyright IBM Corporation 2016.
 * © Copyright HCL Technologies Ltd. 2017. 
 * LICENSE: Apache License, Version 2.0 https://www.apache.org/licenses/LICENSE-2.0
 */

package com.hcl.appscan.sdk.app;

import java.util.Map;

public interface IApplicationProvider {

	/**
	 * Gets the available applications.
	 * @return A Map of applications, keyed by the application id.
	 */
	public Map<String, String> getApplications();
	
	/**
	 * Gets the name of the application with the given id. 
	 * @param id The id of the application.
	 * @return The application name.
	 */
	public String getAppName(String id);
}

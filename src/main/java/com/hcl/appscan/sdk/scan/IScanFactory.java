/**
 * © Copyright IBM Corporation 2016.
 * © Copyright HCL Technologies Ltd. 2017. 
 * LICENSE: Apache License, Version 2.0 https://www.apache.org/licenses/LICENSE-2.0
 */

package com.hcl.appscan.sdk.scan;

import java.util.Map;

import com.hcl.appscan.sdk.auth.IAuthenticationProvider;
import com.hcl.appscan.sdk.logging.IProgress;

public interface IScanFactory {

	/**
	 * Creates a new {@link IScan}.
	 * 
	 * @param properties A {@link Map} of properties for the scan.
	 * @param progress An {@link IProgress} for reporting status.
	 * @param provider An {@link IAuthenticationProvider} for providing authentication services.
	 * @return The created {@link IScan}.
	 */
	public IScan create(Map<String, String> properties, IProgress progress, IAuthenticationProvider provider);
	
	/**
	 * Gets the type of scan this factory creates.
	 * 
	 * @return A string representing the scan type.
	 */
	public String getType();
}

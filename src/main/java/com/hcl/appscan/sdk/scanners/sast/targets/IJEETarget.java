/**
 * © Copyright IBM Corporation 2016.
 * © Copyright HCL Technologies Ltd. 2017. 
 * LICENSE: Apache License, Version 2.0 https://www.apache.org/licenses/LICENSE-2.0
 */

package com.hcl.appscan.sdk.scanners.sast.targets;

public interface IJEETarget extends IJavaTarget {

	/**
	 * Gets the jsp compiler that should be used for this target.
	 * @return The jsp compiler as a String.
	 */
	String getJSPCompiler();
	
}

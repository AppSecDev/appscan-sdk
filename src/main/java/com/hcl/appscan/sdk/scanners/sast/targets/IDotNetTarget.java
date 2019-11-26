/**
 * © Copyright IBM Corporation 2016.
 * © Copyright HCL Technologies Ltd. 2017. 
 * LICENSE: Apache License, Version 2.0 https://www.apache.org/licenses/LICENSE-2.0
 */

package com.hcl.appscan.sdk.scanners.sast.targets;

public interface IDotNetTarget extends ISASTTarget {

	/**
	 * Gets the dependencies of this target as a string.
	 * @return The dependencies as a String.
	 */
	String getReferences();

	/**
	 * Gets the targeted framework version of this target.
	 * @return The target framework version as a String.
	 */
	String getFrameworkVersion();
}

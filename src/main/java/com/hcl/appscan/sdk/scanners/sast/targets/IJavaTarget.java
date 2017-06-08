/**
 * © Copyright IBM Corporation 2016.
 * © Copyright HCL Technologies Ltd. 2017. 
 * LICENSE: Apache License, Version 2.0 https://www.apache.org/licenses/LICENSE-2.0
 */

package com.hcl.appscan.sdk.scanners.sast.targets;

public interface IJavaTarget extends ISASTTarget {

	/**
	 * Gets the classpath for this target as a String.
	 * @return
	 */
	String getClasspath();
	
	/**
	 * Gets the jre/jdk associated with this target.
	 * @return
	 */
	String getJava();
}

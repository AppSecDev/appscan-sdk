/**
 * © Copyright HCL Technologies Ltd. 2017. 
 * LICENSE: Apache License, Version 2.0 https://www.apache.org/licenses/LICENSE-2.0
 */

package com.hcl.appscan.sdk.scanners.sast.targets;

public interface ICppTarget extends ISASTTarget {

	/**
	 * Gets the compiler options used to build the target.
	 * @return
	 */
	String getCompilerOptions();
	
	/**
	 * Gets the macros defined for the target.
	 * @return
	 */
	String getMacros();
	
	/**
	 * Gets the include directories for the target.
	 * @return
	 */
	String getIncludeDirs();

	/**
	 * Gets the source files for the target.
	 * @return
	 */
	String getSourceFiles();

	/**
	 * Gets the version of the target.
	 * @return
	 */
	String getVersion();
}

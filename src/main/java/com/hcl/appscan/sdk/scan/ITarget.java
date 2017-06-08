/**
 * © Copyright IBM Corporation 2016.
 * © Copyright HCL Technologies Ltd. 2017. 
 * LICENSE: Apache License, Version 2.0 https://www.apache.org/licenses/LICENSE-2.0
 */

package com.hcl.appscan.sdk.scan;

/**
 * Represents an item to be scanned. Depending upon the type of scan, this may be a url or a file.
 */
public interface ITarget {

	String getTarget();
}

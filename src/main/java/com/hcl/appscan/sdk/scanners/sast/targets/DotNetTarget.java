/**
 * © Copyright HCL Technologies Ltd. 2017. 
 * LICENSE: Apache License, Version 2.0 https://www.apache.org/licenses/LICENSE-2.0
 */

package com.hcl.appscan.sdk.scanners.sast.targets;

import java.util.HashMap;
import java.util.Map;

import com.hcl.appscan.sdk.scanners.sast.xml.IModelXMLConstants;

public abstract class DotNetTarget extends DefaultTarget implements IDotNetTarget {

	@Override
	public Map<String, String> getProperties() {
		HashMap<String, String> buildInfos = new HashMap<String, String>();
		buildInfos.put(IModelXMLConstants.A_REFERENCES, getReferences());
		buildInfos.put(IModelXMLConstants.A_FRAMEWORK, getFrameworkVersion());
		return buildInfos;
	}
}

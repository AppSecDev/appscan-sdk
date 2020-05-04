/**
 * © Copyright HCL Technologies Ltd. 2019. 
 * LICENSE: Apache License, Version 2.0 https://www.apache.org/licenses/LICENSE-2.0
 */

package com.hcl.appscan.sdk.configuration.ase;

import com.hcl.appscan.sdk.auth.IASEAuthenticationProvider;

public class ConfigurationProviderFactory {
    
	public static IComponent getScanner(String type, IASEAuthenticationProvider provider) {
		IComponent comp = null;
		
		switch(type) {
		case "Folder":
			comp = new ASEFolderProvider(provider);
			break;
		case "TestPolicies":
			comp = new ASETestPoliciesProvider(provider);
			break;
		case "Agent":
			comp = new ASEAgentServerProvider(provider);
			break;
                case "Template":
			comp = new ASETemplateProvider(provider);
			break;
		default:
			break;
		}
		return comp;
	}    
}
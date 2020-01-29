/**
 * © Copyright HCL Technologies Ltd. 2019. 
 * LICENSE: Apache License, Version 2.0 https://www.apache.org/licenses/LICENSE-2.0
 */

package com.hcl.appscan.sdk.auth;

import java.util.List;

public interface IASEAuthenticationProvider extends IAuthenticationProvider {
    
	public void setCookies(List<String> cookies);    
}
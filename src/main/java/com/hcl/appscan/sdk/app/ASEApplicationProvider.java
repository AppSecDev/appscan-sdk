/**
 * © Copyright HCL Technologies Ltd. 2019. 
 * LICENSE: Apache License, Version 2.0 https://www.apache.org/licenses/LICENSE-2.0
 */

package com.hcl.appscan.sdk.app;

import com.hcl.appscan.sdk.CoreConstants;
import com.hcl.appscan.sdk.auth.IASEAuthenticationProvider;
import com.hcl.appscan.sdk.http.HttpsClient;
import com.hcl.appscan.sdk.http.HttpResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.apache.wink.json4j.JSONArray;
import org.apache.wink.json4j.JSONException;
import org.apache.wink.json4j.JSONObject;

public class ASEApplicationProvider implements IApplicationProvider, CoreConstants {
    
    private Map<String, String> m_applications;
    private IASEAuthenticationProvider m_authProvider;
	
    public ASEApplicationProvider(IASEAuthenticationProvider provider) {
    	m_authProvider = provider;
    }

    @Override
    public Map<String, String> getApplications() {
        if(m_applications == null)
        	loadApplications();
        return m_applications;
    }

    @Override
    public String getAppName(String id) {
        return getApplications().get(id);
    }

    private void loadApplications() {
        if(m_authProvider.isTokenExpired())
			return;
		
        m_applications = new HashMap<String, String>();        
        String url =  m_authProvider.getServer() + ASE_APPS+"?columns=name";
        Map<String, String> headers = m_authProvider.getAuthorizationHeader(true);
        headers.putAll(Collections.singletonMap("Range", "items=0-999999")); //$NON-NLS-1$ //$NON-NLS-2$
		
		HttpsClient client = new HttpsClient();
		
		try {
			HttpResponse response = client.get(url, headers, null);
			
			if (!response.isSuccess())
				return;
		
			JSONArray array = (JSONArray)response.getResponseBodyAsJSON();
			if(array == null)
				return;
			
			for(int i = 0; i < array.length(); i++) {
				JSONObject object = array.getJSONObject(i);
				String id = object.getString(ASE_ID_ATTRIBUTE);
				String name = object.getString(ASE_NAME_ATTRIBUTE);
				m_applications.put(id, name);
			}
		}
		catch(IOException | JSONException e) {
			m_applications = null;
		}
    }    
}
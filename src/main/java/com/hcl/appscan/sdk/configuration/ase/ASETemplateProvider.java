/**
 * © Copyright HCL Technologies Ltd. 2019. 
 * LICENSE: Apache License, Version 2.0 https://www.apache.org/licenses/LICENSE-2.0
 */

package com.hcl.appscan.sdk.configuration.ase;

import com.hcl.appscan.sdk.auth.IASEAuthenticationProvider;
import com.hcl.appscan.sdk.http.HttpResponse;
import com.hcl.appscan.sdk.http.HttpsClient;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.wink.json4j.JSONArray;
import org.apache.wink.json4j.JSONException;
import org.apache.wink.json4j.JSONObject;


public class ASETemplateProvider implements IComponent{
    private Map<String, String> m_templates;
    private IASEAuthenticationProvider m_authProvider;

    public ASETemplateProvider(IASEAuthenticationProvider provider) {
        this.m_authProvider=provider;
    }   

    @Override
    public Map<String, String> getComponents() {
        if(m_templates == null)
        	loadTemplates();
        return m_templates;
    }   

    @Override
    public String getComponentName(String id) {
        return getComponents().get(id);
    }
    
    private void loadTemplates() {
        if(m_authProvider.isTokenExpired())
			return;
		
        m_templates = new HashMap<String, String>();        
        String url =  m_authProvider.getServer() + "/api/templates";
        Map<String, String> headers = m_authProvider.getAuthorizationHeader(true);      
		
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
				String id = object.getString("id");
				String path = object.getString("name");
				m_templates.put(id, path);
			}
		}
		catch(IOException | JSONException e) {
			m_templates = null;
		}
    }    
}
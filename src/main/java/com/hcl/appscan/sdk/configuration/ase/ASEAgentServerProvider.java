/**
 * © Copyright HCL Technologies Ltd. 2019. 
 * LICENSE: Apache License, Version 2.0 https://www.apache.org/licenses/LICENSE-2.0
 */

package com.hcl.appscan.sdk.configuration.ase;

import com.hcl.appscan.sdk.CoreConstants;
import com.hcl.appscan.sdk.auth.IASEAuthenticationProvider;
import com.hcl.appscan.sdk.http.HttpResponse;
import com.hcl.appscan.sdk.http.HttpsClient;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.wink.json4j.JSONArray;
import org.apache.wink.json4j.JSONException;
import org.apache.wink.json4j.JSONObject;

public class ASEAgentServerProvider implements IComponent{
    private Map<String, String> m_agentServers;
    private IASEAuthenticationProvider m_authProvider;

    public ASEAgentServerProvider(IASEAuthenticationProvider provider) {
        this.m_authProvider=provider;
    }   

    @Override
    public Map<String, String> getComponents() {
        if(m_agentServers == null)
        	loadAgentServers();
        return m_agentServers;
    }
    
    @Override
    public String getComponentName(String id) {
        return getComponents().get(id);
    }
    
    private void loadAgentServers() {
        if(m_authProvider.isTokenExpired())
			return;
		
        m_agentServers = new HashMap<String, String>();        
        String url =  m_authProvider.getServer() + CoreConstants.ASE_AGENT_SERVER;
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
				String id = object.getString("serverId");
				String path = object.getString("name");
				m_agentServers.put(id, path);
			}
		}
		catch(IOException | JSONException e) {
			m_agentServers = null;
		}
    }
}
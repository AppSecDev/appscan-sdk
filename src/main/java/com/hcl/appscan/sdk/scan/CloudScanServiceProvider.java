/**
 * © Copyright IBM Corporation 2016.
 * © Copyright HCL Technologies Ltd. 2017. 
 * LICENSE: Apache License, Version 2.0 https://www.apache.org/licenses/LICENSE-2.0
 */

package com.hcl.appscan.sdk.scan;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import org.apache.wink.json4j.JSONException;
import org.apache.wink.json4j.JSONObject;

import com.hcl.appscan.sdk.CoreConstants;
import com.hcl.appscan.sdk.Messages;
import com.hcl.appscan.sdk.app.CloudApplicationProvider;
import com.hcl.appscan.sdk.app.IApplicationProvider;
import com.hcl.appscan.sdk.auth.IAuthenticationProvider;
import com.hcl.appscan.sdk.http.HttpClient;
import com.hcl.appscan.sdk.http.HttpPart;
import com.hcl.appscan.sdk.http.HttpResponse;
import com.hcl.appscan.sdk.logging.IProgress;
import com.hcl.appscan.sdk.logging.Message;

public class CloudScanServiceProvider implements IScanServiceProvider, Serializable, CoreConstants {

	private static final long serialVersionUID = 1L;

	private IProgress m_progress;
	private IAuthenticationProvider m_authProvider;
	
	public CloudScanServiceProvider(IProgress progress, IAuthenticationProvider authProvider) {
		m_progress = progress;
		m_authProvider = authProvider;
	}
	
	@Override
	public String createAndExecuteScan(String type, Map<String, String> params) {
		if(loginExpired() || !verifyApplication(params.get(APP_ID)))
			return null;
		
		m_progress.setStatus(new Message(Message.INFO, Messages.getMessage(EXECUTING_SCAN)));
		
		String request_url =  m_authProvider.getServer() + String.format(API_SCANNER, type);
		Map<String, String> request_headers = m_authProvider.getAuthorizationHeader(true);
		
		HttpClient client = new HttpClient();
		
		try {
			HttpResponse response = client.postForm(request_url, request_headers, params);
			int status = response.getResponseCode();
		
			JSONObject json = (JSONObject) response.getResponseBodyAsJSON();
			
			if (status == HttpsURLConnection.HTTP_CREATED) {
				m_progress.setStatus(new Message(Message.INFO, Messages.getMessage(CREATE_SCAN_SUCCESS)));
				return json.getString(ID);
			}
			else if (json != null && json.has(MESSAGE))
				m_progress.setStatus(new Message(Message.ERROR, json.getString(MESSAGE)));
			else
				m_progress.setStatus(new Message(Message.ERROR, Messages.getMessage(ERROR_SUBMITTING_SCAN, status)));
		} catch(IOException | JSONException e) {
			m_progress.setStatus(new Message(Message.ERROR, Messages.getMessage(ERROR_SUBMITTING_SCAN, e.getLocalizedMessage())));
		}
		return null;
	}

	@Override
	public String submitFile(File file) throws IOException {
		if(loginExpired())
			return null;
		
		m_progress.setStatus(new Message(Message.INFO, Messages.getMessage(UPLOADING_FILE, file.getAbsolutePath())));
		
		String fileUploadAPI =  m_authProvider.getServer() + API_FILE_UPLOAD;
		
		List<HttpPart> parts = new ArrayList<HttpPart>();
		parts.add(new HttpPart(FILE_TO_UPLOAD, file, "multipart/form-data")); //$NON-NLS-1$
		
		HttpClient client = new HttpClient();
		
		try {
			HttpResponse response = client.postMultipart(fileUploadAPI, m_authProvider.getAuthorizationHeader(true), parts);		
			JSONObject object = (JSONObject) response.getResponseBodyAsJSON();

			if (object.has(MESSAGE)) {
				m_progress.setStatus(new Message(Message.ERROR, object.getString(MESSAGE)));
			} else {
				return object.getString(FILE_ID);
			}		
		} catch (JSONException e) {
			m_progress.setStatus(new Message(Message.ERROR, Messages.getMessage(ERROR_UPLOADING_FILE, file, e.getLocalizedMessage())));
		}
		return null;
	}
	
	@Override
	public JSONObject getScanDetails(String scanId) throws IOException, JSONException {
		if(loginExpired())
			return null;
		
		String request_url = m_authProvider.getServer() + String.format(API_BASIC_DETAILS, scanId);
		Map<String, String> request_headers = m_authProvider.getAuthorizationHeader(true);
		
		HttpClient client = new HttpClient();
		HttpResponse response = client.get(request_url, request_headers, null);
		
		if (response.getResponseCode() == HttpsURLConnection.HTTP_OK || response.getResponseCode() == HttpsURLConnection.HTTP_CREATED)
			return (JSONObject) response.getResponseBodyAsJSON();

		if (response.getResponseCode() == HttpsURLConnection.HTTP_BAD_REQUEST)
			m_progress.setStatus(new Message(Message.ERROR, Messages.getMessage(ERROR_INVALID_JOB_ID, scanId)));
		
		return null;
	}
	
	public HttpResponse getNonCompliantIssues(String scanId) throws IOException, JSONException {
		if(loginExpired())
			return null;
		
		String request_url = m_authProvider.getServer() + String.format(API_NONCOMPLIANT_ISSUES, scanId);
		Map<String, String> request_headers = m_authProvider.getAuthorizationHeader(true);
		
		HttpClient client = new HttpClient();
		HttpResponse response = client.get(request_url, request_headers, null);
		
		if (response.getResponseCode() == HttpsURLConnection.HTTP_OK || response.getResponseCode() == HttpsURLConnection.HTTP_CREATED)
			return response;

		if (response.getResponseCode() == HttpsURLConnection.HTTP_BAD_REQUEST)
			m_progress.setStatus(new Message(Message.ERROR, Messages.getMessage(ERROR_INVALID_JOB_ID, scanId)));
		
		return null;
	}
	
	@Override
	public IAuthenticationProvider getAuthenticationProvider() {
		return m_authProvider;
	}
	
	private boolean loginExpired() {
		if(m_authProvider.isTokenExpired()) {
			m_progress.setStatus(new Message(Message.ERROR, Messages.getMessage(ERROR_LOGIN_EXPIRED)));
			return true;
		}
		return false;
	}
	
	private boolean verifyApplication(String appId) {
		if(appId != null && !appId.trim().equals("")) { //$NON-NLS-1$
			IApplicationProvider provider = new CloudApplicationProvider(m_authProvider);
			if(provider.getApplications() != null && provider.getApplications().keySet().contains(appId))
				return true;
		}
		m_progress.setStatus(new Message(Message.ERROR, Messages.getMessage(ERROR_INVALID_APP, appId)));
		return false;
	}
	
	@Override
	public void setProgress(IProgress progress) {
		m_progress = progress;
	}
}

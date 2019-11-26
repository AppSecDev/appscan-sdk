/**
 * © Copyright IBM Corporation 2016.
 * © Copyright HCL Technologies Ltd. 2017. 
 * LICENSE: Apache License, Version 2.0 https://www.apache.org/licenses/LICENSE-2.0
 */

package com.hcl.appscan.sdk.http;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.Map;

import org.apache.wink.json4j.JSON;
import org.apache.wink.json4j.JSONArtifact;
import org.apache.wink.json4j.JSONException;

public class HttpResponse {

	private static final int READ_SIZE = 16384;
	
	private byte[] m_content = null;
	private HttpURLConnection m_connection;

	public HttpResponse(HttpURLConnection conn) {
		m_connection = conn;
	}
	
	public int getResponseCode() {
		try {
			return m_connection.getResponseCode();
		} catch (IOException e) {
			return -1;
		}
	}
	
	public Map<String, List<String>> getResponseHeaders() {
		return m_connection.getHeaderFields();
	}
	
	public String getHeaderField(String name) {
		Map<String, List<String>> responseHeaders = getResponseHeaders();
		List<String> values = responseHeaders.containsKey(name) ? responseHeaders.get(name) : null;
		return (values==null ? null : values.get(0));
	}

	/**
	 * Converts a byte array representation of response body into a String.
	 * Returns null if no content was recorded.
	 * 
	 * @return A String representation of the response body.
	 */
	public String getResponseBodyAsString() {
		String body = null;
		try {
			if (hasResponseBody())
				body = new String(m_content, "UTF-8"); //$NON-NLS-1$
		}
		catch (IOException e) {
			body = new String(m_content);
		}
		return body;
	}

	/**
	 * Converts a byte array representation of response body into a
	 * JSONArtifact. Returns null if no response body was recorded.
	 * 
	 * @return A JSONArtifact representation of the response body.
	 * @throws JSONException If an error occurs.
	 * @throws IOException If an error occurs.
	 */
	public JSONArtifact getResponseBodyAsJSON() throws IOException, JSONException {
		if (!hasResponseBody())
			return null;
		return JSON.parse(getResponseBodyAsString());
	}

	/**
	 * Outputs the byte array representation of response body into a destination
	 * file. If the response body is empty, this method has no effect.
	 * 
	 * @param destination The destination file for the content.
	 * @throws IOException If an error occurs.
	 */
	public void getResponseBodyAsFile(File destination) throws IOException {
		FileOutputStream out = new FileOutputStream(destination, false);
		getHttpResponseBody(out);
		out.close();
	}
	
	/**
	 * Returns true if the request returns a successful response code (200 to 299).
	 * @return True if the request was successful.
	 */
	public boolean isSuccess() {
		int rc = getResponseCode(); 
		return (rc >= HttpURLConnection.HTTP_OK && rc < HttpURLConnection.HTTP_MULT_CHOICE);
	}
	
	private boolean hasResponseBody() throws IOException {
		if(m_content == null) {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			this.getHttpResponseBody(out);
			m_content = out.toByteArray();
		}
		return (m_content != null);
	}
	
	private void getHttpResponseBody(OutputStream out) throws IOException {
		int responseCode = getResponseCode();
		boolean error = (responseCode < HttpURLConnection.HTTP_OK || responseCode >= HttpURLConnection.HTTP_MULT_CHOICE);
	
		if (responseCode != HttpURLConnection.HTTP_NO_CONTENT) {
			InputStream is = error ? m_connection.getErrorStream() : m_connection.getInputStream();
			if(is == null)
				return;

			byte[] buf = new byte[READ_SIZE];
			int result = 0;
			
			while((result = is.read(buf, 0, buf.length)) > 0) {
				out.write(buf, 0, result);
			}
			
			out.flush();
			is.close();
		}
	}
}

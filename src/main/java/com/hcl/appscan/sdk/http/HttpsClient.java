/**
 * © Copyright HCL Technologies Ltd. 2019. 
 * LICENSE: Apache License, Version 2.0 https://www.apache.org/licenses/LICENSE-2.0
 */

package com.hcl.appscan.sdk.http;

import java.io.DataOutputStream;
import java.io.IOException;
//import java.net.HttpsURLConnection;
import java.net.URL;
import java.security.cert.X509Certificate;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
//import javax.net.ssl.X509TrustManager;
//import java.security.cert.X509Certificate;
import javax.net.ssl.X509TrustManager;
import org.apache.wink.json4j.JSONObject;

public class HttpsClient {
    private String m_boundary;
    private long m_totalMultipartLength;
    private long m_uploadedMultipartLength;
    private static final String CR_LF = "\r\n"; //$NON-NLS-1$
    private static final String TWO_HYPHENS = "--"; //$NON-NLS-1$
    
    private IHttpProgress m_progressAdapter;
	
	
	public enum Method {
		GET, POST, PUT, DELETE;
	}
	
	public HttpsClient(IHttpProgress progressAdapter) {
		m_progressAdapter = progressAdapter;
	}
	
	public HttpsClient() {
		this(new DefaultHttpProgress());
	}
	
	// ==============================
	// HTTP request methods
	// ==============================

	/**
	 * Submit a get request.
	 * 
	 * @param url The URL string.
	 * @param headerProperties An optional Map of header properties.
	 * @param body An optional request body or payload as a string.
	 * @return The response as a byte array.
	 * @throws IOException If an error occurs.
	 */
	public HttpResponse get(String url,
			Map<String, String> headerProperties, String body)
			throws IOException {
		return makeRequest(Method.GET, url, headerProperties, body);
	}

	/**
	 * Submit a post request.
	 * 
	 * @param url The URL string.
	 * @param headerProperties An optional Map of header properties.
	 * @param body An optional request body or payload as a string.
	 * @return The response as a byte array.
	 * @throws IOException If an error occurs.
	 */
	public HttpResponse post(String url,
			Map<String, String> headerProperties, String body)
			throws IOException {
		if (body==null) body = ""; //$NON-NLS-1$
		return makeRequest(Method.POST, url, headerProperties, body);
	}

	/**
	 * Submit a put request.
	 * 
	 * @param url The URL string.
	 * @param headerProperties An optional Map of header properties.
	 * @param body An optional request body or payload as a string.
	 * @return The response as a byte array.
	 * @throws IOException If an error occurs.
	 */
	public HttpResponse put(String url,
			Map<String, String> headerProperties, String body)
			throws IOException {
		return makeRequest(Method.PUT, url, headerProperties, body);
	}

	/**
	 * Submit a delete request.
	 * 
	 * @param url The URL string.
	 * @param headerProperties An optional Map of header properties.
	 * @param body An optional request body or payload as a string.
	 * @return The response as a byte array.
	 * @throws IOException If an error occurs.
	 */
	public HttpResponse delete(String url,
			Map<String, String> headerProperties, String body)
			throws IOException {
		return makeRequest(Method.DELETE, url, headerProperties, body);
	}
	
	/**
	 * Submit a form with parameters using the get request.
	 * 
	 * @param url The URL string.
	 * @param headerProperties An optional Map of header properties.
	 * @param params An optional Map of parameters.
	 * @return The response as a byte array.
	 * @throws IOException If an error occurs.
	 */
	public HttpResponse getForm(String url,
			Map<String, String> headerProperties, Map<String, String> params)
			throws IOException {
		String body = buildQueryString(params);
		return get(url, headerProperties, body);
	}

	/**
	 * Submit a form with parameters using the post request.
	 * 
	 * @param url The URL string.
	 * @param headerProperties An optional Map of header properties.
	 * @param params An optional Map of parameters.
	 * @return The response as a byte array.
	 * @throws IOException If an error occurs.
	 */
	public HttpResponse postForm(String url,
			Map<String, String> headerProperties, Map<String, String> params)
			throws IOException {
		String body = buildQueryString(params);
		return post(url, headerProperties, body);
	}

	/**
	 * Submit a form with parameters using the put request.
	 * 
	 * @param url The URL string.
	 * @param headerProperties An optional Map of header properties.
	 * @param params An optional Map of parameters.
	 * @return The response as a byte array.
	 * @throws IOException If an error occurs.
	 */
	public HttpResponse putForm(String url,
			Map<String, String> headerProperties, Map<String, String> params)
			throws IOException {
		String body = buildQueryString(params);
		return put(url, headerProperties, body);
	}

	/**
	 * Submit a form with parameters using the delete request.
	 * 
	 * @param url The URL string.
	 * @param headerProperties An optional Map of header properties.
	 * @param params An optional Map of parameters.
	 * @return The response as a byte array.
	 * @throws IOException If an error occurs.
	 */
	public HttpResponse deleteForm(String url,
			Map<String, String> headerProperties, Map<String, String> params)
			throws IOException {
		String body = buildQueryString(params);
		return delete(url, headerProperties, body);
	}
	
	/**
	 * Submit a multipart entity using the post request.
	 * 
	 * @param url The URL string.
	 * @param headerProperties An optional Map of header properties.
	 * @param parts A list of parts.
	 * @return The response as a byte array.
	 * @throws IOException If an error occurs.
	 */
	public HttpResponse postMultipart(String url,
			Map<String, String> headerProperties, List<HttpPart> parts)
			throws IOException {
		m_boundary = "*****"+Long.toString(System.currentTimeMillis())+"*****"; //$NON-NLS-1$ //$NON-NLS-2$
		headerProperties.put("Content-Type", "multipart/form-data; boundary=" + m_boundary); //$NON-NLS-1$ //$NON-NLS-2$
		return makeMultipartRequest(Method.POST, url, headerProperties, parts);
	}
	
	private HttpResponse makeMultipartRequest(Method method, String url,
			Map<String, String> headerProperties, List<HttpPart> parts)
					throws IOException {
		HttpsURLConnection conn = makeConnection(url, method, headerProperties);
				
		DataOutputStream outputStream = null;
		conn.setChunkedStreamingMode(1024);
		
		if (parts!=null && !parts.isEmpty()) {
			conn.setDoOutput(true);
			conn.setUseCaches(false);
			
			outputStream = new DataOutputStream(conn.getOutputStream());
			
			m_uploadedMultipartLength = 0;
			m_totalMultipartLength = getTotalPartsLength(parts);
			
			updateProgress();
			
			StringBuilder builder;
			Map<String, String> partHeaders;
			for (HttpPart part : parts) {
				builder = new StringBuilder();
				builder.append(TWO_HYPHENS+m_boundary+CR_LF);
				partHeaders = part.getPartHeaders();
				Iterator<String> headers = partHeaders.keySet().iterator();
				String header;
				while (headers.hasNext()) {
					header = headers.next();
					builder.append(header + ": " +partHeaders.get(header)+CR_LF); //$NON-NLS-1$
				}
				outputStream.writeBytes(builder.toString()+CR_LF);
				outputStream.flush();
				byte[] buffer = new byte[1024];
			    int bytesRead;
			    while ((bytesRead = part.getPartBodyInputStream().read(buffer)) != -1)
			    {
			    	outputStream.write(buffer, 0, bytesRead);
			    	m_uploadedMultipartLength += bytesRead;
					updateProgress();
			    }
				outputStream.writeBytes(CR_LF);
				outputStream.flush();
			}
			outputStream.writeBytes(TWO_HYPHENS+m_boundary+TWO_HYPHENS+CR_LF);
			outputStream.flush();
			updateProgress();
		}
		outputStream.flush();
		outputStream.close();
		
		m_progressAdapter.endProgress();
		
		return new HttpResponse(conn);
	}

	private HttpResponse makeRequest(Method method, String url,
			Map<String, String> headerProperties, String payload)
			throws IOException {
		HttpsURLConnection conn = makeConnection(url, method, headerProperties);

		// Write payload
		if (payload != null) {
			conn.setDoOutput(true);
			DataOutputStream writer = new DataOutputStream(
					conn.getOutputStream());
			writer.writeBytes(payload);
			writer.flush();
			writer.close();
		}
		
		return new HttpResponse(conn);
	}
	
	private HttpsURLConnection makeConnection(String url, Method method,
			Map<String, String> headerProperties) throws IOException {
			URL requestURL = new URL(url);
                
            try {
	                SSLContext sc = SSLContext.getInstance("TLS");
	                sc.init(null, new TrustManager[] { new TrustAllX509TrustManager() }, new java.security.SecureRandom());
	                HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
	                HttpsURLConnection.setDefaultHostnameVerifier( new HostnameVerifier() {
	                public boolean verify(String string,SSLSession ssls) {
	                	return true;
                }	
               });
            }
            catch(Exception e){
                e.printStackTrace();
            }
			HttpsURLConnection conn = null;
			conn = (HttpsURLConnection) requestURL.openConnection();
			conn.setRequestMethod(method.name());
			conn.setReadTimeout(0);

			// HTTP headers
			if (headerProperties != null) {
				for (String key : headerProperties.keySet()) {
					conn.setRequestProperty(key, headerProperties.get(key));
				}
			}
			return conn;
	}       
	
	private long getTotalPartsLength(List<HttpPart> parts) {
		long totalSize = 0;
		for (HttpPart part : parts) {
			totalSize += part.getPartLength();
		}
		return totalSize;
	}
	
	private void updateProgress() {
		int progress = (int) ((float)m_uploadedMultipartLength/m_totalMultipartLength*100);
		m_progressAdapter.setProgress(progress);
	}

        
    private String buildQueryString(Map<String , String > params){
        JSONObject obj= new JSONObject(params);
        return obj.toString();
    }
}

	class TrustAllX509TrustManager implements X509TrustManager {
	    public X509Certificate[] getAcceptedIssuers() {
	        return new X509Certificate[0];
	    }
	
	    public void checkClientTrusted(java.security.cert.X509Certificate[] certs,
	            String authType) {
	    }
	
	    public void checkServerTrusted(java.security.cert.X509Certificate[] certs,
	            String authType) {
	    }
}
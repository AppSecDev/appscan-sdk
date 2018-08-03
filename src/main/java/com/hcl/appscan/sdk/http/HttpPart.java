/**
 * © Copyright IBM Corporation 2016.
 * © Copyright HCL Technologies Ltd. 2017. 
 * LICENSE: Apache License, Version 2.0 https://www.apache.org/licenses/LICENSE-2.0
 */

package com.hcl.appscan.sdk.http;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class HttpPart {
	
	private static final String CONTENT_DISPOSITION = "Content-Disposition"; //$NON-NLS-1$
	private static final String CONTENT_TYPE = "Content-Type"; //$NON-NLS-1$
	private static final String CONTENT_LENGTH = "Content-Length"; //$NON-NLS-1$
	private long m_totalPartLength;
	
	private Map<String, String> m_partHeaders;
	private InputStream m_bodyStream;
	
	private HttpPart() {
		m_partHeaders = new HashMap<String, String>();
	}
	
	/**
	 * Construct a form field HttpPart for multipart requests
	 * 
	 * @param name The name of the field.
	 * @param value The String value of the part.
	 */
	public HttpPart(String name, String value) {
		this();
		m_partHeaders.put(CONTENT_DISPOSITION, "form-data; name=\"" + name + "\""); //$NON-NLS-1$ //$NON-NLS-2$
		m_partHeaders.put(CONTENT_LENGTH, String.valueOf(value.length()));
		m_bodyStream = new ByteArrayInputStream(value.getBytes());
		m_totalPartLength = value.getBytes().length;
	}
	
	/**
	 * Construct a file HttpPart for multipart requests
	 * 
	 * @param fieldName The name of the file part.
	 * @param file The file to be uploaded.
	 * @param contentType The content type of the file.
	 * @throws IOException If an error occurs.
	 */
	public HttpPart(String fieldName, File file, String contentType) throws IOException {
		this();
		String fileName = file.getName();
		m_partHeaders.put(CONTENT_DISPOSITION, "form-data; name=\"" + fieldName 
    			+ "\"; filename=\"" + fileName + "\""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		m_partHeaders.put(CONTENT_TYPE, contentType);
		m_partHeaders.put(CONTENT_LENGTH, String.valueOf(file.length()));
    	m_bodyStream = new FileInputStream(file);
    	m_totalPartLength = file.length();
	}
	
	/**
	 * Return the HTTP headers for this part.
	 * 
	 * @return A Map of this part's HTTP headers.
	 */
	public Map<String, String> getPartHeaders() {
		return m_partHeaders;
	}
	
	/**
	 * Add an HTTP header to this part.
	 * 
	 * @param name The name of the header.
	 * @param value The value of the header.
	 */
	public void addHeaderField(String name, String value) {
		m_partHeaders.put(name, value);
	}
	
	/**
	 * Return the InputStream of this part's body.
	 * 
	 * @return The InputStream of this part's body.
	 */
	public InputStream getPartBodyInputStream() {
		return m_bodyStream;
	}
	
	/**
	 * Return the total byte size of this part.
	 *  
	 * @return The length of bytes for this part.
	 */
	public long getPartLength() {
		return m_totalPartLength;
	}
}

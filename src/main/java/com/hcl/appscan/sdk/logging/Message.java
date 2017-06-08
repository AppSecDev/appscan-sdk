/**
 * © Copyright IBM Corporation 2016.
 * © Copyright HCL Technologies Ltd. 2017. 
 * LICENSE: Apache License, Version 2.0 https://www.apache.org/licenses/LICENSE-2.0
 */

package com.hcl.appscan.sdk.logging;

import com.hcl.appscan.sdk.CoreConstants;

public class Message implements CoreConstants {
	
	public static final String INFO_SEVERITY = ""; //$NON-NLS-1$
	public static final String WARNING_SEVERITY = "WARNING:"; //$NON-NLS-1$
	public static final String ERROR_SEVERITY = "ERROR:"; //$NON-NLS-1$
	
	public static final int INFO = 0;
	public static final int WARNING = 1;
	public static final int ERROR = 2;
	
	private static final String[] SEVERITIES = new String[] { INFO_SEVERITY, WARNING_SEVERITY, ERROR_SEVERITY};
	
	private int m_severity;
	private String m_text;
	
	public Message(int severity, String text) {
		m_severity = severity;
		m_text = text;
	}
	
	public String getText() {
		return m_text;
	}
	
	public int getSeverity() {
		return m_severity;
	}
	
	public String getSeverityString() {
		return SEVERITIES[m_severity];
	}
}

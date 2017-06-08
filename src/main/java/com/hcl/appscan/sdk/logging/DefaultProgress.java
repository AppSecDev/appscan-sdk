/**
 * © Copyright IBM Corporation 2016.
 * © Copyright HCL Technologies Ltd. 2017. 
 * LICENSE: Apache License, Version 2.0 https://www.apache.org/licenses/LICENSE-2.0
 */

package com.hcl.appscan.sdk.logging;

import java.io.PrintStream;

import com.hcl.appscan.sdk.CoreConstants;

/**
 * A default monitor for recording status.
 */
public class DefaultProgress implements IProgress, CoreConstants {

	private PrintStream m_stream;
	
	/**
	 * Constructor that sends all status messages to stdout.
	 */
	public DefaultProgress() {
		this(System.out);
	}
	
	/**
	 * Constructor
	 * @param stream The stream where status messages are sent.
	 */
	public DefaultProgress(PrintStream stream) {
		m_stream = stream;
	}
	
	@Override
	public void setStatus(Message status) {
		m_stream.println(status.getSeverityString() + status.getText());
	}

	@Override
	public void setStatus(Message status, Throwable e) {
		m_stream.println(status.getSeverityString() + status.getText() + "\n" + e.getLocalizedMessage());
	}

	@Override
	public void setStatus(Throwable e) {
		m_stream.println(Message.ERROR_SEVERITY + e.getLocalizedMessage());
	}
}
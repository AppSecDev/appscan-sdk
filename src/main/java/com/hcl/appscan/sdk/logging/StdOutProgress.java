/**
 * © Copyright HCL Technologies Ltd. 2018. 
 * LICENSE: Apache License, Version 2.0 https://www.apache.org/licenses/LICENSE-2.0
 */

package com.hcl.appscan.sdk.logging;

/**
 * An IProgress that sends all messages to stdout
 */
public class StdOutProgress implements IProgress {

	@Override
	public void setStatus(Message status) {
		System.out.println(status.getSeverityString() + status.getText());
	}

	@Override
	public void setStatus(Message status, Throwable e) {
		System.out.println(status.getSeverityString() + status.getText() + "\n" + e.getLocalizedMessage());
	}

	@Override
	public void setStatus(Throwable e) {
		System.out.println(Message.ERROR_SEVERITY + e.getLocalizedMessage());
	}
}

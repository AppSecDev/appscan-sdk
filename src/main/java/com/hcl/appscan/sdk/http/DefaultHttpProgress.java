/**
 * © Copyright IBM Corporation 2016.
 * © Copyright HCL Technologies Ltd. 2017. 
 * LICENSE: Apache License, Version 2.0 https://www.apache.org/licenses/LICENSE-2.0
 */

package com.hcl.appscan.sdk.http;

import com.hcl.appscan.sdk.Messages;

public class DefaultHttpProgress implements IHttpProgress {

	private static int m_currentProgress = 0;
	
	@Override
	public void setProgress(int percentage) {
		m_currentProgress = percentage;
		updateProgress();
	}

	@Override
	public void resetProgress() {
		m_currentProgress = 0;
		updateProgress();
	}
	
	@Override
	public void endProgress() {
		System.out.println();
	}
	
	private void updateProgress() {
		System.out.print("\r" + Messages.getMessage("transfer.progress", m_currentProgress)); //$NON-NLS-1$ //$NON-NLS-2$
	}
}

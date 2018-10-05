/**
 * © Copyright IBM Corporation 2016.
 * © Copyright HCL Technologies Ltd. 2017. 
 * LICENSE: Apache License, Version 2.0 https://www.apache.org/licenses/LICENSE-2.0
 */

package com.hcl.appscan.sdk.scanners.mobile;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import com.hcl.appscan.sdk.Messages;
import com.hcl.appscan.sdk.error.InvalidTargetException;
import com.hcl.appscan.sdk.error.ScannerException;
import com.hcl.appscan.sdk.logging.DefaultProgress;
import com.hcl.appscan.sdk.logging.IProgress;
import com.hcl.appscan.sdk.scan.IScanServiceProvider;
import com.hcl.appscan.sdk.scanners.ASoCScan;

public class MAScan extends ASoCScan implements MAConstants {
	
	private static final long serialVersionUID = 1L;
	private static final String REPORT_FORMAT = "pdf"; //$NON-NLS-1$
	
	public MAScan(Map<String, String> properties, IScanServiceProvider provider) {
		super(properties, new DefaultProgress(), provider);
	}
	
	public MAScan(Map<String, String> properties, IProgress progress, IScanServiceProvider provider) {
		super (properties, progress, provider);
	}
	
	@Override
	public void run() throws ScannerException, InvalidTargetException {
		String target = getTarget();
		
		if(target == null || !(new File(target).isFile()))
			throw new InvalidTargetException(Messages.getMessage(TARGET_INVALID, target));

		File targetFile = new File(target);
		try {
			String fileId = getServiceProvider().submitFile(targetFile);
			if(fileId == null)
				throw new ScannerException(Messages.getMessage(ERROR_FILE_UPLOAD, targetFile.getName()));

			Map<String, String> params = getProperties();
			params.put(APPLICATION_FILE_ID, fileId);
			
			setScanId(getServiceProvider().createAndExecuteScan(MOBILE_ANALYZER, params));
			if(getScanId() == null)
				throw new ScannerException(Messages.getMessage(ERROR_SUBMITTING_FILE, targetFile.getName()));
		} catch (IOException e) {
			throw new ScannerException(Messages.getMessage(SCAN_FAILED, e.getLocalizedMessage()));
		}
	}
	
	@Override
	public String getType() {
		return MA;
	}

	@Override
	public String getReportFormat() {
		return REPORT_FORMAT;
	}
}

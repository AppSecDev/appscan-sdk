/**
 * © Copyright IBM Corporation 2016.
 * © Copyright HCL Technologies Ltd. 2017, 2020. 
 * LICENSE: Apache License, Version 2.0 https://www.apache.org/licenses/LICENSE-2.0
 */

package com.hcl.appscan.sdk.scanners.sast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.transform.TransformerException;

import com.hcl.appscan.sdk.CoreConstants;
import com.hcl.appscan.sdk.Messages;
import com.hcl.appscan.sdk.error.AppScanException;
import com.hcl.appscan.sdk.error.InvalidTargetException;
import com.hcl.appscan.sdk.error.ScannerException;
import com.hcl.appscan.sdk.logging.IProgress;
import com.hcl.appscan.sdk.scan.IScanManager;
import com.hcl.appscan.sdk.scan.IScanServiceProvider;
import com.hcl.appscan.sdk.scan.ITarget;
import com.hcl.appscan.sdk.scanners.sast.targets.ISASTTarget;
import com.hcl.appscan.sdk.scanners.sast.xml.ModelWriter;
import com.hcl.appscan.sdk.scanners.sast.xml.XmlWriter;
import com.hcl.appscan.sdk.utils.SystemUtil;

public class SASTScanManager implements IScanManager{
	
	private List<ISASTTarget> m_targets;
	private SASTScan m_scan;
	private String m_workingDirectory;
	private boolean m_isThirdPartyScanningEnabled;
	private boolean m_isOpenSourceOnlyEnabled;
	private boolean m_isSourceCodeOnlyEnabled;

	public SASTScanManager(String workingDir) {
		m_workingDirectory = workingDir;
		m_targets = new ArrayList<>();
		m_isThirdPartyScanningEnabled = false;
		m_isOpenSourceOnlyEnabled = false;
		m_isSourceCodeOnlyEnabled = false;
	}

	@Override
	public void prepare(IProgress progress, Map<String, String> properties) throws AppScanException {
		createConfig();
		properties.put(CoreConstants.TARGET, m_workingDirectory);
		properties.put(SASTConstants.PREPARE_ONLY, Boolean.toString(true));
		if(!properties.containsKey(CoreConstants.SCAN_NAME))
			properties.put(CoreConstants.SCAN_NAME, getDefaultScanName());
		run(progress, properties, null);
	}

	@Override
	public void analyze(IProgress progress, Map<String, String> properties, IScanServiceProvider provider) throws AppScanException {
		if(m_scan == null || m_scan.getIrx() == null) {
			createConfig();
			properties.put(CoreConstants.TARGET, m_workingDirectory);
		}
		else
			properties.put(CoreConstants.TARGET, m_scan.getIrx().getAbsolutePath());
		
		if(!properties.containsKey(CoreConstants.SCAN_NAME))
			properties.put(CoreConstants.SCAN_NAME, getDefaultScanName());
		
		run(progress, properties, provider);
	}
	
	@Override
	public void addScanTarget(ITarget target) {
		if(target instanceof ISASTTarget)
			m_targets.add((ISASTTarget)target);
	}

	@Override
	public void getScanResults(File destination, String format) throws AppScanException {
		if(m_scan != null && m_scan.getResultsProvider() != null)
			m_scan.getResultsProvider().getResultsFile(destination, format);
		else
			throw new AppScanException(Messages.getMessage("message.results.unavailable")); //$NON-NLS-1$
	}

	/**
	 * Gets the scan id.
	 * @return The scan id as a String, if one has been created. Otherwise null.
	 */
	public String getScanId() {
		return m_scan == null ? null : m_scan.getScanId();
	}
	
	private void run(IProgress progress,Map<String, String> properties, IScanServiceProvider provider) throws AppScanException {
		try {
			m_scan = new SASTScan(properties, progress, provider);
			m_scan.run();
		} catch (InvalidTargetException | ScannerException e) {
			throw new AppScanException(e.getLocalizedMessage());
		}
	}
	
	public void setIsThirdPartyScanningEnabled(boolean isThirdPartyScanningEnabled) {
		m_isThirdPartyScanningEnabled = isThirdPartyScanningEnabled;
	}

	public void setIsOpenSourceOnlyEnabled(boolean isOpenSourceOnlyEnabled) {
		m_isOpenSourceOnlyEnabled = isOpenSourceOnlyEnabled;
	}
	
	public void setIsSourceCodeOnlyEnabled(boolean isSourceCodeOnlyEnabled) {
		m_isSourceCodeOnlyEnabled = isSourceCodeOnlyEnabled;
	}

	public void createConfig() throws AppScanException {
		createConfig(false);
	}
	
	public void createConfig(boolean useRelativeTargetPaths) throws AppScanException  {
		if(m_targets.isEmpty())
			return;
		try {
			ModelWriter writer = new XmlWriter(useRelativeTargetPaths);
			writer.initWriters(new File(m_workingDirectory));		
			writer.visit(m_targets, m_isThirdPartyScanningEnabled, m_isOpenSourceOnlyEnabled, m_isSourceCodeOnlyEnabled);
			writer.write();
		} catch (IOException | TransformerException  e) {
			throw new AppScanException(e.getLocalizedMessage(), e);
		}
	}
	
	private String getDefaultScanName() {
		return new File(m_workingDirectory).getName() + SystemUtil.getTimeStamp();
	}
}

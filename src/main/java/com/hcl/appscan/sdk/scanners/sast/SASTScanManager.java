/**
 * © Copyright IBM Corporation 2016.
 * © Copyright HCL Technologies Ltd. 2017. 
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
import com.hcl.appscan.sdk.error.AppScanException;
import com.hcl.appscan.sdk.error.InvalidTargetException;
import com.hcl.appscan.sdk.error.ScannerException;
import com.hcl.appscan.sdk.logging.IProgress;
import com.hcl.appscan.sdk.scan.IScanManager;
import com.hcl.appscan.sdk.scan.IScanServiceProvider;
import com.hcl.appscan.sdk.scan.ITarget;
import com.hcl.appscan.sdk.scanners.sast.SASTConstants;
import com.hcl.appscan.sdk.scanners.sast.SASTScan;
import com.hcl.appscan.sdk.scanners.sast.targets.ISASTTarget;
import com.hcl.appscan.sdk.scanners.sast.xml.ModelWriter;
import com.hcl.appscan.sdk.scanners.sast.xml.XmlWriter;

public class SASTScanManager implements IScanManager{
	
	private List<ISASTTarget> m_targets;
	private SASTScan m_scan;
	private String m_workingDirectory;

	public SASTScanManager(String workingDir) {
		m_workingDirectory = workingDir;
		m_targets = new ArrayList<ISASTTarget>();
	}

	@Override
	public void prepare(IProgress progress, Map<String, String> properties) throws AppScanException {
		createConfig();
		properties.put(CoreConstants.TARGET, m_workingDirectory);
		properties.put(SASTConstants.PREPARE_ONLY, Boolean.toString(true));
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
		
		run(progress, properties, provider);
	}
	
	@Override
	public void addScanTarget(ITarget target) {
		if(target instanceof ISASTTarget)
			m_targets.add((ISASTTarget)target);
	}

	private  void run(IProgress progress,Map<String, String> properties, IScanServiceProvider provider) throws AppScanException {
		try {
			m_scan = new SASTScan(properties, progress, provider);
			m_scan.run();
		} catch (InvalidTargetException | ScannerException e) {
			throw new AppScanException(e.getLocalizedMessage());
		}
	}

	private void createConfig() throws AppScanException  {
		if(m_targets.isEmpty())
			return;
		try {
			ModelWriter writer = new XmlWriter();
			writer.initWriters(new File(m_workingDirectory));		
			writer.visit(m_targets);
			writer.write();
		} catch (IOException | TransformerException  e) {
			throw new AppScanException(e.getLocalizedMessage(), e);
		}
	}
}

package com.hcl.appscan.sdk.scanners.sast.targets;

import java.io.File;
import java.util.Collections;
import java.util.Map;

/**
 * A generic target for a given file or directory.
 */
public class GenericTarget extends DefaultTarget {

	private File m_targetFile;
	
	public GenericTarget(String target) {
		m_targetFile = new File(target);
	}
	
	@Override
	public File getTargetFile() {
		return m_targetFile;
	}

	@Override
	public Map<String, String> getProperties() {
		return Collections.emptyMap();
	}
}

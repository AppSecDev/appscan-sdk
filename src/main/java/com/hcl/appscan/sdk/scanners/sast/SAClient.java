/**
 * © Copyright IBM Corporation 2016.
 * © Copyright HCL Technologies Ltd. 2017. 
 * LICENSE: Apache License, Version 2.0 https://www.apache.org/licenses/LICENSE-2.0
 */

package com.hcl.appscan.sdk.scanners.sast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.hcl.appscan.sdk.CoreConstants;
import com.hcl.appscan.sdk.Messages;
import com.hcl.appscan.sdk.error.ScannerException;
import com.hcl.appscan.sdk.logging.DefaultProgress;
import com.hcl.appscan.sdk.logging.IProgress;
import com.hcl.appscan.sdk.logging.Message;
import com.hcl.appscan.sdk.utils.ArchiveUtil;
import com.hcl.appscan.sdk.utils.ServiceUtil;
import com.hcl.appscan.sdk.utils.SystemUtil;

public class SAClient implements SASTConstants {

	private static final File DEFAULT_INSTALL_DIR = new File(System.getProperty("user.home"), ".appscan"); //$NON-NLS-1$ //$NON-NLS-2$
	private static final String SACLIENT = "SAClientUtil"; //$NON-NLS-1$
	private static final String VERSION_INFO = "version.info"; //$NON-NLS-1$
	
	private IProgress m_progress;
	private ProcessBuilder m_builder;
	private File m_installDir;
	
	public SAClient() {
		this(new DefaultProgress());
	}
	
	public SAClient(IProgress progress) {
		m_progress = progress;
		String install = System.getProperty(CoreConstants.SACLIENT_INSTALL_DIR);
		m_installDir = install == null ? DEFAULT_INSTALL_DIR : new File(install);
	}
	
	/**
	 * Run the SAClient
	 * @param workingDir The directory where the SAClient will run.
	 * @param properties A Map of properties that will be converted to program arguments.
	 * @return The process exit code, 0 for success.
	 * @throws IOException
	 * @throws ScannerException
	 */
	public int run(String workingDir, Map<String, String> properties) throws IOException, ScannerException {
		return runClient(workingDir, getClientArgs(properties));
	}
	
	/**
	 * @deprecated Use {@link #run(String, Map)} instead.
	 */
	@Deprecated
	public int run(String workingDir, List<String> args) throws IOException, ScannerException {
		return runClient(workingDir, args);
	}
		
	private int runClient(String workingDir, List<String> args) throws IOException, ScannerException {
		ArrayList<String> arguments = new ArrayList<String>();
		arguments.add(getClientScript());
		arguments.addAll(args);
		m_builder = new ProcessBuilder(arguments);
		m_builder.directory(new File(workingDir));
		m_builder.redirectErrorStream(true);
		
		m_progress.setStatus(new Message(Message.INFO, Messages.getMessage(PREPARING_IRX, getLocalClientVersion())));
		final Process proc = m_builder.start();
		new Thread(new Runnable() {
			@Override
			public void run() {
				BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()));
				String line;
				try {
					while((line = reader.readLine()) != null)
						m_progress.setStatus(new Message(Message.INFO, line));
				}
				catch(IOException e) {
					m_progress.setStatus(e);
				} 
				finally {
					try {
						reader.close();
					} catch (IOException e) {
						m_progress.setStatus(e);
					}
				}
			}
		}).start();
		
		try {
			proc.waitFor();
		} catch (InterruptedException e) {
			m_progress.setStatus(e);
			return -1;
		}

		return proc.exitValue();
	}
	
	/**
	 * Gets the absolute path to the "appscan" script for running the IRGen process, downloading the package if it's
	 * not found or if the current version is out of date.
	 * @return The absolute path to the "appscan" script.
	 * @throws IOException
	 * @throws ScannerException
	 */
	public String getClientScript() throws IOException, ScannerException {
		//See if we already have the client package.
		String scriptPath = "bin" + File.separator + getScriptName(); //$NON-NLS-1$
		File install = findClientInstall();
		
		if(install != null && new File(install, scriptPath).isFile() && !shouldUpdateClient())
			return new File(install, scriptPath).getAbsolutePath();
		
		//Download it.
		m_progress.setStatus(new Message(Message.INFO, Messages.getMessage(DOWNLOADING_CLIENT)));
		if(install != null && install.isDirectory())
			deleteDirectory(install);
		
		File clientZip = new File(m_installDir, SACLIENT + ".zip"); //$NON-NLS-1$
		if(clientZip.isFile())
			clientZip.delete();
		
		try {
			ServiceUtil.getSAClientUtil(clientZip);
		} catch(OutOfMemoryError e) {
			throw new ScannerException(Messages.getMessage(DOWNLOAD_OUT_OF_MEMORY));
		} catch(IOException e) {
			throw new ScannerException(Messages.getMessage(ERROR_DOWNLOADING_CLIENT, e.getLocalizedMessage()));
		}
		
		if(clientZip.isFile()) {
			m_progress.setStatus(new Message(Message.INFO, Messages.getMessage(DOWNLOAD_COMPLETE)));
			m_progress.setStatus(new Message(Message.INFO, Messages.getMessage(EXTRACTING_CLIENT)));
			ArchiveUtil.unzip(clientZip, m_installDir);
			m_progress.setStatus(new Message(Message.INFO, Messages.getMessage(DONE)));
		}

		return new File(findClientInstall(), scriptPath).getAbsolutePath();
	}
	
	private String getScriptName() {
		return SystemUtil.isWindows() ? WIN_SCRIPT : UNIX_SCRIPT;
	}
	
	private boolean shouldUpdateClient() throws IOException {
		String serverVersion = ServiceUtil.getSAClientVersion();
		String localVersion = getLocalClientVersion();

		if(localVersion != null && serverVersion != null) {
			String[] local = localVersion.split("\\."); //$NON-NLS-1$
			String[] server = serverVersion.split("\\."); //$NON-NLS-1$

			for(int iter = 0; iter < local.length && iter < server.length; iter++) {
				int lVersion = Integer.parseInt(local[iter]);
				int sVersion = Integer.parseInt(server[iter]);
				
				if (((iter==0) && lVersion<sVersion)
						|| (iter==1 && lVersion<sVersion)
						|| (iter==2 && lVersion<sVersion)) {
					m_progress.setStatus(new Message(Message.INFO, Messages.getMessage(SACLIENT_OUTDATED, localVersion, serverVersion)));
					return true;
				}
			}
		}
		return false;
	}
	
	private File findClientInstall() {
		if(!m_installDir.isDirectory())
			return null;
		
		File files[] = m_installDir.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.startsWith(SACLIENT) && new File(dir, name).isDirectory();
			}
		});
		return files.length == 0 ? null : files[0];
	}
	
	private String getLocalClientVersion() {
		File versionInfo = new File(findClientInstall(), VERSION_INFO);
		String version = null;
		BufferedReader reader = null;
		
		try {
			reader = new BufferedReader(new FileReader(versionInfo));
			version = reader.readLine(); //The version is the first line of the version.info file.
		} catch (IOException e) {
			m_progress.setStatus(new Message(Message.ERROR, Messages.getMessage(ERROR_CHECKING_SACLIENT_VER, e.getLocalizedMessage())));
		} finally {
			try {
				if(reader != null)
					reader.close();
			} catch (IOException e) {
			}
		}
		return version;
	}
	
	private void deleteDirectory(File directory) {
		if(!directory.isDirectory())
			directory.delete();
		
		for(File file : directory.listFiles()) {
			if(file.isDirectory())
				deleteDirectory(file);
			else
				file.delete();
		}
		directory.delete();
	}
	
	/**
	 * Given a Map of properties, returns a List of arguments.
	 * @param properties The Map of properties.
	 * @return A list of command line arguments for the SAClient to use
	 */
	private List<String> getClientArgs(Map<String, String> properties) {
		ArrayList<String> args = new ArrayList<String>();
		args.add(PREPARE);
		
		if(properties.containsKey(CoreConstants.SCAN_NAME)) {
			args.add(OPT_NAME);
			args.add(properties.get(CoreConstants.SCAN_NAME));
		}
		if(properties.containsKey(LOG_LOCATION)) {
			args.add(OPT_LOG_LOCATION);
			args.add(properties.get(LOG_LOCATION));
		}
		if(properties.containsKey(SAVE_LOCATION)) {
			args.add(OPT_SAVE_LOCATION);
			args.add(properties.get(SAVE_LOCATION));
		}
		if(properties.containsKey(CONFIG_FILE)) {
			args.add(OPT_CONFIG);
			args.add(properties.get(CONFIG_FILE));
		}
		if(properties.containsKey(DEBUG))
			args.add(OPT_DEBUG);
		if(properties.containsKey(VERBOSE))
			args.add(OPT_VERBOSE);
		if(properties.containsKey(THIRD_PARTY))
			args.add(OPT_THIRD_PARTY);
		
		return args;
	}
}

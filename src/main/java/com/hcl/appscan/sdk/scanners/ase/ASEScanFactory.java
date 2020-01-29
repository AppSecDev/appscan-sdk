/**
 * © Copyright HCL Technologies Ltd. 2019. 
 * LICENSE: Apache License, Version 2.0 https://www.apache.org/licenses/LICENSE-2.0
 */

package com.hcl.appscan.sdk.scanners.ase;

import com.hcl.appscan.sdk.auth.IAuthenticationProvider;
import com.hcl.appscan.sdk.logging.IProgress;
import com.hcl.appscan.sdk.scan.ASEScanServiceProvider;
import com.hcl.appscan.sdk.scan.IScan;
import com.hcl.appscan.sdk.scan.IScanFactory;
import com.hcl.appscan.sdk.scan.IScanServiceProvider;
import java.util.Map;

public class ASEScanFactory implements IScanFactory {

    @Override
    public IScan create(Map<String, String> properties, IProgress progress, IAuthenticationProvider authProvider) {
        IScanServiceProvider serviceProvider = new ASEScanServiceProvider(progress, authProvider);
        return new ASEScan(properties, progress, serviceProvider);
    }

    @Override
    public String getType() {
        return ASEConstants.ASE_DAST;
    }
}
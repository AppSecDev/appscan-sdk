/**
 * © Copyright HCL Technologies Ltd. 2019. 
 * LICENSE: Apache License, Version 2.0 https://www.apache.org/licenses/LICENSE-2.0
 */

package com.hcl.appscan.sdk.configuration.ase;

import java.util.Map;

public interface IComponent {
    
	/**
	 * Gets the available components for a component type.
	 * @return A Map of components, keyed by the component id.
	 */
	public Map<String, String> getComponents();
	
	/**
	 * Gets the name of the component with the given id.
	 * @param id The id of the component.
	 * @return The component name.
	 */
	public String getComponentName(String id);
}
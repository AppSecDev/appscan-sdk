/**
 * © Copyright IBM Corporation 2016.
 * © Copyright HCL Technologies Ltd. 2017. 
 * LICENSE: Apache License, Version 2.0 https://www.apache.org/licenses/LICENSE-2.0
 */

package com.hcl.appscan.sdk.presence;

import java.util.Map;

public interface IPresenceProvider {

	/**
	 * Gets the available presences.
	 * @return A Map of presences, keyed by the presence id.
	 */
	public Map<String, String> getPresences();
	
	/**
	 * Gets the name of the presence with the given id. 
	 * @param id The id of the presence.
	 * @return The presence name.
	 */
	public String getName(String id);
	
	/**
	 * Deletes the presence with the given id.
	 * @param id The id of the presence to delete.
	 * @return true if the delete was successful.
	 */
	public boolean delete(String id);
	
	/**
	 * Retrieves details about the presence with the given id.
	 * @param id The id of the presence.
	 * @return A Map containing details of the presence.
	 */
	public Map<String, String> getDetails(String id);
	
	/**
	 * Generate a new key for the given presence.
	 * @param id The id of the presence to generate the new key for.
	 * @return The new key.
	 */
	public String getNewKey(String id);
}

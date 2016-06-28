/*******************************************************************************
 * Copyright (c) 2011, 2016 Eurotech and/or its affiliates
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Eurotech - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.kapua.service.datastore.internal.elasticsearch;

import java.net.UnknownHostException;
import java.util.Map;
import java.util.regex.Pattern;

import org.eclipse.kapua.service.datastore.internal.config.KapuaDatastoreConfig;
import org.eclipse.kapua.service.datastore.internal.config.KapuaDatastoreConfigKeys;
import org.elasticsearch.client.Client;


public class EsClient {

	private static Client client;
	
	public static Client getcurrent() throws UnknownHostException, EsDatastoreException {
		// TODO read hostname and port from configurations
		if (client == null) 
		{
	        KapuaDatastoreConfig config = KapuaDatastoreConfig.getInstance();
	        Map<String, String> map = config.getMap(String.class, KapuaDatastoreConfigKeys.ELASTICSEARCH_NODES, "[0-9]+");
	        String[] esNodes = new String[] {};
	        if (map != null)
	            esNodes = map.values().toArray(new String[] {});
	        
	        if (esNodes == null || esNodes.length == 0)
	        	throw new EsDatastoreException("No elasticsearch nodes found");
	        
	        String[] nodeParts = getNodeParts(esNodes[0]);
	        String esHost = null;
	        String esPort = null;
	        
	        if (nodeParts.length > 0)
	            esHost = nodeParts[0];
	        if (nodeParts.length > 1)
	            esPort = nodeParts[1];
	        
			client = EsUtils.getEsClient(esHost, Integer.parseInt(esPort));
		}
		return client;
	}
	
	private static String[] getNodeParts(String node)
	{
	    if (node==null)
	        return new String[] {};
	    
	    String[] split = node.split(":");
	    return split;
	}
}

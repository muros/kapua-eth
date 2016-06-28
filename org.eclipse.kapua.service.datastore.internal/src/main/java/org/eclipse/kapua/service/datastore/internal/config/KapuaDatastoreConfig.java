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
package org.eclipse.kapua.service.datastore.internal.config;

import org.apache.commons.configuration.Configuration;
import org.eclipse.kapua.commons.config.AbstractKapuaConfig;

public class KapuaDatastoreConfig extends AbstractKapuaConfig <KapuaDatastoreConfigKeys>
{
	private static final KapuaDatastoreConfig instance;
	static {
		instance = new KapuaDatastoreConfig(KapuaDatastoreConfiguration.getConfiguration()); 
	}
	
	private KapuaDatastoreConfig(Configuration config) {
		super(config);
	}
	
	public static KapuaDatastoreConfig getInstance() {
		return instance;
	}
}

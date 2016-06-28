/*******************************************************************************
 * Copyright (c) 2011, 2016 Eurotech and/or its affiliates
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *      Eurotech - initial API and implementation
 *******************************************************************************/
/*******************************************************************************
 * Copyright (c) 2011, 2016 Eurotech and/or its affiliates
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *      Eurotech - initial API and implementation
 *******************************************************************************/
package org.eclipse.kapua.service.account.internal.config;

import java.net.URL;

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.eclipse.kapua.commons.config.KapuaEnvironmentConfiguration;
import org.eclipse.kapua.commons.util.ResourceUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * EdcConfig provides access to the configuration properties for the Everyware
 * Cloud platform. To add new properties, edit the edc-config.properties
 * resource property file and add your keys.
 */
public class KapuaAccountConfiguration
{
	private static final Logger s_logger = LoggerFactory.getLogger(KapuaAccountConfiguration.class);
	
	private static final String ACCOUNT_CONFIG_RESOURCE = "kapua-account-config.properties";

	protected static Configuration s_config = null;
    static {
    	
        CompositeConfiguration compositeConfig = new CompositeConfiguration();
        try {        
        	URL serviceConfigUrl = ResourceUtils.getResource(ACCOUNT_CONFIG_RESOURCE);
        	compositeConfig.addConfiguration(new PropertiesConfiguration(serviceConfigUrl));
        }
        catch (Exception e) {
            s_logger.error("Error loading PropertiesConfiguration", e);
            throw new ExceptionInInitializerError(e);
        }

        compositeConfig.addConfiguration(KapuaEnvironmentConfiguration.getConfiguration());
        s_config = compositeConfig;
    }
    
    private KapuaAccountConfiguration() {}
    	
    /**
     * Returns the Configuration instance used by this KapuaCommonsConfiguration.
     * @return
     */
    public static Configuration getConfiguration() {
        return s_config;
    }
}

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
package org.eclipse.kapua.service.authentication.shiro.config;

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
public class KapuaAuthenticationConfiguration
{
	private static final Logger s_logger = LoggerFactory.getLogger(KapuaAuthenticationConfiguration.class);
	
	private static final String AUTHENTICATION_CONFIG_RESOURCE = "kapua-authentication-config.properties";

	protected static Configuration s_config = null;
    static {
    	
        CompositeConfiguration compositeConfig = new CompositeConfiguration();
        try {        
        	URL serviceConfigUrl = ResourceUtils.getResource(AUTHENTICATION_CONFIG_RESOURCE);
        	compositeConfig.addConfiguration(new PropertiesConfiguration(serviceConfigUrl));
        }
        catch (Exception e) {
            s_logger.error("Error loading PropertiesConfiguration", e);
            throw new ExceptionInInitializerError(e);
        }

        compositeConfig.addConfiguration(KapuaEnvironmentConfiguration.getConfiguration());
        s_config = compositeConfig;
    }
    
    private KapuaAuthenticationConfiguration() {}
    	
    /**
     * Returns the Configuration instance used by this KapuaCommonsConfiguration.
     * @return
     */
    public static Configuration getConfiguration() {
        return s_config;
    }
}

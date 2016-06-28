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
package org.eclipse.kapua.commons.config;

import java.net.URL;

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.SystemConfiguration;
import org.eclipse.kapua.commons.util.ResourceUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * KapuaConfiguration provides access to the configuration properties for Kapua Services.
 * To add new properties, edit the kapua-config.properties resource property file and add your keys.
 */
public class KapuaEnvironmentConfiguration
{
    private static final Logger  s_logger        = LoggerFactory.getLogger(KapuaEnvironmentConfiguration.class);

    private static final String  CONFIG_RESOURCE = "kapua-environment-config.properties";

    private static Configuration s_config        = null;
    static {

        // env+properties configuration
        CompositeConfiguration compositeConfig = new CompositeConfiguration();
        compositeConfig.addConfiguration(new SystemConfiguration());
        try {
            URL configLocalUrl = ResourceUtils.getResource(CONFIG_RESOURCE);
            compositeConfig.addConfiguration(new PropertiesConfiguration(configLocalUrl));
        }
        catch (Exception e) {
            s_logger.error("Error loading PropertiesConfiguration", e);
            throw new ExceptionInInitializerError(e);
        }
        s_config = compositeConfig;
    }

    private KapuaEnvironmentConfiguration()
    {
    }

    /**
     * Returns the Configuration instance used by this KapuaCommonsConfiguration.
     * 
     * @return
     */
    public static Configuration getConfiguration()
    {
        return s_config;
    }
}

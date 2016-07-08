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
package org.eclipse.kapua.app.console.config;

import java.net.URL;

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.eclipse.kapua.commons.setting.EnvironmentConfiguration;
import org.eclipse.kapua.commons.util.ResourceUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * EdcConfig provides access to the configuration properties for the Everyware
 * Cloud platform. To add new properties, edit the edc-config.properties
 * resource property file and add your keys.
 */
public class ConsoleConfiguration
{
    private static final Logger    s_logger             = LoggerFactory.getLogger(ConsoleConfiguration.class);

    private static final String    USER_CONFIG_RESOURCE = "console-config.properties";

    protected static Configuration s_config             = null;
    static {

        CompositeConfiguration compositeConfig = new CompositeConfiguration();
        try {
            URL serviceConfigUrl = ResourceUtils.getResource(USER_CONFIG_RESOURCE);
            compositeConfig.addConfiguration(new PropertiesConfiguration(serviceConfigUrl));
        }
        catch (Exception e) {
            s_logger.error("Error loading PropertiesConfiguration", e);
            throw new ExceptionInInitializerError(e);
        }

        compositeConfig.addConfiguration(EnvironmentConfiguration.getConfiguration());
        s_config = compositeConfig;
    }

    private ConsoleConfiguration()
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

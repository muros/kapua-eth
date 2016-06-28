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

import org.apache.commons.configuration.Configuration;

public class KapuaEnvironmentConfig extends AbstractKapuaConfig<KapuaEnvironmentConfigKeys>
{
    private static final KapuaEnvironmentConfig instance;
    static {
        instance = new KapuaEnvironmentConfig(KapuaEnvironmentConfiguration.getConfiguration());
    }

    private KapuaEnvironmentConfig(Configuration config)
    {
        super(config);
    }

    public static KapuaEnvironmentConfig getInstance()
    {
        return instance;
    }
}

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
package org.eclipse.kapua.locator.spi.config;

import org.apache.commons.configuration.Configuration;
import org.eclipse.kapua.commons.setting.AbstractKapuaSettings;

public class KapuaLocatorConfig extends AbstractKapuaSettings<KapuaLocatorConfigKeys>
{
    private static final KapuaLocatorConfig instance;
    static {
        instance = new KapuaLocatorConfig(KapuaLocatorConfiguration.getConfiguration());
    }

    private KapuaLocatorConfig(Configuration config)
    {
        super(config);
    }

    public static KapuaLocatorConfig getInstance()
    {
        return instance;
    }
}

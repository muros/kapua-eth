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

import org.apache.commons.configuration.Configuration;
import org.eclipse.kapua.commons.setting.AbstractKapuaConfig;

public class ConsoleConfig extends AbstractKapuaConfig<ConsoleConfigKeys>
{
    private static final ConsoleConfig instance;
    static {
        instance = new ConsoleConfig(ConsoleConfiguration.getConfiguration());
    }

    private ConsoleConfig(Configuration config)
    {
        super(config);
    }

    public static ConsoleConfig getInstance()
    {
        return instance;
    }
}

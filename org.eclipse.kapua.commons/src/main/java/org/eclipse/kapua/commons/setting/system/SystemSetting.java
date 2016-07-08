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
package org.eclipse.kapua.commons.setting.system;

import org.eclipse.kapua.commons.setting.AbstractKapuaSettings;

public class SystemSetting extends AbstractKapuaSettings<SystemSettingKey>
{
    private static final String        CONFIG_RESOURCE_NAME = "kapua-environment-config.properties";

    private static final SystemSetting instance             = new SystemSetting();

    private SystemSetting()
    {
        super(CONFIG_RESOURCE_NAME);
    }

    public static SystemSetting getInstance()
    {
        return instance;
    }
}

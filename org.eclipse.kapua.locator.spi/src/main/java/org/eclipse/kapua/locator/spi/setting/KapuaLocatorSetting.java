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
package org.eclipse.kapua.locator.spi.setting;

import org.eclipse.kapua.commons.setting.AbstractKapuaSetting;

public class KapuaLocatorSetting extends AbstractKapuaSetting<KapuaLocatorSettingKeys>
{
    private static final String              LOCATOR_CONFIG_RESOURCE = "kapua-locator-config.properties";

    private static final KapuaLocatorSetting instance                = new KapuaLocatorSetting();

    private KapuaLocatorSetting()
    {
        super(LOCATOR_CONFIG_RESOURCE);
    }

    public static KapuaLocatorSetting getInstance()
    {
        return instance;
    }
}

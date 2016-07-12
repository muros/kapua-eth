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
package org.org.eclipse.kapua.client.pool.setting.internal;

import org.eclipse.kapua.commons.setting.AbstractKapuaSetting;

public class KapuaClientPoolSetting extends AbstractKapuaSetting<KapuaClientPoolSettingKeys>
{
    private static final String                 MQTT_CLIENT_CONFIG_RESOURCE = "client-pool-setting.properties";

    private static final KapuaClientPoolSetting instance                    = new KapuaClientPoolSetting();

    private KapuaClientPoolSetting()
    {
        super(MQTT_CLIENT_CONFIG_RESOURCE);
    }

    public static KapuaClientPoolSetting getInstance()
    {
        return instance;
    }
}

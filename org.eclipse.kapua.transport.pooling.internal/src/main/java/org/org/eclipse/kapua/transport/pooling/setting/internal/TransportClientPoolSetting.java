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
package org.org.eclipse.kapua.transport.pooling.setting.internal;

import org.eclipse.kapua.commons.setting.AbstractKapuaSetting;

public class TransportClientPoolSetting extends AbstractKapuaSetting<TransportClientPoolSettingKeys>
{
    private static final String                 MQTT_CLIENT_CONFIG_RESOURCE = "client-pool-setting.properties";

    private static final TransportClientPoolSetting instance                    = new TransportClientPoolSetting();

    private TransportClientPoolSetting()
    {
        super(MQTT_CLIENT_CONFIG_RESOURCE);
    }

    public static TransportClientPoolSetting getInstance()
    {
        return instance;
    }
}

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
package org.eclipse.kapua.transport.mqtt.pooling.setting;

import org.eclipse.kapua.commons.setting.AbstractKapuaSetting;

public class MqttClientPoolSetting extends AbstractKapuaSetting<MqttClientPoolSettingKeys>
{
    private static final String                MQTT_CLIENT_CONFIG_RESOURCE = "mqtt-client-pool-setting.properties";

    private static final MqttClientPoolSetting instance                    = new MqttClientPoolSetting();

    private MqttClientPoolSetting()
    {
        super(MQTT_CLIENT_CONFIG_RESOURCE);
    }

    public static MqttClientPoolSetting getInstance()
    {
        return instance;
    }
}

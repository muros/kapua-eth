/*******************************************************************************
 * Copyright (c) 2011, 2016 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.transport.mqtt.setting;

import org.eclipse.kapua.commons.setting.AbstractKapuaSetting;

public class MqttClientSetting extends AbstractKapuaSetting<MqttClientSettingKeys>
{
    private static final String            MQTT_CLIENT_CONFIG_RESOURCE = "mqtt-client-setting.properties";

    private static final MqttClientSetting instance                    = new MqttClientSetting();

    private MqttClientSetting()
    {
        super(MQTT_CLIENT_CONFIG_RESOURCE);
    }

    public static MqttClientSetting getInstance()
    {
        return instance;
    }
}

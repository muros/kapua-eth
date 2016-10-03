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

import org.eclipse.kapua.commons.setting.SettingKey;

public enum MqttClientSettingKeys implements SettingKey
{
    TRANSPORT_PROTOCOL_VERSION("transport.protocol.version"),

    TRANSPORT_TOPIC_SEPARATOR("transport.topic.separator"),

    SEND_TIMEOUT_MAX("send.timeout.max"),
    ;

    private String key;

    private MqttClientSettingKeys(String key)
    {
        this.key = key;
    }

    public String key()
    {
        return key;
    }
}

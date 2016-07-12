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
package org.eclipse.kapua.client.mqtt.setting;

import org.eclipse.kapua.commons.setting.SettingKey;

public enum MqttClientSettingKeys implements SettingKey
{
    CLIENT_POOL_CLIENT_PREFIX("client.pool.client.prefix"),
    CLIENT_POOL_CLIENT_PROTOCOL_VERSION("client.pool.client.protocol.version");

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

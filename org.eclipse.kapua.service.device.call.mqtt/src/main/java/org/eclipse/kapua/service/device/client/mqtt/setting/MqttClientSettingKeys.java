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
package org.eclipse.kapua.service.device.client.mqtt.setting;

import org.eclipse.kapua.commons.setting.SettingKey;

public enum MqttClientSettingKeys implements SettingKey
{
    CLIENT_POOL_SIZE_MINIDLE("client.pool.size.min"),
    CLIENT_POOL_SIZE_MAXIDLE("client.pool.size.max"),
    CLIENT_POOL_SIZE_MAXTOTAL("client.pool.size.maxTotal"),
    CLIENT_POOL_EVICTION_INTERVAL("client.pool.eviction.interval"),

    CLIENT_POOL_WHEN_EXAUSTED_BLOCK("client.pool.when.exhausted.block"),
    CLIENT_POOL_WHEN_IDLE_TEST("client.pool.when.idle.test"),

    CLIENT_POOL_ON_BORROW_TEST("client.pool.on.borrow.test"),
    CLIENT_POOL_ON_RETURN_TEST("client.pool.on.return.test"),

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

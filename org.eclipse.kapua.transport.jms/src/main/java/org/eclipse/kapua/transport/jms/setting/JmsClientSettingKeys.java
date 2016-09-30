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
package org.eclipse.kapua.transport.jms.setting;

import org.eclipse.kapua.commons.setting.SettingKey;

public enum JmsClientSettingKeys implements SettingKey
{
    TRANSPORT_TOPIC_SEPARATOR("transport.topic.separator"),
    ;

    private String key;

    private JmsClientSettingKeys(String key)
    {
        this.key = key;
    }

    public String key()
    {
        return key;
    }
}

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
package org.eclipse.kapua.service.device.management.commons.setting;

import org.eclipse.kapua.commons.setting.SettingKey;

public enum DeviceManagementSettingKey implements SettingKey
{
    CHAR_ENCODING("character.encoding"),

    REQUEST_TIMEOUT("request.timeout");

    private String key;

    private DeviceManagementSettingKey(String key)
    {
        this.key = key;
    }

    public String key()
    {
        return key;
    }
}

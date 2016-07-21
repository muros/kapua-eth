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
package org.org.eclipse.kapua.transport.jms.setting;

import org.eclipse.kapua.commons.setting.AbstractKapuaSetting;

public class JmsClientSetting extends AbstractKapuaSetting<JmsClientSettingKeys>
{
    private static final String           MQTT_CLIENT_CONFIG_RESOURCE = "jms-client-setting.properties";

    private static final JmsClientSetting instance                    = new JmsClientSetting();

    private JmsClientSetting()
    {
        super(MQTT_CLIENT_CONFIG_RESOURCE);
    }

    public static JmsClientSetting getInstance()
    {
        return instance;
    }
}

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
package org.eclipse.kapua.commons.util;

import java.net.URI;
import java.net.URISyntaxException;

import org.eclipse.kapua.commons.setting.system.SystemSetting;
import org.eclipse.kapua.commons.setting.system.SystemSettingKey;

public class SystemUtils
{
    public static URI getBrokerURI()
        throws URISyntaxException
    {
        SystemSetting envConfig = SystemSetting.getInstance();
        return new URI(envConfig.getString(SystemSettingKey.BROKER_SCHEME),
                       null,
                       envConfig.getString(SystemSettingKey.BROKER_HOST),
                       envConfig.getInt(SystemSettingKey.BROKER_PORT),
                       null,
                       null,
                       null);
    }
}

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
package org.eclipse.kapua.service.authentication.shiro.setting;

import org.eclipse.kapua.commons.setting.AbstractKapuaSetting;

public class KapuaAuthenticationSetting extends AbstractKapuaSetting<KapuaAuthenticationSettingKeys>
{
    private static final String                     AUTHENTICATION_CONFIG_RESOURCE = "kapua-authentication-setting.properties";

    private static final KapuaAuthenticationSetting instance                       = new KapuaAuthenticationSetting();

    private KapuaAuthenticationSetting()
    {
        super(AUTHENTICATION_CONFIG_RESOURCE);
    }

    public static KapuaAuthenticationSetting getInstance()
    {
        return instance;
    }
}
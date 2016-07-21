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
package org.eclipse.kapua.service.authorization.shiro.setting;

import org.eclipse.kapua.commons.setting.AbstractKapuaSetting;

public class KapuaAuthorizationSetting extends AbstractKapuaSetting<KapuaAuthorizationSettingKeys>
{
    private static final String                    AUTHORIZATION_SETTING_RESOURCE = "kapua-authorization-setting.properties";

    private static final KapuaAuthorizationSetting instance                       = new KapuaAuthorizationSetting();

    private KapuaAuthorizationSetting()
    {
        super(AUTHORIZATION_SETTING_RESOURCE);
    }

    public static KapuaAuthorizationSetting getInstance()
    {
        return instance;
    }
}
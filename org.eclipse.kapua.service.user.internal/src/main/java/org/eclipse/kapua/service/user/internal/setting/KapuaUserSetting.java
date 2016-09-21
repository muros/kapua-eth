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
package org.eclipse.kapua.service.user.internal.setting;

import org.eclipse.kapua.commons.setting.AbstractKapuaSetting;

public class KapuaUserSetting extends AbstractKapuaSetting<KapuaUserSettingKeys>
{
    private static final String           USER_SETTING_RESOURCE = "kapua-user-setting.properties";

    private static final KapuaUserSetting instance              = new KapuaUserSetting();

    private KapuaUserSetting()
    {
        super(USER_SETTING_RESOURCE);
    }

    public static KapuaUserSetting getInstance()
    {
        return instance;
    }
}

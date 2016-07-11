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
package org.eclipse.kapua.service.account.internal.setting;

import org.eclipse.kapua.commons.setting.AbstractKapuaSetting;

public class KapuaAccountSetting extends AbstractKapuaSetting<KapuaAccountSettingKeys>
{
    private static final String              ACCOUNT_SETTING_RESOURCE = "kapua-account-setting.properties";

    private static final KapuaAccountSetting instance                 = new KapuaAccountSetting();

    private KapuaAccountSetting()
    {
        super(ACCOUNT_SETTING_RESOURCE);
    }

    public static KapuaAccountSetting getInstance()
    {
        return instance;
    }
}

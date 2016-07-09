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

import org.eclipse.kapua.commons.setting.AbstractKapuaSettings;

public class AccountSettings extends AbstractKapuaSettings<AccountSettingKey>
{
    
    private static final String ACCOUNT_CONFIG_RESOURCE = "kapua-account-config.properties";

    private static final AccountSettings instance = new AccountSettings();
    
    private AccountSettings()
    {
        super(ACCOUNT_CONFIG_RESOURCE);
    }

    public static AccountSettings getInstance()
    {
        return instance;
    }
}

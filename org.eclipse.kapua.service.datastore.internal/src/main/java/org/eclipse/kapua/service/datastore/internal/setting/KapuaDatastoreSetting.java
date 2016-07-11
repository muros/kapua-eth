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
package org.eclipse.kapua.service.datastore.internal.setting;

import org.eclipse.kapua.commons.setting.AbstractKapuaSetting;

public class KapuaDatastoreSetting extends AbstractKapuaSetting<KapuaDatastoreSettingKeys>
{
    private static final String                DATASTORE_SETTING_RESOURCE = "kapua-datastore-setting.properties";

    private static final KapuaDatastoreSetting instance                   = new KapuaDatastoreSetting();

    private KapuaDatastoreSetting()
    {
        super(DATASTORE_SETTING_RESOURCE);
    }

    public static KapuaDatastoreSetting getInstance()
    {
        return instance;
    }
}

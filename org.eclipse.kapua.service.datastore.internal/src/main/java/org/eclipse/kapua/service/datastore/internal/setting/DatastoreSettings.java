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

public class DatastoreSettings extends AbstractKapuaSetting<DatastoreSettingKey>
{
    private static final String    DATASTORE_CONFIG_RESOURCE = "kapua-datastore-setting.properties";

    private static final DatastoreSettings instance = new DatastoreSettings();

    private DatastoreSettings()
    {
        super(DATASTORE_CONFIG_RESOURCE);
    }

    public static DatastoreSettings getInstance()
    {
        return instance;
    }
}

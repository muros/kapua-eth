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
package org.eclipse.kapua.app.console.config;

import org.eclipse.kapua.commons.config.KapuaConfigKey;

public enum ConsoleConfigKeys implements KapuaConfigKey
{
    GOOGLE_ANALYTICS_TRACKING_ID("google.analytics.trackingid"),

    SKIN_RESOURCE_DIR("skin.resource.dir"),

    DEVICE_CONFIGURATION_ICON_FOLDER("device.configuration.icon.folder"),
    DEVICE_CONFIGURATION_ICON_CACHE_TIME("device.configuration.icon.cache.time"),
    DEVICE_CONFIGURATION_ICON_SIZE_MAX("device.configuration.icon.size.max"),

    DEVICE_CONFIGURATION_SERVICE_IGNORE("device.configuration.service.ignore"),

    FILE_UPLOAD_SIZE_MAX("file.upload.size.max"),
    FILE_UPLOAD_INMEMORY_SIZE_THRESHOLD("file.upload.inmemory.size.threshold"),;

    private String key;

    private ConsoleConfigKeys(String key)
    {
        this.key = key;
    }

    public String key()
    {
        return key;
    }
}

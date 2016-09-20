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
package org.eclipse.kapua.service.device.management.configuration.internal;

import org.eclipse.kapua.service.device.management.configuration.DeviceComponentConfiguration;
import org.eclipse.kapua.service.device.management.configuration.DeviceConfiguration;
import org.eclipse.kapua.service.device.management.configuration.DeviceConfigurationFactory;

public class DeviceConfigurationFactoryImpl implements DeviceConfigurationFactory
{

    @Override
    public DeviceComponentConfiguration newComponentConfigurationInstance(String componentConfigurationId)
    {
        return new DeviceComponentConfigurationImpl(componentConfigurationId);
    }

    @Override
    public DeviceConfiguration newConfigurationInstance()
    {
        return new DeviceConfigurationImpl();
    }

}

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
package org.eclipse.kapua.service.device.management.configuration.message.internal;

import org.eclipse.kapua.message.internal.KapuaMessageImpl;
import org.eclipse.kapua.service.device.management.request.KapuaRequestMessage;

public class ConfigurationRequestMessage extends KapuaMessageImpl<ConfigurationRequestChannel, ConfigurationRequestPayload>
                                         implements KapuaRequestMessage<ConfigurationRequestChannel, ConfigurationRequestPayload>
{
    @SuppressWarnings("unchecked")
    @Override
    public Class<ConfigurationRequestMessage> getRequestClass()
    {
        return ConfigurationRequestMessage.class;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Class<ConfigurationResponseMessage> getResponseClass()
    {
        return ConfigurationResponseMessage.class;
    }

}

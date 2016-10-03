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
package org.eclipse.kapua.service.device.management.deploy.message.internal;

import org.eclipse.kapua.message.internal.KapuaMessageImpl;
import org.eclipse.kapua.service.device.management.request.KapuaRequestMessage;

public class DeployRequestMessage extends KapuaMessageImpl<DeployRequestChannel, DeployRequestPayload>
                                         implements KapuaRequestMessage<DeployRequestChannel, DeployRequestPayload>
{
    @SuppressWarnings("unchecked")
    @Override
    public Class<DeployRequestMessage> getRequestClass()
    {
        return DeployRequestMessage.class;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Class<DeployResponseMessage> getResponseClass()
    {
        return DeployResponseMessage.class;
    }

}

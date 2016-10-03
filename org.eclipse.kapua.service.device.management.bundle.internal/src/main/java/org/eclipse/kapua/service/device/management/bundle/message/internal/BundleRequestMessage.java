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
package org.eclipse.kapua.service.device.management.bundle.message.internal;

import org.eclipse.kapua.message.internal.KapuaMessageImpl;
import org.eclipse.kapua.service.device.management.request.KapuaRequestMessage;

public class BundleRequestMessage extends KapuaMessageImpl<BundleRequestChannel, BundleRequestPayload>
                                         implements KapuaRequestMessage<BundleRequestChannel, BundleRequestPayload>
{
    @SuppressWarnings("unchecked")
    @Override
    public Class<BundleRequestMessage> getRequestClass()
    {
        return BundleRequestMessage.class;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Class<BundleResponseMessage> getResponseClass()
    {
        return BundleResponseMessage.class;
    }

}

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
package org.eclipse.kapua.transport;

import org.eclipse.kapua.model.KapuaObjectFactory;
import org.eclipse.kapua.transport.message.TransportChannel;
import org.eclipse.kapua.transport.message.TransportMessage;
import org.eclipse.kapua.transport.message.TransportPayload;

public interface TransportClientFactory<D extends TransportChannel, P extends TransportPayload, MQ extends TransportMessage<D, P>, MS extends TransportMessage<D, P>, C extends TransportFacade<D, P, MQ, MS>, CO extends TransportClientConnectOptions>
                                       extends KapuaObjectFactory
{
    public C getFacade()
        throws Exception;

    public CO newConnectOptions();
}

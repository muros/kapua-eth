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
package org.eclipse.kapua.transport.message.mqtt;

import org.eclipse.kapua.transport.message.TransportPayload;

public class MqttPayload implements TransportPayload
{
    private byte[] body;

    public MqttPayload(byte[] body)
    {
        this.body = body;
    }

    public byte[] getBody()
    {
        return body;
    }

    public void setBody(byte[] body)
    {
        this.body = body;
    }
}

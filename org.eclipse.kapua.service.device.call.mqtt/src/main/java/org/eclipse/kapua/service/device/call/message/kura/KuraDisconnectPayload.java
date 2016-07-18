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
package org.eclipse.kapua.service.device.call.message.kura;

import java.io.IOException;
import java.util.Iterator;

import org.eclipse.kapua.message.internal.MessageException;
import org.eclipse.kapua.service.device.call.message.DevicePayload;

public class KuraDisconnectPayload extends KuraPayload implements DevicePayload
{
    private final static String UPTIME       = "uptime";
    private final static String DISPLAY_NAME = "display_name";

    public KuraDisconnectPayload(String uptime, String displayName)
    {
        super();

        metrics().put(UPTIME, uptime);
        metrics().put(DISPLAY_NAME, displayName);
    }

    public <P extends KuraPayload> KuraDisconnectPayload(P kuraPayload) throws MessageException, IOException
    {
        Iterator<String> hdrIterator = metrics().keySet().iterator();

        while (hdrIterator.hasNext()) {
            String hdrName = hdrIterator.next();
            String hdrVal = (String) metrics().get(hdrName);

            metrics().put(hdrName, hdrVal);
        }

        setBody(kuraPayload.getBody());
    }

    public String getUptime()
    {
        return (String) metrics().get(UPTIME);
    }

    public String getDisplayName()
    {
        return (String) metrics().get(DISPLAY_NAME);
    }

    public String toDisplayString()
    {
        return new StringBuilder().append("[ getUptime()=").append(getUptime())
                                  .append(", getDisplayName()=").append(getDisplayName())
                                  .append("]")
                                  .toString();
    }
}

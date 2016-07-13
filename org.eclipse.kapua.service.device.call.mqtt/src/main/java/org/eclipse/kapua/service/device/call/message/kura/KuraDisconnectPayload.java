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

public class KuraDisconnectPayload extends AbstractKuraPayload
{
    private final static String UPTIME       = "uptime";
    private final static String DISPLAY_NAME = "display_name";

    public KuraDisconnectPayload(String uptime, String displayName)
    {
        super();

        addMetric(UPTIME, uptime);
        addMetric(DISPLAY_NAME, displayName);
    }

    public <P extends AbstractKuraPayload> KuraDisconnectPayload(P kapuaPayload) throws MessageException, IOException
    {
        Iterator<String> hdrIterator = kapuaPayload.metricsIterator();

        while (hdrIterator.hasNext()) {
            String hdrName = hdrIterator.next();
            String hdrVal = (String) kapuaPayload.getMetric(hdrName);

            addMetric(hdrName, hdrVal);
        }

        setBody(kapuaPayload.getBody());
    }

    public String getUptime()
    {
        return (String) getMetric(UPTIME);
    }

    public String getDisplayName()
    {
        return (String) getMetric(DISPLAY_NAME);
    }

    @Override
    public String toString()
    {
        return new StringBuilder().append("[ getUptime()=").append(getUptime())
                                  .append(", getDisplayName()=").append(getDisplayName())
                                  .append("]")
                                  .toString();
    }
}

/*******************************************************************************
 * Copyright (c) 2011, 2016 Eurotech and/or its affiliates and others
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *      Eurotech - initial API and implementation
 *******************************************************************************/
package org.eclipse.kapua.service.device.call.kura.app;

public enum RequestMetrics
{
    REQ_METRIC_REQUEST_ID("request.id"),
    REQ_METRIC_REQUESTER_CLIENT_ID("requester.client.id"),
    ;

    private String value;

    RequestMetrics(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }
}

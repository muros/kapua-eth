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
package org.eclipse.kapua.service.device.management.commons.message.response;

import org.eclipse.kapua.message.internal.KapuaPayloadImpl;
import org.eclipse.kapua.service.device.management.ResponseProperties;
import org.eclipse.kapua.service.device.management.response.KapuaResponsePayload;

public class KapuaResponsePayloadImpl extends KapuaPayloadImpl implements KapuaResponsePayload
{

    @Override
    public String getExceptionMessage()
    {
        return (String) getProperties().get(ResponseProperties.RESP_PROPERTY_EXCEPTION_MESSAGE.getValue());
    }

    @Override
    public void setExceptionMessage(String setExecptionMessage)
    {
        getProperties().put(ResponseProperties.RESP_PROPERTY_EXCEPTION_MESSAGE.getValue(), setExecptionMessage);
    }

    @Override
    public String getExceptionStack()
    {
        return (String) getProperties().get(ResponseProperties.RESP_PROPERTY_EXCEPTION_STACK.getValue());
    }

    @Override
    public void setExceptionStack(String setExecptionStack)
    {
        getProperties().put(ResponseProperties.RESP_PROPERTY_EXCEPTION_STACK.getValue(), setExecptionStack);
    }

}

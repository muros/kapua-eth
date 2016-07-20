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
package org.eclipse.kapua.message.internal;

import org.eclipse.kapua.message.KapuaChannel;

public class KapuaChannelImpl implements KapuaChannel
{
    private String   clientId;
    private String[] semanticParts;

    @Override
    public String getClientId()
    {
        return clientId;
    }

    @Override
    public void setClientId(String clientId)
    {
        this.clientId = clientId;
    }

    @Override
    public String[] getSemanticParts()
    {
        return semanticParts;
    }

    @Override
    public void setSemanticParts(String[] semanticParts)
    {
        this.semanticParts = semanticParts;
    }

}

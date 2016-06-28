/*******************************************************************************
 * Copyright (c) 2011, 2016 Eurotech and/or its affiliates
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *      Eurotech - initial API and implementation
 *******************************************************************************/
package org.eclipse.kapua.service.account;

import org.eclipse.kapua.model.KapuaNamedEntity;

public interface Account extends KapuaNamedEntity
{
    public static final String TYPE = "acct";

    default public String getType()
    {
        return TYPE;
    }

    public Organization getOrganization();

    public void setOrganization(Organization organization);

    public String getParentAccountPath();

    public void setParentAccountPath(String parentAccountPath);
}

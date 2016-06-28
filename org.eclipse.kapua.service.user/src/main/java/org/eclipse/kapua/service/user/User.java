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
package org.eclipse.kapua.service.user;

import org.eclipse.kapua.model.KapuaNamedEntity;

public interface User extends KapuaNamedEntity
{
    public static final String TYPE = "user";

    default public String getType()
    {
        return TYPE;
    }

    public UserStatus getStatus();

    public void setStatus(UserStatus status);

    public String getDisplayName();

    public void setDisplayName(String displayName);

    public String getEmail();

    public void setEmail(String email);

    public String getPhoneNumber();

    public void setPhoneNumber(String phoneNumber);

    public String getPassword();

    public String getSalt();

    public String getRawPassword();

    public void setRawPassword(String rawPassword);
}

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
package org.eclipse.kapua.service.authentication.shiro.credential;

import org.apache.shiro.authc.SimpleAuthenticationInfo;

public class KapuaAuthenticationInfo extends SimpleAuthenticationInfo
{
    private static final long serialVersionUID = -3115312598553417080L;

    private String            username;
    private char[]            password;

    public KapuaAuthenticationInfo(String username, char[] password)
    {
        this.username = username;
        this.password = password;
    }

    public String getUserId()
    {
        return username;
    }

    public void setUserId(String userId)
    {
        this.username = userId;
    }

    public char[] getPassword()
    {
        return password;
    }

    public void setPassword(char[] password)
    {
        this.password = password;
    }

}

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
package org.eclipse.kapua.service.authorization.permission.shiro;

import javax.xml.bind.annotation.XmlElement;

import org.eclipse.kapua.commons.model.AbstractKapuaEntityCreator;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.authorization.Actions;
import org.eclipse.kapua.service.authorization.permission.UserPermission;
import org.eclipse.kapua.service.authorization.permission.UserPermissionCreator;

public class UserPermissionCreatorImpl extends AbstractKapuaEntityCreator<UserPermission> implements UserPermissionCreator
{
    private static final long serialVersionUID = 972154225756734130L;

    @XmlElement(name = "userId")
    private KapuaId           userId;

    @XmlElement(name = "domain")
    private String            domain;

    @XmlElement(name = "action")
    private Actions           action;

    @XmlElement(name = "targetScopeId")
    private KapuaId           targetScopeId;

    public UserPermissionCreatorImpl(KapuaId scopeId)
    {
        super(scopeId);
    }

    @Override
    public void setUserId(KapuaId userId)
    {
        this.userId = userId;
    }

    @Override
    public KapuaId getUserId()
    {
        return userId;
    }

    @Override
    public void setDomain(String domain)
    {
        this.domain = domain;
    }

    @Override
    public String getDomain()
    {
        return domain;
    }

    @Override
    public void setAction(Actions action)
    {
        this.action = action;
    }

    @Override
    public Actions getAction()
    {
        return action;
    }

    @Override
    public void setTargetScopeId(KapuaId targetScopeId)
    {
        this.targetScopeId = targetScopeId;

    }

    @Override
    public KapuaId getTargetScopeId()
    {
        return targetScopeId;
    }
}

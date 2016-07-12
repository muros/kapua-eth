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
package org.eclipse.kapua.service.authorization.shiro;

import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.authorization.Action;
import org.eclipse.kapua.service.authorization.Domain;
import org.eclipse.kapua.service.authorization.Permission;

public class PermissionImpl implements Permission
{
    private Domain  domain;
    private Action  action;
    private KapuaId targetScopeId;

    private PermissionImpl()
    {
    }

    public PermissionImpl(Domain domain, Action action, KapuaId targetScopeId)
    {
        this();
        this.domain = domain;
        this.action = action;
        this.targetScopeId = targetScopeId;
    }

    @Override
    public void setDomain(Domain domain)
    {
        this.domain = domain;
    }

    @Override
    public Domain getDomain()
    {
        return domain;
    }

    @Override
    public void setAction(Action action)
    {
        this.action = action;
    }

    @Override
    public Action getAction()
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

    public String toString()
    {
        StringBuilder sb = new StringBuilder(domain.name());

        if (action != null) {
            sb.append(":")
              .append(action.name());
        }
        if (targetScopeId != null) {
            sb.append(":")
              .append(targetScopeId.getId());
        }
        return sb.toString();
    }
}

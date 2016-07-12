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

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlElement;

import org.eclipse.kapua.commons.model.AbstractKapuaEntity;
import org.eclipse.kapua.commons.model.id.KapuaEid;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.authorization.Action;
import org.eclipse.kapua.service.authorization.Domain;
import org.eclipse.kapua.service.authorization.permission.UserPermission;
import org.hibernate.annotations.DynamicUpdate;

@Entity(name = "Permission")
@Table(name = "athz_permission")
@DynamicUpdate
public class UserPermissionImpl extends AbstractKapuaEntity implements UserPermission
{
    private static final long serialVersionUID = -3760818776351242930L;

    @XmlElement(name = "userId")
    @Embedded
    @AttributeOverrides({
                          @AttributeOverride(name = "eid", column = @Column(name = "user_id", nullable = false, updatable = false))
    })
    private KapuaEid          userId;

    @XmlElement(name = "domain")
    @Enumerated(EnumType.STRING)
    @Column(name = "domain", nullable = false, updatable = false)
    private Domain            domain;

    @XmlElement(name = "action")
    @Enumerated(EnumType.STRING)
    @Column(name = "action", nullable = false, updatable = false)
    private Action            action;

    @XmlElement(name = "targetScopeId")
    @Embedded
    @AttributeOverrides({
                          @AttributeOverride(name = "eid", column = @Column(name = "target_scope_id", updatable = false))
    })
    private KapuaEid          targetScopeId;

    public UserPermissionImpl()
    {
        super();
    }

    public UserPermissionImpl(KapuaId accountId)
    {
        super(accountId);
    }

    @Override
    public void setUserId(KapuaId userId)
    {
        this.userId = (KapuaEid) userId;
    }

    @Override
    public KapuaId getUserId()
    {
        return userId;
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
        this.targetScopeId = (KapuaEid) targetScopeId;
    }

    @Override
    public KapuaId getTargetScopeId()
    {
        return targetScopeId;
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(domain);
        if (action != null) {
            sb.append(":")
              .append(action);
        }
        if (targetScopeId != null) {
            sb.append(":")
              .append(targetScopeId.getId());
        }
        return sb.toString();
    }
}

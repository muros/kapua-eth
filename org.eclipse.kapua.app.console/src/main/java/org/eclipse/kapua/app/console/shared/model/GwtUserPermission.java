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
package org.eclipse.kapua.app.console.shared.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class GwtUserPermission extends KapuaBaseModel implements Serializable
{

    private static final long serialVersionUID = 8760151213714238533L;

    /**
     * Defines the domain of the object protected by the Permission
     */
    public enum Domain
    {
        account,
        user,
        data,
        device,
        broker,
    }

    /**
     * Defines the actions allowed by the Permission
     */
    public enum Action
    {
        create,
        read,
        update,
        delete,
        connect,
        all;
    }

    private Domain m_domain;
    private Action m_action;
    private String m_scopeId;

    public GwtUserPermission(Domain domain, Action action, String accountId, boolean enabled)
    {

        m_domain = domain;
        m_action = action;
        m_scopeId = accountId;

        StringBuilder sbAccountPermission = new StringBuilder();
        sbAccountPermission.append(m_domain.name());
        if (m_action != null) {
            sbAccountPermission.append(":")
                               .append(m_action.name());
        }

        setEnabled(enabled);
        setPermission(toString());
        setAccountPermission(sbAccountPermission.toString());
    }

    public GwtUserPermission(Domain domain, Action action, boolean enabled)
    {
        this(domain, action, null, enabled);
    }

    public GwtUserPermission(Domain domain, boolean enabled)
    {
        this(domain, null, null, enabled);
    }

    public boolean getEnabled()
    {
        return (Boolean) get("enabled");
    }

    public void setEnabled(boolean enabled)
    {
        set("enabled", enabled);
    }

    public void setPermission(String permission)
    {
        set("permission", permission);
    }

    public String getPermission()
    {
        return get("permission");
    }

    public void setAccountPermission(String permission)
    {
        set("accountPermission", permission);
    }

    public String getAccountPermission()
    {
        return get("accountPermission");
    }

    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(m_domain.name());
        if (m_action != null) {
            sb.append(":")
              .append(m_action.name());
        }
        if (m_scopeId != null) {
            sb.append(":")
              .append(String.valueOf(m_scopeId));
        }
        return sb.toString();
    }

    public static List<GwtUserPermission> getAllPermissions(String scopeId)
    {
        List<GwtUserPermission> permissions = new ArrayList<GwtUserPermission>();
        permissions.add(new GwtUserPermission(Domain.account, Action.create, scopeId, false));
        permissions.add(new GwtUserPermission(Domain.account, Action.read, scopeId, false));
        permissions.add(new GwtUserPermission(Domain.account, Action.update, scopeId, false));
        permissions.add(new GwtUserPermission(Domain.account, Action.delete, scopeId, false));
        permissions.add(new GwtUserPermission(Domain.account, Action.all, scopeId, false));

        permissions.add(new GwtUserPermission(Domain.broker, Action.connect, scopeId, false));

        permissions.add(new GwtUserPermission(Domain.data, Action.read, scopeId, false));

        permissions.add(new GwtUserPermission(Domain.device, Action.create, scopeId, false));
        permissions.add(new GwtUserPermission(Domain.device, Action.read, scopeId, false));
        permissions.add(new GwtUserPermission(Domain.device, Action.update, scopeId, false));
        permissions.add(new GwtUserPermission(Domain.device, Action.delete, scopeId, false));

        permissions.add(new GwtUserPermission(Domain.user, Action.create, scopeId, false));
        permissions.add(new GwtUserPermission(Domain.user, Action.read, scopeId, false));
        permissions.add(new GwtUserPermission(Domain.user, Action.update, scopeId, false));
        permissions.add(new GwtUserPermission(Domain.user, Action.delete, scopeId, false));

        return permissions;
    }
}

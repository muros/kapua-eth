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
package org.eclipse.kapua.service.authorization.user.role.shiro;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.eclipse.kapua.commons.model.AbstractKapuaEntity;
import org.eclipse.kapua.commons.model.id.KapuaEid;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.authorization.user.role.UserRole;

@Entity(name = "UserRole")
@Table(name = "athz_user_role")
public class UserRoleImpl extends AbstractKapuaEntity implements UserRole
{
    private static final long serialVersionUID = -3760818776351242930L;

    @Embedded
    @AttributeOverrides({
                          @AttributeOverride(name = "eid", column = @Column(name = "user_id"))
    })
    private KapuaEid          userId;

    // @OneToMany(fetch = FetchType.EAGER)
    @ElementCollection()
    @CollectionTable(name = "athz_user_role")
    // @AssociationOverrides({
    // @AssociationOverride(name = "eid", joinColumns = @JoinColumn(name = "role_id"))
    // })
    private Set<RoleId>       rolesIds;

    protected UserRoleImpl()
    {
        super();
    }

    public UserRoleImpl(KapuaId scopeId)
    {
        super(scopeId);
    }

    public void setUserId(KapuaId userId)
    {
        if (userId.getId() != null) {
            this.userId = new KapuaEid(userId.getId());
        }
    }

    public KapuaId getUserId()
    {
        return userId;
    }

    @Override
    public void setRolesIds(Set<KapuaId> rolesIds)
    {
        Set<RoleId> rolesTmp = new HashSet<>();

        for (KapuaId id : rolesIds) {
            rolesTmp.add(new RoleId(id.getId()));
        }
        this.rolesIds = rolesTmp;
    }

    @Override
    public Set<KapuaId> getRolesIds()
    {
        Set<KapuaId> rolesTmp = new HashSet<>();

        for (KapuaId r : rolesIds) {
            rolesTmp.add(r);
        }

        return rolesTmp;
    }

    // public void setRoles(Set<Role> roles)
    // {
    // Set<RoleImpl> rolesTmp = new HashSet<>();
    //
    // for (Role r : roles) {
    // RoleImpl role = new RoleImpl(r.getScopeId());
    // role.setName(r.getName());
    // role.setPermissions(r.getPermissions());
    //
    // rolesTmp.add(role);
    // }
    //
    // this.roles = rolesTmp;
    // }
    //
    // @Override
    // public Set<Role> getRoles()
    // {
    // Set<Role> rolesTmp = new HashSet<>();
    //
    // for (Role r : roles) {
    // rolesTmp.add(r);
    // }
    //
    // return rolesTmp;
    // }

}

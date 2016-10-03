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
package org.eclipse.kapua.service.user.internal;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import org.eclipse.kapua.commons.model.AbstractKapuaNamedEntity;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.user.User;
import org.eclipse.kapua.service.user.UserStatus;

@Entity(name = "User")
@Table(name = "usr_user")
public class UserImpl extends AbstractKapuaNamedEntity implements User
{
    private static final long serialVersionUID = 4029650117581681503L;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private UserStatus        status;

    @Basic
    @Column(name = "display_name")
    private String            displayName;

    @Basic
    @Column(name = "email")
    private String            email;

    @Basic
    @Column(name = "phone_number")
    private String            phoneNumber;

    public UserImpl()
    {
        super();
    }

    public UserImpl(KapuaId scopeId,
                    String name)
    {
        super(scopeId, name);
        this.status = UserStatus.ENABLED;
    }

    public UserStatus getStatus()
    {
        return status;
    }

    public void setStatus(UserStatus status)
    {
        this.status = status;
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public void setDisplayName(String displayName)
    {
        this.displayName = displayName;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public String getPhoneNumber()
    {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber)
    {
        this.phoneNumber = phoneNumber;
    }
}

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
package org.eclipse.kapua.service.user.internal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.eclipse.kapua.commons.model.AbstractKapuaNamedEntityCreator;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.user.User;
import org.eclipse.kapua.service.user.UserCreator;

/**
 * UserCreator encapsulates all the information needed to create a new User in the system.
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "accountId", "name", "rawPassword", "displayName", "email", "phoneNumber" })
public class UserCreatorImpl extends AbstractKapuaNamedEntityCreator<User> implements UserCreator
{
    private static final long serialVersionUID = 4664940282892151008L;

    @XmlElement(name = "rawPassword")
    private String            rawPassword;

    @XmlElement(name = "displayName")
    private String            displayName;

    @XmlElement(name = "email")
    private String            email;

    @XmlElement(name = "phoneNumber")
    private String            phoneNumber;

    public UserCreatorImpl(KapuaId accountId, String name)
    {
        super(accountId, name);
    }

    public String getRawPassword()
    {
        return this.rawPassword;
    }

    public void setRawPassword(String rawPassword)
    {
        this.rawPassword = rawPassword;
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

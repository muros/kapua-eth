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
package org.eclipse.kapua.service.user;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.eclipse.kapua.model.KapuaNamedEntityCreator;

/**
 * UserCreator encapsulates all the information needed to create a new User in the system.
 */

@XmlRootElement(name="userCreator")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "displayName",
                       "email",
                       "phoneNumber" },
         factoryClass = UserXmlRegistry.class,
         factoryMethod = "newUserCreator")
public interface UserCreator extends KapuaNamedEntityCreator<User>
{
    @XmlElement(name = "displayName")
    public String getDisplayName();

    public void setDisplayName(String displayName);

    @XmlElement(name = "email")
    public String getEmail();

    public void setEmail(String email);

    @XmlElement(name = "phoneNumber")
    public String getPhoneNumber();

    public void setPhoneNumber(String phoneNumber);
}

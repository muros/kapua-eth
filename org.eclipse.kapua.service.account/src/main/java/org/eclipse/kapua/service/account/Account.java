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
package org.eclipse.kapua.service.account;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.eclipse.kapua.model.KapuaNamedEntity;

@XmlRootElement(name = "account")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(propOrder = { "organization", 
					  "parentAccountPath" },
		factoryClass = AccountXmlRegistry.class, 
		factoryMethod = "newAccount")
public interface Account extends KapuaNamedEntity
{
    public static final String TYPE = "acct";

    default public String getType()
    {
        return TYPE;
    }

    @XmlElement(name = "organization")
    public Organization getOrganization();

    public void setOrganization(Organization organization);

    @XmlElement(name = "parentAccountPath")
    public String getParentAccountPath();

    public void setParentAccountPath(String parentAccountPath);
}

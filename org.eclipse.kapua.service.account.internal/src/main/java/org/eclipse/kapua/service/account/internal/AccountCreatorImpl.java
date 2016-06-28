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
package org.eclipse.kapua.service.account.internal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.eclipse.kapua.commons.model.AbstractKapuaNamedEntityCreator;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.account.Account;
import org.eclipse.kapua.service.account.AccountCreator;

/**
 * AccountCreator encapsulates all the information needed to create a new Account in the system.
 * The data provided will be used to seed the new Account and its related information such as the associated organization and users.
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "accountName",
                      "accountPassword",
                      "organizationName",
                      "organizationPersonName",
                      "organizationEmail",
                      "organizationPhoneNumber",
                      "organizationAddressLine1",
                      "organizationAddressLine2",
                      "organizationCity",
                      "organizationZipPostCode",
                      "organizationStateProvinceCounty",
                      "organizationCountry"
					})

public class AccountCreatorImpl extends AbstractKapuaNamedEntityCreator<Account> implements AccountCreator
{
	private static final long serialVersionUID = -2460883485294616032L;

	@XmlElement(name = "accountName")
	private String accountName;

	@XmlElement(name = "accountPassword")
	private String accountPassword;

	@XmlElement(name = "organizationName")
	private String organizationName;

	@XmlElement(name = "organizationPersonName")
	private String organizationPersonName;
	
	@XmlElement(name = "organizationEmail")
	private String organizationEmail;

	@XmlElement(name = "organizationPhoneNumber")
	private String organizationPhoneNumber;

	@XmlElement(name = "organizationAddressLine1")
	private String organizationAddressLine1;

	@XmlElement(name = "organizationAddressLine2")
	private String organizationAddressLine2;

	@XmlElement(name = "organizationCity")
	private String organizationCity;

	@XmlElement(name = "organizationZipPostCode")
	private String organizationZipPostCode;

	@XmlElement(name = "organizationStateProvinceCounty")
	private String organizationStateProvinceCounty;

	@XmlElement(name = "organizationCountry")
	private String organizationCountry;

    public AccountCreatorImpl(KapuaId scopeId, String name)
    {
    	super(scopeId, name);
    }

    public String getAccountName()
    {
        return accountName;
    }

    public void setAccountName(String accountName)
    {
        this.accountName = accountName;
    }

    public String getAccountPassword()
    {
        return accountPassword;
    }

    public void setAccountPassword(String accountPassword)
    {
        this.accountPassword = accountPassword;
    }

    public String getOrganizationName()
    {
        return organizationName;
    }

    public void setOrganizationName(String organizationName)
    {
        this.organizationName = organizationName;
    }

    public String getOrganizationPersonName()
    {
        return organizationPersonName;
    }

    public void setOrganizationPersonName(String organizationPersonName)
    {
        this.organizationPersonName = organizationPersonName;
    }

    public String getOrganizationEmail()
    {
        return organizationEmail;
    }

    public void setOrganizationEmail(String organizationEmail)
    {
        this.organizationEmail = organizationEmail;
    }

    public String getOrganizationPhoneNumber()
    {
        return organizationPhoneNumber;
    }

    public void setOrganizationPhoneNumber(String organizationPhoneNumber)
    {
        this.organizationPhoneNumber = organizationPhoneNumber;
    }

    public String getOrganizationAddressLine1()
    {
        return organizationAddressLine1;
    }

    public void setOrganizationAddressLine1(String organizationAddressLine1)
    {
        this.organizationAddressLine1 = organizationAddressLine1;
    }

    public String getOrganizationAddressLine2()
    {
        return organizationAddressLine2;
    }

    public void setOrganizationAddressLine2(String organizationAddressLine2)
    {
        this.organizationAddressLine2 = organizationAddressLine2;
    }

    public String getOrganizationCity()
    {
        return organizationCity;
    }

    public void setOrganizationCity(String organizationCity)
    {
        this.organizationCity = organizationCity;
    }

    public String getOrganizationZipPostCode()
    {
        return organizationZipPostCode;
    }

    public void setOrganizationZipPostCode(String organizationZipPostCode)
    {
        this.organizationZipPostCode = organizationZipPostCode;
    }

    public String getOrganizationStateProvinceCounty()
    {
        return organizationStateProvinceCounty;
    }

    public void setOrganizationStateProvinceCounty(String organizationStateProvinceCounty)
    {
        this.organizationStateProvinceCounty = organizationStateProvinceCounty;
    }

    public String getOrganizationCountry()
    {
        return organizationCountry;
    }

    public void setOrganizationCountry(String organizationCountry)
    {
        this.organizationCountry = organizationCountry;
    }
}
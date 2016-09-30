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
package org.eclipse.kapua.service.account;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.eclipse.kapua.model.KapuaNamedEntityCreator;

@XmlRootElement(name="accountCreator")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(propOrder = { "accountPassword",
                      "organizationName",
                      "organizationPersonName",
                      "organizationEmail",
                      "organizationPhoneNumber",
                      "organizationAddressLine1",
                      "organizationAddressLine2",
                      "organizationCity",
                      "organizationZipPostCode",
                      "organizationStateProvinceCounty",
                      "organizationCountry"},
		 factoryClass = AccountXmlRegistry.class,
		 factoryMethod = "newAccountCreator")
public interface AccountCreator extends KapuaNamedEntityCreator<Account>
{
	@XmlElement(name = "accountPassword")
    public String getAccountPassword();

    public void setAccountPassword(String accountPassword);

	@XmlElement(name = "organizationName")
    public String getOrganizationName();

    public void setOrganizationName(String organizationName);

	@XmlElement(name = "organizationPersonName")
    public String getOrganizationPersonName();

    public void setOrganizationPersonName(String organizationPersonName);

	@XmlElement(name = "organizationEmail")
    public String getOrganizationEmail();

    public void setOrganizationEmail(String organizationEmail);

	@XmlElement(name = "organizationPhoneNumber")
    public String getOrganizationPhoneNumber();

    public void setOrganizationPhoneNumber(String organizationPhoneNumber);

	@XmlElement(name = "organizationAddressLine1")
    public String getOrganizationAddressLine1();

    public void setOrganizationAddressLine1(String organizationAddressLine1);

	@XmlElement(name = "organizationAddressLine2")
    public String getOrganizationAddressLine2();

    public void setOrganizationAddressLine2(String organizationAddressLine2);

	@XmlElement(name = "organizationCity")
    public String getOrganizationCity();

    public void setOrganizationCity(String organizationCity);

	@XmlElement(name = "organizationZipPostCode")
    public String getOrganizationZipPostCode();

    public void setOrganizationZipPostCode(String organizationZipPostCode);

	@XmlElement(name = "organizationStateProvinceCounty")
    public String getOrganizationStateProvinceCounty();

    public void setOrganizationStateProvinceCounty(String organizationStateProvinceCounty);

	@XmlElement(name = "organizationCountry")
    public String getOrganizationCountry();

    public void setOrganizationCountry(String organizationCountry);
}

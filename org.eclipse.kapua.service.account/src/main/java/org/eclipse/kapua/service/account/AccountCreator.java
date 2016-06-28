/*******************************************************************************
 * Copyright (c) 2011, 2016 Eurotech and/or its affiliates
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *      Eurotech - initial API and implementation
 *******************************************************************************/
package org.eclipse.kapua.service.account;

import org.eclipse.kapua.model.KapuaNamedEntityCreator;

public interface AccountCreator extends KapuaNamedEntityCreator<Account>
{
    public String getAccountPassword();

    public void setAccountPassword(String accountPassword);

    public String getOrganizationName();

    public void setOrganizationName(String organizationName);

    public String getOrganizationPersonName();

    public void setOrganizationPersonName(String organizationPersonName);

    public String getOrganizationEmail();

    public void setOrganizationEmail(String organizationEmail);

    public String getOrganizationPhoneNumber();

    public void setOrganizationPhoneNumber(String organizationPhoneNumber);

    public String getOrganizationAddressLine1();

    public void setOrganizationAddressLine1(String organizationAddressLine1);

    public String getOrganizationAddressLine2();

    public void setOrganizationAddressLine2(String organizationAddressLine2);

    public String getOrganizationCity();

    public void setOrganizationCity(String organizationCity);

    public String getOrganizationZipPostCode();

    public void setOrganizationZipPostCode(String organizationZipPostCode);

    public String getOrganizationStateProvinceCounty();

    public void setOrganizationStateProvinceCounty(String organizationStateProvinceCounty);

    public String getOrganizationCountry();

    public void setOrganizationCountry(String organizationCountry);
}
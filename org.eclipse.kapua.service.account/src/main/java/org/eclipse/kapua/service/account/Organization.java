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
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(factoryClass=AccountXmlRegistry.class, factoryMethod="newOrganization")
public interface Organization
{

    public String getName();

    public void setName(String name);

    public String getPersonName();

    public void setPersonName(String name);

    public String getEmail();

    public void setEmail(String email);

    public String getPhoneNumber();

    public void setPhoneNumber(String phoneNumber);

    public String getAddressLine1();

    public void setAddressLine1(String addressLine1);

    public String getAddressLine2();

    public void setAddressLine2(String addressLine2);

    public String getZipPostCode();

    public void setZipPostCode(String zipPostalCode);

    public String getCity();

    public void setCity(String city);

    public String getStateProvinceCounty();

    public void setStateProvinceCounty(String stateProvinceCounty);

    public String getCountry();

    public void setCountry(String country);
}

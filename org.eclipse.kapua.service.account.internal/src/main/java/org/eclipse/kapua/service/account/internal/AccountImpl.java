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
package org.eclipse.kapua.service.account.internal;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.eclipse.kapua.commons.model.AbstractKapuaNamedEntity;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.account.Account;
import org.eclipse.kapua.service.account.Organization;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "scopeId", "id", "name", "createdOn", "createdBy", "modifiedOn", "modifiedBy", "organization", "parentAccountPath", "optlock" })
@Entity(name = "Account")
@NamedQueries({
                @NamedQuery(name = "Account.findChildAccounts", query = "SELECT a FROM Account a WHERE a.scopeId = :scopeId ORDER BY a.name")
})
@Table(name = "act_account")
public class AccountImpl extends AbstractKapuaNamedEntity implements Account
{
    private static final long serialVersionUID = 8530992430658117928L;

    @XmlElement(name = "organization")
    @Embedded
    @AttributeOverrides({
                          @AttributeOverride(name = "name", column = @Column(name = "org_name")),
                          @AttributeOverride(name = "personName", column = @Column(name = "org_person_name")),
                          @AttributeOverride(name = "email", column = @Column(name = "org_email")),
                          @AttributeOverride(name = "phoneNumber", column = @Column(name = "org_phone_number")),
                          @AttributeOverride(name = "addressLine1", column = @Column(name = "org_address_line_1")),
                          @AttributeOverride(name = "addressLine2", column = @Column(name = "org_address_line_2")),
                          @AttributeOverride(name = "addressLine3", column = @Column(name = "org_address_line_3")),
                          @AttributeOverride(name = "addressLine3", column = @Column(name = "org_address_line_3")),
                          @AttributeOverride(name = "zipPostCode", column = @Column(name = "org_zip_postcode")),
                          @AttributeOverride(name = "city", column = @Column(name = "org_city")),
                          @AttributeOverride(name = "stateProvinceCounty", column = @Column(name = "org_state_province_county")),
                          @AttributeOverride(name = "country", column = @Column(name = "org_country")),
    })
    private OrganizationImpl  organization;

    @XmlElement(name = "parentAccountPath")
    @Basic
    @Column(name = "parent_account_path", nullable = false)
    private String            parentAccountPath;

    public AccountImpl()
    {
    }

    public AccountImpl(KapuaId scopeId, String name)
    {
        super(scopeId, name);
        this.parentAccountPath = "";
    }

    public Organization getOrganization()
    {
        return organization;
    }

    public void setOrganization(Organization organization)
    {
        this.organization = (OrganizationImpl) organization;
    }

    public String getParentAccountPath()
    {
        return parentAccountPath;
    }

    public void setParentAccountPath(String parentAccountPath)
    {
        this.parentAccountPath = parentAccountPath;
    }
}

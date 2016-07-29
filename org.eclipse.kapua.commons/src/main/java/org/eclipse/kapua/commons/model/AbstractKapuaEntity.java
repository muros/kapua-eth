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
package org.eclipse.kapua.commons.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.EmbeddedId;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.model.id.KapuaEid;
import org.eclipse.kapua.commons.security.KapuaSecurityUtils;
import org.eclipse.kapua.commons.util.KapuaEidGenerator;
import org.eclipse.kapua.model.KapuaEntity;
import org.eclipse.kapua.model.id.KapuaId;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@SuppressWarnings("serial")
@MappedSuperclass
@Access(AccessType.FIELD)
@XmlAccessorType(XmlAccessType.FIELD)
@DynamicInsert
@DynamicUpdate
public abstract class AbstractKapuaEntity implements KapuaEntity, Serializable
{
    @XmlElement(name = "id")
    @EmbeddedId
    @AttributeOverrides({
                          @AttributeOverride(name = "eid", column = @Column(name = "id", nullable = false, updatable = false))
    })
    protected KapuaEid id;

    @XmlElement(name = "scopeId")
    @Embedded
    @AttributeOverrides({
                          @AttributeOverride(name = "eid", column = @Column(name = "scope_id", nullable = false, updatable = false))
    })
    protected KapuaEid scopeId;

    @XmlElement(name = "createdOn")
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_on", nullable = false)
    protected Date     createdOn;

    @XmlElement(name = "createdBy")
    @Embedded
    @AttributeOverrides({
                          @AttributeOverride(name = "eid", column = @Column(name = "created_by", nullable = false, updatable = false))
    })
    protected KapuaEid createdBy;

    protected AbstractKapuaEntity()
    {
    }

    public AbstractKapuaEntity(KapuaId scopeId)
    {
        this();
        if (scopeId != null) {
            this.scopeId = new KapuaEid(scopeId.getId());
        }
    }

    public KapuaId getScopeId()
    {
        return scopeId;
    }

    public KapuaId getId()
    {
        return id;
    }

    public Date getCreatedOn()
    {
        return createdOn;
    }

    public KapuaId getCreatedBy()
    {
        return createdBy;
    }

    @PrePersist
    protected void prePersistsAction()
        throws KapuaException
    {
        this.id = KapuaEidGenerator.generate();
        this.createdBy = (KapuaEid) KapuaSecurityUtils.getSession().getUserId();
        this.createdOn = new Date();
    }
}

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
package org.eclipse.kapua.configuration.spi;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.eclipse.kapua.commons.model.AbstractKapuaUpdatableEntity;
import org.eclipse.kapua.model.config.KapuaConfigEntity;
import org.eclipse.kapua.model.id.KapuaId;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder= {"scopeId","id","name","createdOn","createdBy","modifiedOn","modifiedBy","optlock"})
@Entity(name = "Configuration")
@Table(name = "sys_configuration")
public class ServiceConfigEntity extends AbstractKapuaUpdatableEntity implements KapuaConfigEntity
{
    private static final long serialVersionUID = 8699765898092343484L;

    public static final String TYPE = "scfg";

    public String getType()
    {
        return TYPE;
    }
	
	public ServiceConfigEntity(KapuaId scopeId) {
		super(scopeId);
	}
	
	//TODO Add configuration specific fields
}

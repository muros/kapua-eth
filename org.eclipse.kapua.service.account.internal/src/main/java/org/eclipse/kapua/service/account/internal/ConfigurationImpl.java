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

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.eclipse.kapua.commons.config.model.AbstractKapuaConfigEntity;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.account.Configuration;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder= {"scopeId","id","name","createdOn","createdBy","modifiedOn","modifiedBy","optlock"})
@Entity(name = "Configuration")
@Table(name = "act_configuration")
public class ConfigurationImpl extends AbstractKapuaConfigEntity implements Configuration
{
	private static final long serialVersionUID = -7130150573272544539L;
	
	public ConfigurationImpl(KapuaId scopeId) {
		super(scopeId);
	}
}

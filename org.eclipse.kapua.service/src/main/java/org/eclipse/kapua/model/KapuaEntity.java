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
package org.eclipse.kapua.model;

import java.util.Date;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.id.KapuaIdAdapter;

@XmlType(propOrder = {"id", "type", "scopeId", "createdOn", "createdBy" })
public interface KapuaEntity
{
	@XmlElement(name="id")
	@XmlJavaTypeAdapter(KapuaIdAdapter.class)
    public KapuaId getId();

	public void setId(KapuaId id);
	
	@XmlElement(name="type")
    public String getType();

	@XmlElement(name="scopeId")
	@XmlJavaTypeAdapter(KapuaIdAdapter.class)
    public KapuaId getScopeId();
	
	public void setScopeId(KapuaId id);

	@XmlElement(name="createdOn")
    public Date getCreatedOn();

	@XmlElement(name="createdBy")
	@XmlJavaTypeAdapter(KapuaIdAdapter.class)
    public KapuaId getCreatedBy();
}

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
package org.eclipse.kapua.model;

import java.util.Date;
import java.util.Properties;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.id.KapuaIdAdapter;

@XmlType(propOrder = {"modifiedOn", "modifiedBy", "optlock" })
public interface KapuaUpdatableEntity extends KapuaEntity
{
    @XmlElement(name="modifiedOn")
    public Date getModifiedOn();

	@XmlElement(name="modifiedBy")
	@XmlJavaTypeAdapter(KapuaIdAdapter.class)
    public KapuaId getModifiedBy();

	@XmlElement(name="optlock")
    public int getOptlock();

    public void setOptlock(int optlock);

    @XmlTransient
    public Properties getEntityAttributes()
        throws KapuaException;

    public void setEntityAttributes(Properties props)
        throws KapuaException;

    @XmlTransient
    public Properties getEntityProperties()
        throws KapuaException;

    public void setEntityProperties(Properties props)
        throws KapuaException;
}

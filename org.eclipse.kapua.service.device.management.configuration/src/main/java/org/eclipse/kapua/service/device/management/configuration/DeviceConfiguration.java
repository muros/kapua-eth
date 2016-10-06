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
package org.eclipse.kapua.service.device.management.configuration;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.eclipse.kapua.model.KapuaEntity;

@XmlRootElement(name = "configurations")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(propOrder = {
        "configurations"
}, factoryClass = DeviceConfigurationXmlRegistry.class, factoryMethod = "newConfiguration")
public interface DeviceConfiguration extends KapuaEntity
{
    @XmlElementWrapper(name = "configurations")
    public <C extends DeviceComponentConfiguration> List<C> getComponentConfigurations();
}

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
package org.eclipse.kapua.model.config;

import java.util.Map;

import org.eclipse.kapua.model.config.metatype.Tocd;

/**
 * Describes the configuration of an OSGi Component.
 * The Component configuration groups all the information related to the configuration of a Component.
 * It provides access to parsed ObjectClassDefintion associated to this Component.
 * The configuration does not reuse the OSGi ObjectClassDefinition as the latter
 * does not provide access to certain aspects such as the required attribute,
 * the min and max values. Instead it returns the raw ObjectClassDefintion as parsed
 * from the MetaType Information XML resource associated to this Component.
 */
public interface ComponentConfiguration {
    /**
     * The PID (service's persistent identity) of the OSGi Component
     * associated to this configuration.
     * The service's persistent identity is defined as the name attribute of the
     * Component Descriptor XML file; at runtime, the same value is also available
     * in the component.name and in the service.pid attributes of the Component Configuration.
     */
    public String getComponentId();

    public void setComponentId(String componentId);

    public String getComponentName();

    public void setComponentName(String componentName);

    public Tocd getDefinition();

    public void setDefinition(Tocd definition);

    public Map<String, Object> q();

    public void setProperties(Map<String, Object> properties);
}

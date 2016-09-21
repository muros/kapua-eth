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
 *******************************************************************************/
package org.eclipse.kapua.commons.configuration;

import java.util.Properties;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.model.KapuaUpdatableEntity;

public interface ServiceConfig extends KapuaUpdatableEntity
{
    public static final String TYPE = "scfg";

    default public String getType()
    {
        return TYPE;
    }

    public String getPid();

    public void setPid(String pid);

    public Properties getConfigurations() throws KapuaException;

    public void setConfigurations(Properties configurations) throws KapuaException;
}

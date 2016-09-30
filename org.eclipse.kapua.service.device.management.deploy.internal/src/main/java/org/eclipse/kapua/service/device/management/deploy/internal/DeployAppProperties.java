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
package org.eclipse.kapua.service.device.management.deploy.internal;

import org.eclipse.kapua.service.device.management.KapuaAppProperties;

public enum DeployAppProperties implements KapuaAppProperties
{
    APP_NAME("DEPLOY"), 
    APP_VERSION("1.0.0"),
    
    APP_PROPERTY_DEPLOY_INSTALL_URL("kapua.deploy.url"),
    APP_PROPERTY_DEPLOY_INSTALL_FILENAME("kapua.deploy.install.filename"),
    
    APP_PROPERTY_DEPLOY_UNINSTALL_FILENAME("kapua.deploy.uninstall.filename"),
    
    ;

    private String value;

    DeployAppProperties(String value)
    {
        this.value = value;
    }

    @Override
    public String getValue()
    {
        return value;
    }

}

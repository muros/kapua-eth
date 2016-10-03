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
package org.eclipse.kapua.service.device.management.bundle;

public interface DeviceBundle
{
    public long getId();

    public void setId(long id);

    public String getName();

    public void setName(String name);

    public String getState();

    public void setState(String state);

    public String getVersion();

    public void setVersion(String version);

}

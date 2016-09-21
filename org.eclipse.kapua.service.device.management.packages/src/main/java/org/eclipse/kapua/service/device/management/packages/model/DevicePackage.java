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
package org.eclipse.kapua.service.device.management.packages.model;

import java.util.Date;

//TODO: Needs annotation. First verify if possible with JAXB or EclipseLink MOXy
public interface DevicePackage
{

    public String getName();

    public void setName(String dpName);

    public String getVersion();

    public void setVersion(String dpVersion);

    public <B extends DevicePackageBundleInfos> B getBundleInfos();

    public Date getInstallDate();

    public void setInstallDate(Date installDate);
}

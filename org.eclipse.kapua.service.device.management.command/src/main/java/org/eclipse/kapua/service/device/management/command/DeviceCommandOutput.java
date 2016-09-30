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
package org.eclipse.kapua.service.device.management.command;

public interface DeviceCommandOutput
{
    public String getStderr();

    public void setStderr(String stderr);

    public String getStdout();

    public void setStdout(String stdout);

    public String getExceptionMessage();

    public void setExceptionMessage(String exceptionMessage);

    public String getExceptionStack();

    public void setExceptionStack(String exceptionStack);

    public Integer getExitCode();

    public void setExitCode(Integer exitCode);

    public Boolean hasTimedout();

    public void setHasTimedout(Boolean hasTimedout);
}

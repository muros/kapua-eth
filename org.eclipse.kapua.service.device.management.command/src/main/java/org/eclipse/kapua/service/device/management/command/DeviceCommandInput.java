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
package org.eclipse.kapua.service.device.management.command;

public interface DeviceCommandInput
{
    public void setCommand(String command);

    public String getCommand();

    public void setPassword(String password);

    public String getPassword();

    public void setArguments(String[] arguments);

    public String[] getArguments();

    public void setTimeout(Integer timeout);

    public Integer getTimeout();

    public void setWorkingDir(String workingDir);

    public String getWorkingDir();

    public void setBytes(byte[] bytes);

    public byte[] getBytes();

    public void setEnvironment(String[] environment);

    public String[] getEnvironment();

    public void setRunAsynch(boolean runAsync);

    public boolean isRunAsynch();

    public void setStdin(String stdin);

    public String getStdin();
}

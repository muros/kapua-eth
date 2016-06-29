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
package org.org.eclipse.kapua.service.device.management.command;

public interface DeviceCommandInput
{
    public void setCommand(String command);

    public void setPassword(String password);

    public void setArguments(String[] arguments);

    public void setTimeout(Integer timeout);

    public void setWorkingDir(String workingDir);

    public void setBytes(byte[] bytes);

    public void setEnvironment(String[] environment);

    public void setRunAsynch(boolean runAsync);

    public void setStdin(String stdin);
}

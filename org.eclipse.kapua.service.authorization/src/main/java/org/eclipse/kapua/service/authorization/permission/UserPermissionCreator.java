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
package org.eclipse.kapua.service.authorization.permission;

import org.eclipse.kapua.model.KapuaEntityCreator;
import org.eclipse.kapua.model.id.KapuaId;

public interface UserPermissionCreator extends KapuaEntityCreator<UserPermission>
{
    public void setUserId(KapuaId userId);

    public KapuaId getUserId();

    public void setDomain(String domain);

    public String getDomain();

    public void setAction(String action);

    public String getAction();

    public void setTargetScopeId(KapuaId entityId);

    public KapuaId getTargetScopeId();
}

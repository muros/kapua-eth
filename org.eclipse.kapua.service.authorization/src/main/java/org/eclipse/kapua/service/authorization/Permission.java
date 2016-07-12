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
package org.eclipse.kapua.service.authorization;

import org.eclipse.kapua.model.id.KapuaId;

public interface Permission
{
    public void setDomain(Domain domain);

    public Domain getDomain();

    public void setAction(Action action);

    public Action getAction();

    public void setTargetScopeId(KapuaId targetScopeId);

    public KapuaId getTargetScopeId();
}

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
package org.eclipse.kapua.service.authorization.permission.shiro;

import org.eclipse.kapua.commons.model.query.KapuaListResultImpl;
import org.eclipse.kapua.service.authorization.permission.UserPermission;
import org.eclipse.kapua.service.authorization.permission.UserPermissionListResult;

public class UserPermissionListResultImpl extends KapuaListResultImpl<UserPermission> implements UserPermissionListResult
{
    private static final long serialVersionUID = 2231053707705207563L;
}

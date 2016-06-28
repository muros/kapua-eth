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
package org.eclipse.kapua.service.datastore.internal;

import org.eclipse.kapua.service.datastore.internal.PermissionAction;

public enum DatastorePermAction implements PermissionAction
{
    CREATE("create"),
    UPDATE("update"),
    DELETE("delete"),
    READ("read");
    
    private String key;
    
    private DatastorePermAction(String key)
    {
        this.key = key;
    }
    
    public String key()
    {
        return this.key;
    }
}

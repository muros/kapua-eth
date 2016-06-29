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
package org.eclipse.kapua.configuration.spi;

import org.eclipse.kapua.commons.model.AbstractKapuaEntityCreator;
import org.eclipse.kapua.model.config.KapuaConfigEntityCreator;
import org.eclipse.kapua.model.id.KapuaId;

public class ServiceConfigEntityCreator extends AbstractKapuaEntityCreator<ServiceConfigEntity>
                                        implements KapuaConfigEntityCreator<ServiceConfigEntity>
{
    private static final long serialVersionUID = -1159593426674371485L;

    protected ServiceConfigEntityCreator(KapuaId scopeId)
    {
        super(scopeId);
        // TODO Auto-generated constructor stub
    }

    // TODO add configuration specific fields
}

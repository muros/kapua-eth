/*******************************************************************************
 * Copyright (c) 2011, 2016 Eurotech and/or its affiliates
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *      Eurotech - initial API and implementation
 *******************************************************************************/
package org.eclipse.kapua.commons.config.model;

import org.eclipse.kapua.commons.model.AbstractKapuaEntityCreator;
import org.eclipse.kapua.model.config.KapuaConfigEntity;
import org.eclipse.kapua.model.config.KapuaConfigEntityCreator;
import org.eclipse.kapua.model.id.KapuaId;

public abstract class AbstractKapuaConfigEntityCreator<E extends KapuaConfigEntity> 
			extends AbstractKapuaEntityCreator<E> implements KapuaConfigEntityCreator<E>
{

    protected AbstractKapuaConfigEntityCreator(KapuaId scopeId) {
		super(scopeId);
		// TODO Auto-generated constructor stub
	}

	private static final long serialVersionUID = -1159593426674371485L;

}

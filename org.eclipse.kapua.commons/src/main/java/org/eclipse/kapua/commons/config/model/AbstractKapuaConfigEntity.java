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
package org.eclipse.kapua.commons.config.model;

import org.eclipse.kapua.commons.model.AbstractKapuaUpdatableEntity;
import org.eclipse.kapua.model.config.KapuaConfigEntity;
import org.eclipse.kapua.model.id.KapuaId;

public abstract class AbstractKapuaConfigEntity extends AbstractKapuaUpdatableEntity implements KapuaConfigEntity
{
	private static final long serialVersionUID = 8699765898092343484L;
	
	public AbstractKapuaConfigEntity(KapuaId scopeId) {
		super(scopeId);
	}
}

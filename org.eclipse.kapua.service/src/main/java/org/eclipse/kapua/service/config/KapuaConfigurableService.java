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
package org.eclipse.kapua.service.config;

import java.util.Map;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.model.config.metatype.Tocd;
import org.eclipse.kapua.model.id.KapuaId;

public interface KapuaConfigurableService
{
	public Tocd getConfigMetadata(KapuaId scopeId) throws KapuaException;
	
	public Map<String, Object> getConfigValues(KapuaId scopeId) throws KapuaException;
	
	public void setConfigValues(KapuaId scopeId, Map<String, Object> values) throws KapuaException;
}

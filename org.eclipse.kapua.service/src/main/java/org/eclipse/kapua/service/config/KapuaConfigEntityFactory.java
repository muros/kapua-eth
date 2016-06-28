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

import org.eclipse.kapua.model.KapuaEntityFactory;
import org.eclipse.kapua.model.config.KapuaConfigEntity;
import org.eclipse.kapua.model.config.KapuaConfigEntityCreator;
import org.eclipse.kapua.model.id.KapuaId;

public interface KapuaConfigEntityFactory<E extends KapuaConfigEntity, 
										  C extends KapuaConfigEntityCreator<E>> extends KapuaEntityFactory 
{
	public C newConfigurationCreator(KapuaId scopeId);
	public E newConfiguration(C creator);
}

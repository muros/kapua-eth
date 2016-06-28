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
package org.eclipse.kapua.service.account.internal;

import org.eclipse.kapua.commons.config.model.AbstractKapuaConfigEntityCreator;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.account.Configuration;
import org.eclipse.kapua.service.account.ConfigurationCreator;

public class ConfigurationCreatorImpl extends AbstractKapuaConfigEntityCreator<Configuration> implements ConfigurationCreator
{
	private static final long serialVersionUID = -5668999801424728257L;
	
	public ConfigurationCreatorImpl(KapuaId scopeId) {
		super(scopeId);
	}
}

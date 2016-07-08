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
package org.eclipse.kapua.service.authentication.shiro.config;

import org.eclipse.kapua.commons.setting.SettingKey;

public enum KapuaAuthenticationConfigKeys implements SettingKey
{
	AUTHENTICATION_KEY("authentication.key");
	
	private String key;
	
	private KapuaAuthenticationConfigKeys(String key) {
		this.key = key;
	}
	
	public String key() {
		return key;
	}
}

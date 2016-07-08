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
package org.eclipse.kapua.service.user.internal.config;

import org.eclipse.kapua.commons.setting.SettingKey;

public enum KapuaUserConfigKeys implements SettingKey
{
	USER_KEY("user.key");
	
	private String key;
	
	private KapuaUserConfigKeys(String key) {
		this.key = key;
	}
	
	public String key() {
		return key;
	}
}

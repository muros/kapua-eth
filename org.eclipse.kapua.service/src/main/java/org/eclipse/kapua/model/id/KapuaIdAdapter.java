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
 *******************************************************************************/
package org.eclipse.kapua.model.id;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.eclipse.kapua.locator.KapuaLocator;

public class KapuaIdAdapter extends XmlAdapter<String, KapuaId>{

	private final KapuaLocator locator = KapuaLocator.getInstance();
	private final KapuaIdFactory kapuaIdFactory = locator.getFactory(KapuaIdFactory.class);

	@Override
	public String marshal(KapuaId v) throws Exception 
	{
		return v.getShortId();
	}

	@Override
	public KapuaId unmarshal(String v) throws Exception {
		return kapuaIdFactory.newKapuaId(v);
	}

}

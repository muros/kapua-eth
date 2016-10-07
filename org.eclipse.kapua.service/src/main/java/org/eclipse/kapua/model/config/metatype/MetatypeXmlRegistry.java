/*******************************************************************************
 * Copyright (c) 2011, 2016 Eurotech and/or its affiliates and others
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Eurotech - initial API and implementation
 *******************************************************************************/

package org.eclipse.kapua.model.config.metatype;

import org.eclipse.kapua.locator.KapuaLocator;

public class MetatypeXmlRegistry {
    private KapuaLocator locator = KapuaLocator.getInstance();
    private KapuaMetatypeFactory factory = locator.getFactory(KapuaMetatypeFactory.class);

    public KapuaTocd newKapuaTocd() { return factory.newKapuaTocd(); }
}

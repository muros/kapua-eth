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

import org.eclipse.kapua.model.KapuaObjectFactory;

public interface KapuaMetatypeFactory extends KapuaObjectFactory{
    public KapuaTocd newKapuaTocd();

    public KapuaTad newKapuaTad();

    public KapuaTscalar newKapuaTscalar();

    public KapuaToption newKapuaToption();

    public KapuaTicon newKapuaTicon();
}

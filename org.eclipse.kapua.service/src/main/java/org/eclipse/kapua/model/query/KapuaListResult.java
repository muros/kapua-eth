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
package org.eclipse.kapua.model.query;

import java.util.List;

import org.eclipse.kapua.model.KapuaEntity;

public interface KapuaListResult<E extends KapuaEntity> extends List<E>
{
    public boolean isLimitExceeded();

    public void setLimitExceeded(boolean limitExceeded);
}

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
 *
 *******************************************************************************/
package org.eclipse.kapua.model.query;

import org.eclipse.kapua.model.KapuaEntity;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.query.predicate.KapuaPredicate;

public interface KapuaQuery<E extends KapuaEntity>
{
    public Integer getOffset();

    public Integer getLimit();

    public void setOffset(Integer offset);

    public void setLimit(Integer limit);

    public void setScopeId(KapuaId scopeId);

    public KapuaId getScopeId();

    public void setPredicate(KapuaPredicate queryPredicate);

    public KapuaPredicate getPredicate();

    public void setSortCriteria(KapuaSortCriteria sortCriteria);

    public KapuaSortCriteria getSortCriteria();

    public KapuaFetchStyle getFetchStyle();

    public void setFetchStyle(KapuaFetchStyle fetchStyle);
}

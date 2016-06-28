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
package org.eclipse.kapua.service.datastore.internal.model.query;

import org.eclipse.kapua.service.datastore.internal.AbstractStorableQuery;
import org.eclipse.kapua.service.datastore.model.TopicInfo;
import org.eclipse.kapua.service.datastore.model.query.TopicInfoQuery;

public class StorableTopicQueryImpl extends AbstractStorableQuery<TopicInfo> implements TopicInfoQuery
{

    private String prefix;

    public StorableTopicQueryImpl() {
        super();
        this.prefix = null;
    }

    public StorableTopicQueryImpl(String prefix) {
        super();
        this.prefix = prefix;
    }

    public String getPrefix() {
        return prefix;
    }

    public TopicInfoQuery setPrefix(String prefix) {
        this.prefix = prefix;
        return this;
    }
}

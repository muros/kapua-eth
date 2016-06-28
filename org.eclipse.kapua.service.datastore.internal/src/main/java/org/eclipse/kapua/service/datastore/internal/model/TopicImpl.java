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
package org.eclipse.kapua.service.datastore.internal.model;

import org.eclipse.kapua.service.datastore.model.Topic;

public class TopicImpl implements Topic
{
    private String fullTopic;

    @Override
    public String getTopicName() {
        return fullTopic;
    }

    @Override
    public void setTopicName(String topicName)
    {
        fullTopic = topicName;
    }
}

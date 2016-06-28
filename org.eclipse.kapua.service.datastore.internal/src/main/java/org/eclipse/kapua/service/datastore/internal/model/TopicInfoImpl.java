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

import java.util.Date;

import org.eclipse.kapua.service.datastore.model.Topic;
import org.eclipse.kapua.service.datastore.model.TopicInfo;

public class TopicInfoImpl implements TopicInfo
{
    private Topic topic;
    private Date lastMsgTimestamp;
    private String fullTopicName;
    
    @Override
    public Topic getMessageTopic()
    {
        return topic;
    }

    @Override
    public Date getLastMessageTimestamp()
    {
        return lastMsgTimestamp;
    }

    @Override
    public String getFullTopicName()
    {
        return fullTopicName;
    }

}

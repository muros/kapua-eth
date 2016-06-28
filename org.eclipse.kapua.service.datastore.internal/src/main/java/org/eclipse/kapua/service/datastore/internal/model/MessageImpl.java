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
package org.eclipse.kapua.service.datastore.internal.model;

import java.util.Date;

import org.eclipse.kapua.service.datastore.model.Message;
import org.eclipse.kapua.service.datastore.model.Payload;
import org.eclipse.kapua.service.datastore.model.Topic;

public class MessageImpl implements Message
{
    private String  uuid;
    private Topic   topic;
    private Date    timestamp;
    private Date    receivedOn;
    private Payload payload;

    public MessageImpl()
    {
    }

    public MessageImpl(String uuid, Date timestamp, Topic topic)
    {
        this.uuid = null;
        this.timestamp = timestamp;
        this.topic = topic;
    }

    @Override
    public String getUuid()
    {
        return this.uuid;
    }

    @Override
    public Topic getTopic()
    {
        return this.topic;
    }

    @Override
    public void setTopic(Topic topic)
    {
        this.topic = topic;
    }

    @Override
    public String getFullTopic()
    {
        return this.topic.getTopicName();
    }

    @Override
    public Date getTimestamp()
    {
        return this.timestamp;
    }

    @Override
    public void setTimestamp(Date timestamp)
    {
        this.timestamp = timestamp;
    }

    @Override
    public Date getReceivedOn()
    {
        return this.receivedOn;
    }

    @Override
    public void setReceivedOn(Date receivedOn)
    {
        this.receivedOn = receivedOn;
    }

    public void setUUID(String uuid)
    {
        this.uuid = uuid;
    }

    @Override
    public Payload getPayload()
    {
        return this.payload;
    }

    @Override
    public void setPayload(Payload payload)
    {
        this.payload = payload;
    }
}

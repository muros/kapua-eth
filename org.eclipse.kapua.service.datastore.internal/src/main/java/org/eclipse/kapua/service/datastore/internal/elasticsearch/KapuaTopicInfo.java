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
package org.eclipse.kapua.service.datastore.internal.elasticsearch;

import java.util.Date;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.eclipse.kapua.message.KapuaTopic;

@XmlRootElement(name = "topicInfo")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class KapuaTopicInfo {
    private KapuaTopic m_edcTopic;
    private Date m_timestamp;

    public KapuaTopicInfo() {
    }

    public KapuaTopicInfo(KapuaTopic topic, Date timestamp) {
        super();
        m_edcTopic = topic;
        m_timestamp = timestamp;
    }

    public KapuaTopic getEdcTopic() {
        return m_edcTopic;
    }

    @XmlElement(name = "lastMessageOn")
    public Date getLastMessageTimestamp() {
        return m_timestamp;
    }

    @XmlElement(name = "topic")
    public String getFullTopic() {
        return m_edcTopic.getFullTopic();
    }
}
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
package org.eclipse.kapua.message;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.eclipse.kapua.message.KapuaMessage;
import org.eclipse.kapua.message.KapuaPayload;
import org.eclipse.kapua.message.KapuaTopic;


/**
 * EdcMessage provides an abstraction over the messages sent in and out of the Everyware Cloud platform.
 * It encapsulates all the information regarding the message: the topic it was addressed to, the timestamp
 * when it was received by the platform, and the payload contained in the message.
 * The payload can be represented by a raw binary array or by an EdcPayload object if it was formatted
 * as such when the message was composed and sent. Refer to the EdcPayload documentation for more details on
 * how EdcPayload are modelled and how they can be constructed.<br/>
 * The EdcMessage class is used both by the messages/search API to return message results from the platform,
 * as well as by messages/store and messages/publish API to send messages to the platform.
 *
 */
@XmlRootElement(name="message")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(propOrder= {"topic","timestamp","edcPayload","uuid"})
public class KapuaMessage implements Comparable<KapuaMessage> {
    private KapuaTopic   m_edcTopic;
    private Date       m_timestamp;
    private byte[]     m_payload;
    private String     m_uuid;
    private KapuaPayload m_edcPayload;

    public KapuaMessage() {
    }

    public KapuaMessage(KapuaTopic topic, Date timestamp, byte[] payload) {
        m_edcTopic  = topic;
        m_timestamp = timestamp;
        m_payload   = payload;
        m_uuid      = null;
    }

    public KapuaMessage(KapuaTopic topic, Date timestamp, String uuid) {
        m_edcTopic  = topic;
        m_timestamp = timestamp;
        m_uuid      = uuid;
    }

    public KapuaMessage(KapuaTopic topic, Date timestamp, String uuid, KapuaPayload payload) {
        m_edcTopic   = topic;
        m_timestamp  = timestamp;
        m_uuid       = uuid;
        m_edcPayload = payload;
        m_payload    = null;
    }

    public KapuaMessage(KapuaTopic topic, Date timestamp, String uuid, byte[] payload) {
        m_edcTopic   = topic;
        m_timestamp  = timestamp;
        m_uuid       = uuid;
        m_payload    = payload;
        m_edcPayload = null;
    }

    public KapuaTopic getKapuaTopic() {
        return m_edcTopic;
    }

    /**
     * The topic to which this message is sent to.
     * A generic Publishing topic can be represented as accountName/assetId/semanticTopic where:
     * <ul>
     * <li>accountName is the name of the Everyware Cloud account owner.
     * <li>assetId is a unique ID representing a particular asset (either the application or the sensors from which the data has been gathered).
     * <li>semanticTopic is the section of the topic used to further specify information about the device or data, using an hierarchical name space representation.
     * </ul>
    * System and control topic starts with the $EDC account and are represented as: $EDC/accountName/assetId/semanticTopic.
     */
    @XmlElement(name="topic")
    public String getTopic() {
        return m_edcTopic.getFullTopic();
    }

    public void setTopic(String fullTopic) throws KapuaInvalidTopicException {
        m_edcTopic = new KapuaTopic(fullTopic);
    }

    /**
     * The timestamp when this message was received by the platform.
     * This timestamp has to be distinguished from the timestamp when the message was sent to the platform, which applications can capture in the EdcPayload timestamp field.
     * In the case of the REST API messages/store and messages/publish, the value provided in this field is ignored and a server-side timestamp will be used.
     */
    @XmlElement(name="receivedOn")
    public Date getTimestamp() {
        return m_timestamp;
    }

    public void setTimestamp(Date timestamp) {
        m_timestamp = timestamp;
    }

    /**
     * The uuid of the published message.
     */
    @XmlElement(name="uuid")
    public String getUuid() {
        return m_uuid;
    }

    public void setUuid(String uuid) {
        m_uuid = uuid;
    }

    /**
     * The raw binary array of the payload if this message payload was not formatted using the EdcPayload object.
     */
    public byte[] getPayload() {
        if(m_payload == null) {
            if(m_edcPayload != null) {
                return m_edcPayload.toByteArray();
            }
        }
        return m_payload;
    }

    /**
     * The payload of this message as EdcPayload object.
     */
    @XmlElement(name="payload")
    public KapuaPayload getKapuaPayload() {
        return m_edcPayload;
    }

    public void setKapuaPayload(KapuaPayload payload) {
        m_edcPayload = payload;
    }

    public boolean hasEdcPayload() {
        if(m_edcPayload == null) {
            return false;
        }
        return true;
    }

    public int compareTo(KapuaMessage msg) {
        return (m_timestamp.compareTo(msg.getTimestamp())*(-1));
    }
}

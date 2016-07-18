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
package org.eclipse.kapua.message.internal;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.eclipse.kapua.message.KapuaChannel;
import org.eclipse.kapua.message.KapuaMessage;
import org.eclipse.kapua.message.KapuaPayload;

/**
 * KapuaMessage provides an abstraction over the messages sent in and out of the Everyware Cloud platform.
 * It encapsulates all the information regarding the message: the topic it was addressed to, the timestamp
 * when it was received by the platform, and the payload contained in the message.
 * The payload can be represented by a raw binary array or by an KapuaPayload object if it was formatted
 * as such when the message was composed and sent. Refer to the KapuaPayload documentation for more details on
 * how KapuaPayload are modelled and how they can be constructed.<br/>
 * The KapuaMessage class is used both by the messages/search API to return message results from the platform,
 * as well as by messages/store and messages/publish API to send messages to the platform.
 *
 */
@SuppressWarnings("rawtypes")
@XmlRootElement(name = "message")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(propOrder = { "destination", "timestamp", "payload" })
public abstract class AbstractKapuaMessage<D extends KapuaChannel, P extends KapuaPayload> implements Comparable<AbstractKapuaMessage>, KapuaMessage<D, P>
{
    private D    destination;
    private Date timestamp;
    private P    payload;

    public AbstractKapuaMessage()
    {
    }

    public AbstractKapuaMessage(D destination, Date timestamp, P payload)
    {
        this.destination = destination;
        this.timestamp = timestamp;
        this.payload = payload;
    }

    @XmlElement(name = "destination")
    public D getChannel()
    {
        return destination;
    }

    @Override
    public void setChannel(D destination)
    {
        this.destination = destination;
    }

    // /**
    // * The topic to which this message is sent to.
    // * A generic Publishing topic can be represented as accountName/assetId/semanticTopic where:
    // * <ul>
    // * <li>accountName is the name of the Everyware Cloud account owner.
    // * <li>assetId is a unique ID representing a particular asset (either the application or the sensors from which the data has been gathered).
    // * <li>semanticTopic is the section of the topic used to further specify information about the device or data, using an hierarchical name space representation.
    // * </ul>
    // * System and control topic starts with the $EDC account and are represented as: $EDC/accountName/assetId/semanticTopic.
    // */
    // @XmlElement(name = "topic")
    // public String getTopic()
    // {
    // return destination.getFullTopic();
    // }
    //
    // public void setTopic(String fullTopic)
    // throws KapuaInvalidTopicException
    // {
    // destination = new KapuaTopic(fullTopic);
    // }

    /**
     * The timestamp when this message was received by the platform.
     * This timestamp has to be distinguished from the timestamp when the message was sent to the platform, which applications can capture in the KapuaPayload timestamp field.
     * In the case of the REST API messages/store and messages/publish, the value provided in this field is ignored and a server-side timestamp will be used.
     */
    @XmlElement(name = "receivedOn")
    public Date getTimestamp()
    {
        return timestamp;
    }

    public void setTimestamp(Date timestamp)
    {
        this.timestamp = timestamp;
    }

    /**
     * The payload of this message as KapuaPayload object.
     */
    @XmlElement(name = "payload")
    public P getPayload()
    {
        return payload;
    }

    public void setPayload(P payload)
    {
        this.payload = payload;
    }

    @Override
    public int compareTo(AbstractKapuaMessage msg)
    {
        return (timestamp.compareTo(msg.getTimestamp()) * (-1));
    }

}

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

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlInlineBinaryData;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.eclipse.kapua.message.KapuaPayload;
import org.eclipse.kapua.message.KapuaPosition;
import org.eclipse.kapua.message.internal.xml.KapuaMetricsMapAdapter;

/**
 * KapuaPayload defines the recommended payload structure for the messages sent to the Everyware Cloud platform.
 * Eurotech designed the format as an open format that is flexible from the aspect of data modeling
 * yet is efficient when it comes to bandwidth conservation. The same payload model is used by the REST API
 * - in which case it is serialized into XML or JSON as requested by the client - or uses the efficient
 * Google ProtoBuf when sent over an MQTT connection when the bandwidth is very important.
 * The KapuaPayload contains the following fields: sentOn timestamp, an optional set of metrics represented as
 * name-value pairs, an optional position field to capture a GPS position, and an optional binary body.
 * <ul>
 * <li>sentOn: it is the timestamp when the data was captured and sent to the Everyware Cloud platform.
 * <li>metrics: a metric is a data structure composed of the name, a value, and the type of the value.
 * When used with the REST API valid metric types are: string, double, int, float, long, boolean, base64Binary.
 * Data exposed into the payload metrics can be processed through the real-time rules offered by the
 * Everyware Cloud platform or used as query criteria when searching for messages through the messages/searchByMetric API.
 * Each payload can have zero or more metrics.
 * <li>position: it is an optional field used to capture a geo position associated to this payload.
 * <li>body: it is an optional part of the payload that allows additional information to be transmitted in any format determined by the user.
 * This field will be stored into the platform database, but the Everyware Cloud cannot apply any statistical analysis on it.
 * </ul>
 */
@XmlRootElement(name = "payload")
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class AbstractKapuaPayload implements KapuaPayload
{
    /**
     * Timestamp when the data was captured and sent to the Everyware Cloud platform.
     */
    @XmlElement(name = "sentOn")
    private Date                timestamp;

    /**
     * It is an optional field used to capture a geo position associated to this payload.
     */
    @XmlElement(name = "position")
    private KapuaPosition       position;

    /**
     * A metric is a data structure composed of the name, a value, and the type of the value.
     * When used with the REST API valid metric types are: string, double, int, float, long, boolean, base64Binary.
     * Data exposed into the payload metrics can be processed through the real-time rules offered by the
     * Everyware Cloud platform or used a query criteria when searching for messages through the messages/searchByMetric API.
     * Each payload can have zero or more metrics.
     */
    @XmlElement(name = "metrics")
    @XmlJavaTypeAdapter(KapuaMetricsMapAdapter.class)
    private Map<String, Object> metrics;

    /**
     * It is an optional part of the payload that allows additional information to be transmitted in any format determined by the user.
     * This field will be stored into the platform database but the Everyware Cloud cannot apply any statistical analysis on it.
     */
    @XmlElement(name = "body")
    @XmlInlineBinaryData
    private byte[]              body;

    public AbstractKapuaPayload()
    {
        metrics = new HashMap<String, Object>();
        this.body = null;
    }

    public AbstractKapuaPayload(AbstractKapuaPayload kapuaPayload)
    {
        this();

        for (String name : kapuaPayload.metricNames()) {
            addMetric(name, kapuaPayload.getMetric(name));
        }
        setBody(kapuaPayload.getBody());
    }

    public Date getTimestamp()
    {
        return timestamp;
    }

    public void setTimestamp(Date timestamp)
    {
        this.timestamp = timestamp;
    }

    public KapuaPosition getPosition()
    {
        return position;
    }

    public void setPosition(KapuaPosition position)
    {
        this.position = position;
    }

    public Object getMetric(String name)
    {
        return metrics.get(name);
    }

    public void addMetric(String name, Object value)
    {
        metrics.put(name, value);
    }

    public void removeMetric(String name)
    {
        metrics.remove(name);
    }

    public void removeAllMetrics()
    {
        metrics.clear();
    }

    public Set<String> metricNames()
    {
        return Collections.unmodifiableSet(metrics.keySet());
    }

    public Iterator<String> metricsIterator()
    {
        return metrics.keySet().iterator();
    }

    public Map<String, Object> metrics()
    {
        return Collections.unmodifiableMap(metrics);
    }

    public byte[] getBody()
    {
        return body;
    }

    public void setBody(byte[] body)
    {
        this.body = body;
    }

    //
    // Utility methods
    //
    public String toDisplayString()
    {
        StringBuilder sb = new StringBuilder();
        Iterator<String> hdrIterator = metricsIterator();
        while (hdrIterator.hasNext()) {
            String hdrName = hdrIterator.next();
            Object hdrValue = getMetric(hdrName);
            String hdrValueString = "";
            Class<?> type = hdrValue.getClass();
            if (type == Float.class) {
                hdrValueString = Float.toString((Float) hdrValue);
            }
            else if (type == Double.class) {
                hdrValueString = Double.toString((Double) hdrValue);
            }
            else if (type == Integer.class) {
                hdrValueString = Integer.toString((Integer) hdrValue);
            }
            else if (type == Long.class) {
                hdrValueString = Long.toString((Long) hdrValue);
            }
            else if (type == Boolean.class) {
                hdrValueString = Boolean.toString((Boolean) hdrValue);
            }
            else if (type == String.class) {
                hdrValueString = (String) hdrValue;
            }
            else if (type == byte[].class) {
                hdrValueString = byteArrayToHexString((byte[]) hdrValue);
            }
            sb.append(hdrName);
            sb.append("=");
            sb.append(hdrValueString);

            if (hdrIterator.hasNext()) {
                sb.append("~~");
            }
        }

        if (position != null) {
            String pos = position.toDisplayString();
            if (pos != null) {
                sb.append("~~").append(pos);
            }
        }

        return sb.toString();
    }

    private String byteArrayToHexString(byte[] b)
    {
        StringBuffer sb = new StringBuffer(b.length * 2);
        for (int i = 0; i < b.length; i++) {
            int v = b[i] & 0xff;
            if (v < 16) {
                sb.append('0');
            }
            sb.append(Integer.toHexString(v));
        }
        return sb.toString().toUpperCase();
    }
}

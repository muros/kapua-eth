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

import java.io.IOException;
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
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.eclipse.kapua.message.protobuf.KapuaPayloadProto;
import org.eclipse.kapua.message.util.GZIPUtils;
import org.eclipse.kapua.message.xml.KapuaMetricsMapAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;

/**
 * EdcPayload defines the recommended payload structure for the messages sent to the Everyware Cloud platform.
 * Eurotech designed the format as an open format that is flexible from the aspect of data modeling
 * yet is efficient when it comes to bandwidth conservation. The same payload model is used by the REST API
 * - in which case it is serialized into XML or JSON as requested by the client - or uses the efficient
 * Google ProtoBuf when sent over an MQTT connection when the bandwidth is very important.
 * The EdcPayload contains the following fields: sentOn timestamp, an optional set of metrics represented as
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
public class KapuaPayload
{

    private static final Logger s_logger = LoggerFactory.getLogger(KapuaPayload.class);

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

    @XmlTransient
    private byte[]              rawBytes;

    public KapuaPayload()
    {
        metrics = new HashMap<String, Object>();
        this.body = null;
        this.rawBytes = null;
    }

    public KapuaPayload(KapuaPayload kapuaPayload)
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
        invalidateRawBytes();
        this.timestamp = timestamp;
    }

    public KapuaPosition getPosition()
    {
        return position;
    }

    public KapuaPayload setPosition(KapuaPosition position)
    {
        invalidateRawBytes();
        this.position = position;
        return this;
    }

    public Object getMetric(String name)
    {
        return metrics.get(name);
    }

    public void addMetric(String name, Object value)
    {
        invalidateRawBytes();
        metrics.put(name, value);
    }

    public void removeMetric(String name)
    {
        invalidateRawBytes();
        metrics.remove(name);
    }

    public void removeAllMetrics()
    {
        invalidateRawBytes();
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
        invalidateRawBytes();
        this.body = body;
    }

    public static KapuaPayload buildFromByteArray(byte[] bytes)
        throws KapuaInvalidMessageException, IOException
    {
        return buildFromByteArray(bytes, null);
    }

    /**
     * Factory method to build an EdcPayload instance from a byte array.
     *
     * @param bytes
     * @return
     * @throws InvalidProtocolBufferException
     * @throws IOException
     */
    public static KapuaPayload buildFromByteArray(byte[] bytes, String jmsTopic)
        throws KapuaInvalidMessageException, IOException
    {
        // Check if a compressed payload and try to decompress it
        if (GZIPUtils.isCompressed(bytes)) {
            try {
                bytes = GZIPUtils.decompress(bytes);
            }
            catch (IOException e) {
                s_logger.info("Decompression failed! Topic was: {} - message size: {}", new Object[] { jmsTopic, bytes.length }, e);
            }
        }

        // build the EdcPayloadProto.EdcPayload
        KapuaPayloadProto.KapuaPayload protoMsg = null;
        try {
            protoMsg = KapuaPayloadProto.KapuaPayload.parseFrom(bytes);
        }
        catch (InvalidProtocolBufferException ipbe) {
            throw new KapuaInvalidMessageException(ipbe);
        }

        // build the EdcPayload
        KapuaPayload edcPayload = new KapuaPayload();
        edcPayload.rawBytes = bytes;

        // set the timestamp
        if (protoMsg.hasTimestamp()) {
            edcPayload.timestamp = new Date(protoMsg.getTimestamp());
        }

        // set the position
        if (protoMsg.hasPosition()) {
            edcPayload.position = KapuaPosition.buildFromProtoBuf(protoMsg.getPosition());
        }

        // set the metrics
        for (int i = 0; i < protoMsg.getMetricCount(); i++) {
            String name = protoMsg.getMetric(i).getName();
            try {
                Object value = getProtoKapuaMetricValue(protoMsg.getMetric(i),
                                                        protoMsg.getMetric(i).getType());
                edcPayload.addMetric(name, value);
            }
            catch (KapuaInvalidMetricTypeException ihte) {
                s_logger.warn("During deserialization, ignoring metric named: {}. Unrecognized value type: {}.", name, ihte.getValueTypeOrdinal());
            }
        }

        // set the body
        if (protoMsg.hasBody()) {
            edcPayload.setBody(protoMsg.getBody().toByteArray());
        }

        return edcPayload;
    }

    /**
     * Conversion method to serialize an EdcPayload instance into a byte array.
     *
     * @return
     */
    public byte[] toByteArray()
    {
        if (this.rawBytes != null) {
            return rawBytes;
        }

        // Build the message
        KapuaPayloadProto.KapuaPayload.Builder protoMsg = KapuaPayloadProto.KapuaPayload.newBuilder();

        // set the timestamp
        if (timestamp != null) {
            protoMsg.setTimestamp(timestamp.getTime());
        }

        // set the position
        if (position != null) {
            protoMsg.setPosition(position.toProtoBuf());
        }

        // set the metrics
        for (String name : metrics.keySet()) {

            // build a metric
            Object value = metrics.get(name);
            try {
                KapuaPayloadProto.KapuaPayload.KapuaMetric.Builder metricB = KapuaPayloadProto.KapuaPayload.KapuaMetric.newBuilder();
                metricB.setName(name);

                setProtoKapuaMetricValue(metricB, value);
                metricB.build();

                // add it to the message
                protoMsg.addMetric(metricB);
            }
            catch (KapuaInvalidMetricTypeException eihte) {
                try {
                    s_logger.error("During serialization, ignoring metric named: {}. Unrecognized value type: {}.", name, value.getClass().getName());
                }
                catch (NullPointerException npe) {
                    s_logger.error("During serialization, ignoring metric named: {}. The value is null.", name);
                }
                throw new RuntimeException(eihte);
            }
        }

        // set the body
        if (this.body != null) {
            protoMsg.setBody(ByteString.copyFrom(this.body));
        }

        this.rawBytes = protoMsg.build().toByteArray();
        return this.rawBytes;
    }

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

    //
    // Helper methods to convert the EdcMetrics
    //

    private void invalidateRawBytes()
    {
        this.rawBytes = null;
    }

    private static void setProtoKapuaMetricValue(KapuaPayloadProto.KapuaPayload.KapuaMetric.Builder metric,
                                                 Object o)
        throws KapuaInvalidMetricTypeException
    {

        if (o instanceof String) {
            metric.setType(KapuaPayloadProto.KapuaPayload.KapuaMetric.ValueType.STRING);
            metric.setStringValue((String) o);
        }
        else if (o instanceof Double) {
            metric.setType(KapuaPayloadProto.KapuaPayload.KapuaMetric.ValueType.DOUBLE);
            metric.setDoubleValue((Double) o);
        }
        else if (o instanceof Integer) {
            metric.setType(KapuaPayloadProto.KapuaPayload.KapuaMetric.ValueType.INT32);
            metric.setIntValue((Integer) o);
        }
        else if (o instanceof Float) {
            metric.setType(KapuaPayloadProto.KapuaPayload.KapuaMetric.ValueType.FLOAT);
            metric.setFloatValue((Float) o);
        }
        else if (o instanceof Long) {
            metric.setType(KapuaPayloadProto.KapuaPayload.KapuaMetric.ValueType.INT64);
            metric.setLongValue((Long) o);
        }
        else if (o instanceof Boolean) {
            metric.setType(KapuaPayloadProto.KapuaPayload.KapuaMetric.ValueType.BOOL);
            metric.setBoolValue((Boolean) o);
        }
        else if (o instanceof byte[]) {
            metric.setType(KapuaPayloadProto.KapuaPayload.KapuaMetric.ValueType.BYTES);
            metric.setBytesValue(ByteString.copyFrom((byte[]) o));
        }
        else if (o == null) {
            throw new KapuaInvalidMetricTypeException("null value");
        }
        else {
            throw new KapuaInvalidMetricTypeException(o.getClass().getName());
        }
    }

    private static Object getProtoKapuaMetricValue(KapuaPayloadProto.KapuaPayload.KapuaMetric metric,
                                                   KapuaPayloadProto.KapuaPayload.KapuaMetric.ValueType type)
        throws KapuaInvalidMetricTypeException
    {
        switch (type) {

            case DOUBLE:
                return metric.getDoubleValue();

            case FLOAT:
                return metric.getFloatValue();

            case INT64:
                return metric.getLongValue();

            case INT32:
                return metric.getIntValue();

            case BOOL:
                return metric.getBoolValue();

            case STRING:
                return metric.getStringValue();

            case BYTES:
                ByteString bs = metric.getBytesValue();
                return bs.toByteArray();

            default:
                throw new KapuaInvalidMetricTypeException(type.ordinal());
        }

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

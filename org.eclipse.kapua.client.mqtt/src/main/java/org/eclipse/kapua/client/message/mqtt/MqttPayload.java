package org.eclipse.kapua.client.message.mqtt;

import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlInlineBinaryData;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.client.message.KapuaPayload;
import org.eclipse.kapua.client.mqtt.MqttClientErrorCodes;
import org.eclipse.kapua.client.mqtt.MqttClientException;
import org.eclipse.kapua.message.KapuaInvalidMetricTypeException;
import org.eclipse.kapua.message.KapuaPosition;
import org.eclipse.kapua.message.protobuf.MqttPayloadProto;
import org.eclipse.kapua.message.util.GZIPUtils;
import org.eclipse.kapua.message.xml.KapuaMetricsMapAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;

public class MqttPayload implements KapuaPayload
{
    private static final Logger s_logger = LoggerFactory.getLogger(MqttPayload.class);

    @XmlElement(name = "sentOn")
    private Date                timestamp;

    @XmlElement(name = "position")
    private KapuaPosition       position;

    @XmlElement(name = "metrics")
    @XmlJavaTypeAdapter(KapuaMetricsMapAdapter.class)
    private Map<String, Object> metrics;

    @XmlElement(name = "body")
    @XmlInlineBinaryData
    private byte[]              body;

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

    @Override
    public void readFromByteArray(byte[] rawPayload)
        throws KapuaException
    {
        //
        // Check if a compressed payload and try to decompress it
        if (GZIPUtils.isCompressed(rawPayload)) {
            try {
                rawPayload = GZIPUtils.decompress(rawPayload);
            }
            catch (IOException ioe) {
                throw new MqttClientException(MqttClientErrorCodes.GZIP_DECOMPRESSION_ERROR, ioe, (Object[]) null);
            }
        }

        //
        // Build the EdcPayloadProto.EdcPayload
        MqttPayloadProto.KapuaPayload protoMsg = null;
        try {
            protoMsg = MqttPayloadProto.KapuaPayload.parseFrom(rawPayload);
        }
        catch (InvalidProtocolBufferException ipbe) {
            throw new MqttClientException(MqttClientErrorCodes.PROTO_ENCODING_ERROR, ipbe, (Object[]) null);
        }

        //
        // Set timestamp
        if (protoMsg.hasTimestamp()) {
            timestamp = new Date(protoMsg.getTimestamp());
        }

        //
        // Set position
        if (protoMsg.hasPosition()) {
            position = KapuaPosition.buildFromProtoBuf(protoMsg.getPosition());
        }

        //
        // Set metrics
        for (int i = 0; i < protoMsg.getMetricCount(); i++) {
            String name = protoMsg.getMetric(i).getName();
            try {
                Object value = getProtoKapuaMetricValue(protoMsg.getMetric(i),
                                                        protoMsg.getMetric(i).getType());
                addMetric(name, value);
            }
            catch (KapuaInvalidMetricTypeException ihte) {
                s_logger.warn("During deserialization, ignoring metric named: {}. Unrecognized value type: {}.", name, ihte.getValueTypeOrdinal());
            }
        }

        // set the body
        if (protoMsg.hasBody()) {
            setBody(protoMsg.getBody().toByteArray());
        }
    }

    @Override
    public byte[] toByteArray()
    {
        //
        // Build the message
        MqttPayloadProto.KapuaPayload.Builder protoMsg = MqttPayloadProto.KapuaPayload.newBuilder();

        //
        // Set timestamp
        if (timestamp != null) {
            protoMsg.setTimestamp(timestamp.getTime());
        }

        //
        // Set position
        if (position != null) {
            protoMsg.setPosition(position.toProtoBuf());
        }

        //
        // Set metrics
        for (String name : metrics.keySet()) {

            // build a metric
            Object value = metrics.get(name);
            try {
                MqttPayloadProto.KapuaPayload.KapuaMetric.Builder metricB = MqttPayloadProto.KapuaPayload.KapuaMetric.newBuilder();
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

        return protoMsg.build().toByteArray();
    }

    //
    // Private methods
    //

    private static Object getProtoKapuaMetricValue(MqttPayloadProto.KapuaPayload.KapuaMetric metric,
                                                   MqttPayloadProto.KapuaPayload.KapuaMetric.ValueType type)
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

    private static void setProtoKapuaMetricValue(MqttPayloadProto.KapuaPayload.KapuaMetric.Builder metric,
                                                 Object o)
        throws KapuaInvalidMetricTypeException
    {

        if (o instanceof String) {
            metric.setType(MqttPayloadProto.KapuaPayload.KapuaMetric.ValueType.STRING);
            metric.setStringValue((String) o);
        }
        else if (o instanceof Double) {
            metric.setType(MqttPayloadProto.KapuaPayload.KapuaMetric.ValueType.DOUBLE);
            metric.setDoubleValue((Double) o);
        }
        else if (o instanceof Integer) {
            metric.setType(MqttPayloadProto.KapuaPayload.KapuaMetric.ValueType.INT32);
            metric.setIntValue((Integer) o);
        }
        else if (o instanceof Float) {
            metric.setType(MqttPayloadProto.KapuaPayload.KapuaMetric.ValueType.FLOAT);
            metric.setFloatValue((Float) o);
        }
        else if (o instanceof Long) {
            metric.setType(MqttPayloadProto.KapuaPayload.KapuaMetric.ValueType.INT64);
            metric.setLongValue((Long) o);
        }
        else if (o instanceof Boolean) {
            metric.setType(MqttPayloadProto.KapuaPayload.KapuaMetric.ValueType.BOOL);
            metric.setBoolValue((Boolean) o);
        }
        else if (o instanceof byte[]) {
            metric.setType(MqttPayloadProto.KapuaPayload.KapuaMetric.ValueType.BYTES);
            metric.setBytesValue(ByteString.copyFrom((byte[]) o));
        }
        else if (o == null) {
            throw new KapuaInvalidMetricTypeException("null value");
        }
        else {
            throw new KapuaInvalidMetricTypeException(o.getClass().getName());
        }
    }
}

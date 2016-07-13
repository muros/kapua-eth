package org.eclipse.kapua.service.device.call.message.kura;

import java.io.IOException;
import java.util.Date;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.message.KapuaPosition;
import org.eclipse.kapua.message.internal.AbstractKapuaPayload;
import org.eclipse.kapua.message.internal.MessageErrorCodes;
import org.eclipse.kapua.message.internal.MessageException;
import org.eclipse.kapua.service.device.call.message.kura.proto.KuraPayloadProto;
import org.eclipse.kapua.service.device.call.message.kura.utils.GZIPUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;

public abstract class AbstractKuraPayload extends AbstractKapuaPayload
{
    private static final Logger s_logger = LoggerFactory.getLogger(AbstractKuraPayload.class);

    @Override
    public byte[] toByteArray()
    {
        // Build the message
        KuraPayloadProto.KuraPayload.Builder protoMsg = KuraPayloadProto.KuraPayload.newBuilder();

        // set the timestamp
        if (getTimestamp() != null) {
            protoMsg.setTimestamp(getTimestamp().getTime());
        }

        // set the position
        if (getPosition() != null) {
            protoMsg.setPosition(buildPositionProtoBuf(getPosition()));
        }

        // set the metrics
        for (String name : metrics().keySet()) {

            // build a metric
            Object value = metrics().get(name);
            try {
                KuraPayloadProto.KuraPayload.KuraMetric.Builder metricB = KuraPayloadProto.KuraPayload.KuraMetric.newBuilder();
                metricB.setName(name);

                setProtoKuraMetricValue(metricB, value);
                metricB.build();

                // add it to the message
                protoMsg.addMetric(metricB);
            }
            catch (MessageException eihte) {
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
        if (getBody() != null) {
            protoMsg.setBody(ByteString.copyFrom(getBody()));
        }

        return protoMsg.build().toByteArray();
    }

    @Override
    public void readFromByteArray(byte[] bytes)
        throws KapuaException
    {
        if (GZIPUtils.isCompressed(bytes)) {
            try {
                bytes = GZIPUtils.decompress(bytes);
            }
            catch (IOException e) {
                // throw new KapuaDeviceCallException(KapuaDeviceCallErrorCodes.)
                // FIXME: manage!
                throw KapuaException.internalError(e);
            }
        }

        KuraPayloadProto.KuraPayload protoMsg = null;
        try {
            protoMsg = KuraPayloadProto.KuraPayload.parseFrom(bytes);
        }
        catch (InvalidProtocolBufferException ipbe) {
            throw new MessageException(MessageErrorCodes.INVALID_MESSAGE, ipbe, (Object[]) null);
        }

        //
        // Add timestamp
        if (protoMsg.hasTimestamp()) {
            setTimestamp(new Date(protoMsg.getTimestamp()));
        }

        //
        // Add position
        if (protoMsg.hasPosition()) {
            setPosition(buildFromProtoBuf(protoMsg.getPosition()));
        }

        //
        // Add metrics
        for (int i = 0; i < protoMsg.getMetricCount(); i++) {
            String name = protoMsg.getMetric(i).getName();
            try {
                Object value = getProtoKuraMetricValue(protoMsg.getMetric(i), protoMsg.getMetric(i).getType());
                addMetric(name, value);
            }
            catch (MessageException ihte) {

                s_logger.warn("During deserialization, ignoring metric named: " + name + ". Unrecognized value type: " + protoMsg.getMetric(i).getType(), ihte);
            }
        }

        // set the body
        if (protoMsg.hasBody()) {
            setBody(protoMsg.getBody().toByteArray());
        }
    }

    //
    // Private methods
    //
    private Object getProtoKuraMetricValue(KuraPayloadProto.KuraPayload.KuraMetric metric,
                                           KuraPayloadProto.KuraPayload.KuraMetric.ValueType type)
        throws MessageException
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
                throw new MessageException(MessageErrorCodes.INVALID_METRIC_TYPE, null, type);
        }
    }

    private static void setProtoKuraMetricValue(KuraPayloadProto.KuraPayload.KuraMetric.Builder metric, Object o)
        throws MessageException
    {

        if (o instanceof String) {
            metric.setType(KuraPayloadProto.KuraPayload.KuraMetric.ValueType.STRING);
            metric.setStringValue((String) o);
        }
        else if (o instanceof Double) {
            metric.setType(KuraPayloadProto.KuraPayload.KuraMetric.ValueType.DOUBLE);
            metric.setDoubleValue((Double) o);
        }
        else if (o instanceof Integer) {
            metric.setType(KuraPayloadProto.KuraPayload.KuraMetric.ValueType.INT32);
            metric.setIntValue((Integer) o);
        }
        else if (o instanceof Float) {
            metric.setType(KuraPayloadProto.KuraPayload.KuraMetric.ValueType.FLOAT);
            metric.setFloatValue((Float) o);
        }
        else if (o instanceof Long) {
            metric.setType(KuraPayloadProto.KuraPayload.KuraMetric.ValueType.INT64);
            metric.setLongValue((Long) o);
        }
        else if (o instanceof Boolean) {
            metric.setType(KuraPayloadProto.KuraPayload.KuraMetric.ValueType.BOOL);
            metric.setBoolValue((Boolean) o);
        }
        else if (o instanceof byte[]) {
            metric.setType(KuraPayloadProto.KuraPayload.KuraMetric.ValueType.BYTES);
            metric.setBytesValue(ByteString.copyFrom((byte[]) o));
        }
        else if (o == null) {
            throw new MessageException(MessageErrorCodes.INVALID_METRIC_VALUE, null, "null");
        }
        else {
            throw new MessageException(MessageErrorCodes.INVALID_METRIC_TYPE, null, o.getClass().getName());
        }
    }

    private KuraPayloadProto.KuraPayload.KuraPosition buildPositionProtoBuf(KapuaPosition position)
    {
        KuraPayloadProto.KuraPayload.KuraPosition.Builder protoPos = null;
        protoPos = KuraPayloadProto.KuraPayload.KuraPosition.newBuilder();

        if (position.getLatitude() != null) {
            protoPos.setLatitude(position.getLatitude());
        }
        if (position.getLongitude() != null) {
            protoPos.setLongitude(position.getLongitude());
        }
        if (position.getAltitude() != null) {
            protoPos.setAltitude(position.getAltitude());
        }
        if (position.getPrecision() != null) {
            protoPos.setPrecision(position.getPrecision());
        }
        if (position.getHeading() != null) {
            protoPos.setHeading(position.getHeading());
        }
        if (position.getSpeed() != null) {
            protoPos.setSpeed(position.getSpeed());
        }
        if (position.getTimestamp() != null) {
            protoPos.setTimestamp(position.getTimestamp().getTime());
        }
        if (position.getSatellites() != null) {
            protoPos.setSatellites(position.getSatellites());
        }
        if (position.getStatus() != null) {
            protoPos.setStatus(position.getStatus());
        }
        return protoPos.build();
    }

    private KapuaPosition buildFromProtoBuf(KuraPayloadProto.KuraPayload.KuraPosition protoPosition)
    {
        KapuaPosition position = getPosition();

        if (protoPosition.hasLatitude()) {
            position.setLatitude(protoPosition.getLatitude());
        }
        if (protoPosition.hasLongitude()) {
            position.setLongitude(protoPosition.getLongitude());
        }
        if (protoPosition.hasAltitude()) {
            position.setAltitude(protoPosition.getAltitude());
        }
        if (protoPosition.hasPrecision()) {
            position.setPrecision(protoPosition.getPrecision());
        }
        if (protoPosition.hasHeading()) {
            position.setHeading(protoPosition.getHeading());
        }
        if (protoPosition.hasSpeed()) {
            position.setSpeed(protoPosition.getSpeed());
        }
        if (protoPosition.hasSatellites()) {
            position.setSatellites(protoPosition.getSatellites());
        }
        if (protoPosition.hasStatus()) {
            position.setStatus(protoPosition.getStatus());
        }
        if (protoPosition.hasTimestamp()) {
            position.setTimestamp(new Date(protoPosition.getTimestamp()));
        }
        return position;
    }
}

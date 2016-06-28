package org.eclipse.kapua.message;

import java.util.ArrayList;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.eclipse.kapua.message.KapuaPosition;
import org.eclipse.kapua.message.protobuf.KapuaPayloadProto;

/**
 * EdcPosition is a data structure to capture a geo location. It can be
 * associated to an EdcPayload to geotag an EdcMessage before sending to the
 * Everyware Cloud. Refer to the description of each of the fields for more
 * information on the model of EdcPosition.
 */
@XmlRootElement(name = "position")
@XmlAccessorType(XmlAccessType.FIELD)
public class KapuaPosition {
    /**
     * Longitude of this position in degrees. This is a mandatory field.
     */
    @XmlElement(name = "longitude")
    private Double longitude;

    /**
     * Latitude of this position in degrees. This is a mandatory field.
     */
    @XmlElement(name = "latitude")
    private Double latitude;

    /**
     * Altitude of the position in meters.
     */
    @XmlElement(name = "altitude")
    private Double altitude;

    /**
     * Dilution of the precision (DOP) of the current GPS fix.
     */
    @XmlElement(name = "precision")
    private Double precision;

    /**
     * Heading (direction) of the position in degrees
     */
    @XmlElement(name = "heading")
    private Double heading;

    /**
     * Speed for this position in meter/sec.
     */
    @XmlElement(name = "speed")
    private Double speed;

    /**
     * Timestamp extracted from the GPS system
     */
    @XmlElement(name = "timestamp")
    private Date timestamp;

    /**
     * Number of satellites seen by the systems
     */
    @XmlElement(name = "satellites")
    private Integer satellites;

    /**
     * Status of GPS system: 1 = no GPS response, 2 = error in response, 4 =
     * valid.
     */
    @XmlElement(name = "status")
    private Integer status;

    public KapuaPosition() {
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public Double getAltitude() {
        return altitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    public Double getPrecision() {
        return precision;
    }

    public void setPrecision(double precision) {
        this.precision = precision;
    }

    public Double getHeading() {
        return heading;
    }

    public void setHeading(double heading) {
        this.heading = heading;
    }

    public Double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public Integer getSatellites() {
        return satellites;
    }

    public void setSatellites(int satellites) {
        this.satellites = satellites;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public static KapuaPosition buildFromProtoBuf(
        KapuaPayloadProto.KapuaPayload.KapuaPosition protoPosition) {

        KapuaPosition position = new KapuaPosition();

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

    public KapuaPayloadProto.KapuaPayload.KapuaPosition toProtoBuf() {

            KapuaPayloadProto.KapuaPayload.KapuaPosition.Builder protoPos = KapuaPayloadProto.KapuaPayload.KapuaPosition
                .newBuilder();
        if (latitude != null) {
            protoPos.setLatitude(latitude);
        }
        if (longitude != null) {
            protoPos.setLongitude(longitude);
        }
        if (altitude != null) {
            protoPos.setAltitude(altitude);
        }
        if (precision != null) {
            protoPos.setPrecision(precision);
        }
        if (heading != null) {
            protoPos.setHeading(heading);
        }
        if (speed != null) {
            protoPos.setSpeed(speed);
        }
        if (timestamp != null) {
            protoPos.setTimestamp(timestamp.getTime());
        }
        if (satellites != null) {
            protoPos.setSatellites(satellites);
        }
        if (status != null) {
            protoPos.setStatus(status);
        }
        return protoPos.build();
    }

    @Override
    public String toString() {
        // a-la JSON
        StringBuilder sb = new StringBuilder();

        sb.append("{");
        sb.append("\"longitude\":");
        sb.append(longitude);
        sb.append(", ");
        sb.append("\"latitude\":");
        sb.append(latitude);
        sb.append(", ");
        sb.append("\"altitude\":");
        sb.append(altitude);
        sb.append(", ");
        sb.append("\"precision\":");
        sb.append(precision);
        sb.append(", ");
        sb.append("\"heading\":");
        sb.append(heading);
        sb.append(", ");
        sb.append("\"speed\":");
        sb.append(speed);
        sb.append(", ");
        sb.append("\"timestamp\":");
        sb.append("\"");
        sb.append(timestamp);
        sb.append("\"");
        sb.append(", ");
        sb.append("\"satellites\":");
        sb.append(satellites);
        sb.append(", ");
        sb.append("\"status\":");
        sb.append(status);
        sb.append("}");

        return sb.toString();
    }

    public String toDisplayString() {
        ArrayList<String> list = new ArrayList<String>();
        StringBuilder sb = new StringBuilder();
        if (latitude != null) {
            list.add(new StringBuilder("latitude").append("=").append(latitude)
                     .toString());
        }
        if (longitude != null) {
            list.add(new StringBuilder("longitude").append("=")
                     .append(longitude).toString());
        }
        if (altitude != null) {
            list.add(new StringBuilder("altitude").append("=").append(altitude)
                     .toString());
        }
        if (precision != null) {
            list.add(new StringBuilder("precision").append("=")
                     .append(precision).toString());
        }
        if (heading != null) {
            list.add(new StringBuilder("heading").append("=").append(heading)
                     .toString());
        }
        if (speed != null) {
            list.add(new StringBuilder("speed").append("=").append(speed)
                     .toString());
        }
        if (timestamp != null) {
            list.add(new StringBuilder("timestamp").append("=")
                     .append(timestamp).toString());
        }
        if (satellites != null) {
            list.add(new StringBuilder("satellites").append("=")
                     .append(satellites).toString());
        }
        if (status != null) {
            list.add(new StringBuilder("status").append("=").append(status)
                     .toString());
        }

        if (list.size() == 0) {
            return null;
        }

        sb.append(list.get(0));
        for (int i = 1; i < list.size(); i++) {
            sb.append("~~").append(list.get(i));
        }

        return sb.toString();
    }
}

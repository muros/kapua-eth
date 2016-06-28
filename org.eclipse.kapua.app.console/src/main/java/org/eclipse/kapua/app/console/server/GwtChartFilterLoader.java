package org.eclipse.kapua.app.console.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.kapua.app.console.server.util.EdcExceptionHandler;
import org.eclipse.kapua.app.console.shared.GwtEdcException;
import org.eclipse.kapua.app.console.shared.model.GwtDataPoint;
import org.eclipse.kapua.app.console.shared.model.GwtHeader;
import org.eclipse.kapua.service.datastore.DataStoreService;
import org.eclipse.kapua.service.locator.ServiceLocator;

import com.eurotech.cloud.commons.model.query.EdcListResult;
import com.eurotech.cloud.commons.model.query.EdcMessageQuery;
import com.eurotech.cloud.message.EdcMessage;
import com.eurotech.cloud.message.EdcPayload;
import com.eurotech.cloud.message.EdcPosition;

public class GwtChartFilterLoader {
    private Map<String, List<GwtDataPoint>> dataPoint;
    private int                             limit;
    private Object                          keyOffset;
    private boolean                         hasNext;
    private long                            timestampOfLastInsertedMessage;

    private int                             processedMessagesCount;
    private int                             insertedMessages   = 0;

    private String                          accountName;
    private String                          topic;
    private String                          asset;
    private List<GwtHeader>                 metrics;

    private static final String             POSITION_LONGITUDE = "position_longitude";
    private static final String             POSITION_LATITUDE  = "position_latitude";
    private static final String             POSITION_ALTITUDE  = "position_altitude";
    private static final String             POSITION_PRECISION = "position_precision";
    private static final String             POSITION_HEADING   = "position_heading";
    private static final String             POSITION_SPEED     = "position_speed";
    private static final String             POSITION_TIMESTAMP = "position_timestamp";
    private static final String             POSITION_SATELLITE = "position_satellite";
    private static final String             POSITION_STATUS    = "position_status";

    public GwtChartFilterLoader() {
    }

    public GwtChartFilterLoader(String accountName, List<GwtHeader> metrics) {
        this.accountName = accountName;
        this.metrics = metrics;
    }

    public void setAsset(String asset) {
        this.asset = asset;
        this.topic = null;

    }

    public void setTopic(String topic) {
        this.topic = topic;
        this.asset = null;
    }

    public void load(EdcMessageQuery query)
    throws GwtEdcException {

        dataPoint = new HashMap<String, List<GwtDataPoint>>();
        processedMessagesCount = 0;
        limit = query.getLimit();
        hasNext = true;

        ServiceLocator locator = ServiceLocator.getInstance();
        DataStoreService dss = locator.getDataStoreService();
        EdcListResult<EdcMessage> msgs;

        try {
            while (insertedMessages < limit && hasNext == true) {

                // Do query with service bean
                if (topic != null) {
                    msgs = dss.findMessagesByTopic(accountName, topic, query);
                } else {
                    msgs = dss.findMessagesByAsset(accountName, asset, query);
                }

                // Put in result only messages with metric that we need
                // If the number of list of msgs is >= of the limit messages or gwtMsgs reach the limit of messages,
                // hasNext signals that there are more messages for this query to read.
                hasNext = populateMessagesMap(msgs, metrics, dataPoint);

                // If there are other messages but the limit in the result has not been reached move the offset to the last message fetched from Cassandra to continue query the next
                if (hasNext && (insertedMessages < limit)) {
                    keyOffset = msgs.getNextKeyOffset();
                } else {
                    // If there are other messages but the limit in the result has been reached move the offset to the timestamp of the last message inserted in result.
                    // Next query will be executed from that message (-1 is added to not read twice the message)
                    keyOffset = timestampOfLastInsertedMessage - 1;
                }

                // Move the offset
                query.setKeyOffset(keyOffset);

                // If at this point the keyOffset is null it means that there are no more message for this query
                if (keyOffset == null) {
                    hasNext = false;
                }
            }
        } catch (Throwable t) {
            EdcExceptionHandler.handle(t);
        }
    }

    public void loadWithoutPaging(EdcMessageQuery query)
    throws GwtEdcException {

        dataPoint = new HashMap<String, List<GwtDataPoint>>();
        limit = query.getLimit();

        ServiceLocator locator = ServiceLocator.getInstance();
        DataStoreService dss = locator.getDataStoreService();

        try {

            EdcListResult<EdcMessage> msgs;

            if (topic != null) {
                msgs = dss.findMessagesByTopic(accountName, topic, query);
            } else {
                msgs = dss.findMessagesByAsset(accountName, asset, query);
            }
            populateMessagesMap(msgs, metrics, dataPoint);

        } catch (Throwable t) {
            EdcExceptionHandler.handle(t);
        }
    }

    // Returns the number of msgs processed in case we hit the MESSAGE_PAGE_SIZE
    private boolean populateMessagesMap(EdcListResult<EdcMessage> msgs, List<GwtHeader> metrics, Map<String, List<GwtDataPoint>> result) {
        // build the result
        if (msgs == null || msgs.isEmpty()) {
            return false;
        }

        boolean inserted = false;
        for (EdcMessage msg : msgs) {
            EdcPayload payload = msg.getEdcPayload();
            if (payload != null) {
                EdcPosition position = payload.getPosition();

                processedMessagesCount++;

                for (GwtHeader metric : metrics) {
                    String name = metric.getName();
                    Object value = payload.getMetric(name);
                    if (value == null) {
                        // Check if it is a position metric
                        if (name.startsWith("position_") && position != null) {
                            if (name.equals(POSITION_LONGITUDE)) {
                                value = position.getLongitude();
                            } else if (name.equals(POSITION_LATITUDE)) {
                                value = position.getLatitude();
                            } else if (name.equals(POSITION_ALTITUDE)) {
                                value = position.getAltitude();
                            } else if (name.equals(POSITION_PRECISION)) {
                                value = position.getPrecision();
                            } else if (name.equals(POSITION_HEADING)) {
                                value = position.getHeading();
                            } else if (name.equals(POSITION_SPEED)) {
                                value = position.getSpeed();
                            } else if (name.equals(POSITION_TIMESTAMP)) {
                                if (position.getTimestamp() != null) {
                                    value = position.getTimestamp().getTime();
                                }
                            } else if (name.equals(POSITION_SATELLITE)) {
                                value = position.getSatellites();
                            } else if (name.equals(POSITION_STATUS)) {
                                value = position.getStatus();
                            }
                        }
                    }
                    if (value != null && (value instanceof Number)) {

                        // this message has at least one of the metrics
                        // requested so include into the result set
                        List<GwtDataPoint> gwtMsgs = (List<GwtDataPoint>) result.get(name);
                        if (gwtMsgs == null) {
                            gwtMsgs = new ArrayList<GwtDataPoint>();
                            result.put(name, gwtMsgs);
                        }
                        gwtMsgs.add(new GwtDataPoint(msg.getTimestamp().getTime(), (Number) value));

                        inserted = true;

                    }
                }

                if (inserted) {
                    insertedMessages++;
                    inserted = false;
                }

                if (insertedMessages >= limit) {
                    timestampOfLastInsertedMessage = msg.getTimestamp().getTime();
                    return true;
                }
            }
        }
        return true;
    }

    public boolean hasNext() {
        return hasNext;
    }

    public Map<String, List<GwtDataPoint>> getDataPoint() {
        return dataPoint;
    }

    public void setDataPoint(Map<String, List<GwtDataPoint>> dataPoint) {
        this.dataPoint = dataPoint;
    }

    public int getProcessedMessagesCount() {
        return processedMessagesCount;
    }

    public void setProcessedMessagesCount(int processedMessagesCount) {
        this.processedMessagesCount = processedMessagesCount;
    }

    public Object getKeyOffset() {
        return keyOffset;
    }
}

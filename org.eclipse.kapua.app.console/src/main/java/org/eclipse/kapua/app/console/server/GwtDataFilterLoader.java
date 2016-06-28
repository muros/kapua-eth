package org.eclipse.kapua.app.console.server;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.kapua.app.console.server.util.EdcExceptionHandler;
import org.eclipse.kapua.app.console.shared.GwtEdcException;
import org.eclipse.kapua.app.console.shared.model.GwtHeader;
import org.eclipse.kapua.app.console.shared.model.GwtMessage;
import org.eclipse.kapua.service.datastore.DataStoreService;
import org.eclipse.kapua.service.locator.ServiceLocator;

import com.eurotech.cloud.commons.model.query.EdcListResult;
import com.eurotech.cloud.commons.model.query.EdcMessageQuery;
import com.eurotech.cloud.message.EdcMessage;
import com.eurotech.cloud.message.EdcPayload;
import com.eurotech.cloud.message.EdcPosition;

public class GwtDataFilterLoader {
    private List<GwtMessage>    gwtMsgs;
    private int                 limit;
    private Object              keyOffset;
    private boolean             hasNext;
    private long                timestampOfLastInsertedMessage;

    private String              accountName;
    private String              topic;
    private String              asset;
    private List<GwtHeader>     metrics;

    /*
     * NOTE:
     * 
     * We need to add meta info to the GwtMessage as property of the Model.
     * Also customer metrics are stored into the GwtMessage, so there is the chance of name overlap
     * between the metric name and the meta parameter name.
     * 
     * i.e.
     *  timestamp, asset, topic
     * 
     * To avoid overlap we put a prefix that shouldn't be is use by customers like 'm&t@_'
     * 
     */
    
    private static final String TIMESTAMP          = "m&t@_timestamp";
    private static final String ASSET              = "m&t@_asset";
    private static final String TOPIC              = "m&t@_topic";
    private static final String POSITION_LONGITUDE = "position_longitude";
    private static final String POSITION_LATITUDE  = "position_latitude";
    private static final String POSITION_ALTITUDE  = "position_altitude";
    private static final String POSITION_PRECISION = "position_precision";
    private static final String POSITION_HEADING   = "position_heading";
    private static final String POSITION_SPEED     = "position_speed";
    private static final String POSITION_TIMESTAMP = "position_timestamp";
    private static final String POSITION_SATELLITE = "position_satellite";
    private static final String POSITION_STATUS    = "position_status";

    public GwtDataFilterLoader() {
    }

    public GwtDataFilterLoader(String accountName, List<GwtHeader> metrics) {
        this.accountName = accountName;
        this.metrics = metrics;
    }

    public void load(EdcMessageQuery query)
    throws GwtEdcException {

        gwtMsgs = new ArrayList<GwtMessage>();
        limit = query.getLimit();
        hasNext = true;

        ServiceLocator locator = ServiceLocator.getInstance();
        DataStoreService dss = locator.getDataStoreService();
        EdcListResult<EdcMessage> msgs;

        try {
            while (gwtMsgs.size() < limit && hasNext == true) {

                // Do query with service bean
                if (topic != null) {
                    msgs = dss.findMessagesByTopic(accountName, topic, query);
                } else {
                    msgs = dss.findMessagesByAsset(accountName, asset, query);
                }

                // Put in result only messages with metric that we need
                // If the number of list of msgs is >= of the limit messages or gwtMsgs reach the limit of messages,
                // hasNext signals that there are more messages for this query to read.
                hasNext = populateMessagesList(msgs, metrics, gwtMsgs);

                // If there are other messages but the limit in the result has not been reached move the offset to the last message fetched from Cassandra to continue query the next
                if (hasNext && (gwtMsgs.size() < limit)) {
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

    // Returns the number of msgs processed in case we hit the MESSAGE_PAGE_SIZE
    private boolean populateMessagesList(EdcListResult<EdcMessage> msgs, List<GwtHeader> metrics, List<GwtMessage> gwtMsgs) {

        if (msgs == null || msgs.isEmpty()) {
            return false;
        }

        Map<String, Object> props = null;

        for (int i = 0; i < msgs.size(); i++) {

            props = null;

            EdcMessage msg = msgs.get(i);
            EdcPayload payload = msg.getEdcPayload();

            if (payload != null) {
                EdcPosition position = payload.getPosition();

                for (GwtHeader metric : metrics) {

                    String name = metric.getName();
                    String unescapedName = metric.getUnescapedName();
                    Object value = payload.getMetric(unescapedName);

                    if (value == null) {
                        // Check if it is a position metric
                        if (unescapedName.startsWith("position_") && position != null) {
                            if (unescapedName.equals(POSITION_LONGITUDE)) {
                                value = position.getLongitude();
                            } else if (unescapedName.equals(POSITION_LATITUDE)) {
                                value = position.getLatitude();
                            } else if (unescapedName.equals(POSITION_ALTITUDE)) {
                                value = position.getAltitude();
                            } else if (unescapedName.equals(POSITION_PRECISION)) {
                                value = position.getPrecision();
                            } else if (unescapedName.equals(POSITION_HEADING)) {
                                value = position.getHeading();
                            } else if (unescapedName.equals(POSITION_SPEED)) {
                                value = position.getSpeed();
                            } else if (unescapedName.equals(POSITION_TIMESTAMP)) {
                                Date d = position.getTimestamp();
                                if (d != null)
                                    value = d.getTime();
                            } else if (unescapedName.equals(POSITION_SATELLITE)) {
                                value = position.getSatellites();
                            } else if (unescapedName.equals(POSITION_STATUS)) {
                                value = position.getStatus();
                            }
                        }
                    }
                    if (value != null) {
                        // this message has at least one of the metrics
                        // requested so include into the result set only
                        // if we haven't hit the paging limit
                        if (props == null) {
                            props = new HashMap<String, Object>();
                            props.put(TIMESTAMP, msg.getTimestamp());
                            props.put(ASSET, msg.getEdcTopic().getAsset());
                            props.put(TOPIC, msg.getEdcTopic().getSemanticTopic());
                        }

                        props.put(name, value);
                    }
                }
            }

            if (props != null) {
                GwtMessage gwtMsg = new GwtMessage();
                gwtMsg.setAllowNestedValues(false);
                gwtMsg.setProperties(props);
                gwtMsgs.add(gwtMsg);

                if (gwtMsgs.size() == limit) {
                    timestampOfLastInsertedMessage = msg.getTimestamp().getTime();
                    return true;
                }
            }
        }
        return true;
    }

    public void setAsset(String asset) {
        this.asset = asset;
        this.topic = null;
    }

    public void setTopic(String topic) {
        this.topic = topic;
        this.asset = null;
    }

    public boolean hasNext() {
        return hasNext;
    }

    public List<GwtMessage> getLoadedGwtMessages() {
        return gwtMsgs;
    }

    public Object getKeyOffset() {
        return keyOffset;
    }
}
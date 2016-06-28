package org.eclipse.kapua.message;

import java.io.IOException;
import java.util.Iterator;

public class KapuaDisconnectPayload extends KapuaPayload {
    private final static String UPTIME = "uptime";
    private final static String DISPLAY_NAME = "display_name";


    public KapuaDisconnectPayload(String uptime, String displayName) {
        super();

        addMetric(UPTIME, uptime);
        addMetric(DISPLAY_NAME, displayName);
    }

    public KapuaDisconnectPayload(KapuaPayload edcMessage) throws KapuaInvalidMessageException, IOException {
        Iterator<String> hdrIterator = edcMessage.metricsIterator();

        while (hdrIterator.hasNext()) {
            String hdrName = hdrIterator.next();
            String hdrVal = (String)edcMessage.getMetric(hdrName);

            addMetric(hdrName, hdrVal);
        }

        setBody(edcMessage.getBody());
    }

    public String getUptime() {
        return (String) getMetric(UPTIME);
    }
    public String getDisplayName() {
        return (String) getMetric(DISPLAY_NAME);
    }

    @Override
    public String toString() {
        return "EdcBirthMessage [getUptime()=" + getUptime() + ", getDisplayName()=" + getDisplayName() + "]";
    }
}

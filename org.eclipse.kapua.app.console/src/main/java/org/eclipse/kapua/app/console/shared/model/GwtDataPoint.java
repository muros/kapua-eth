package org.eclipse.kapua.app.console.shared.model;

import java.io.Serializable;

public class GwtDataPoint implements Serializable {

    private static final long serialVersionUID = -8592551619448300541L;

    private long   m_timestamp;
    private Number m_value;

    public GwtDataPoint() {
    }

    public GwtDataPoint(long timestamp, Number value) {
        m_value = value;
        m_timestamp = timestamp;
    }

    public long getTimestamp() {
        return m_timestamp;
    }

    public void setTimestamp(long timestamp) {
        m_timestamp = timestamp;
    }

    public Number getValue() {
        return m_value;
    }

    public void setValue(Number value) {
        m_value = value;
    }
}

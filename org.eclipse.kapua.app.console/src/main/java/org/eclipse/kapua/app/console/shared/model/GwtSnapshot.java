package org.eclipse.kapua.app.console.shared.model;

import java.io.Serializable;
import java.util.Date;

import org.eclipse.kapua.app.console.client.util.DateUtils;
import org.eclipse.kapua.app.console.client.util.MessageUtils;

public class GwtSnapshot extends EdcBaseModel implements Serializable {
    private static final long serialVersionUID = 204571826084819719L;


    public GwtSnapshot() {
    }

    @Override
    @SuppressWarnings({"unchecked"})
    public <X> X get(String property) {
        if ("createdOnFormatted".equals(property)) {
            if (((Date) get("createdOn")).getTime() == 0) {
                return (X) (MessageUtils.get("snapSeeded"));
            }
            return (X) (DateUtils.formatDateTime((Date) get("createdOn")));
        } else if ("snapshotId".equals(property)) {
            return (X) new Long(((Date) get("createdOn")).getTime());
        } else {
            return super.get(property);
        }
    }

    public Date getCreatedOn() {
        return (Date) get("createdOn");
    }

    public long getSnapshotId() {
        return ((Date) get("createdOn")).getTime();
    }

    public String getCreatedOnFormatted() {
        return DateUtils.formatDateTime((Date) get("createdOn"));
    }

    public void setCreatedOn(Date createdOn) {
        set("createdOn", createdOn);
    }
}

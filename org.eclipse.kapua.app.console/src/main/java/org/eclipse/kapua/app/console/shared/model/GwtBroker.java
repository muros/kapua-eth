package org.eclipse.kapua.app.console.shared.model;

import java.io.Serializable;

public class GwtBroker extends EdcBaseModel implements Serializable {

    private static final long serialVersionUID = -7953082858219194327L;

    public GwtBroker() {}


    public String getUrl() {
        return get("url");
    }

    public String getUnescapedUrl() {
        return getUnescaped("url");
    }

    public void setUrl(String url) {
        set("url", url);
    }

    public String getHost() {
        return get("host");
    }

    public void setHost(String host) {
        set("host", host);
    }

    public Integer getPort() {
        return get("port");
    }

    public void setPort(Integer port) {
        set("port", port);
    }

    public Boolean isSsl() {
        return get("ssl");
    }

    public void setSsl(Boolean ssl) {
        set("ssl", ssl);
    }

    public String toString() {
        return getUrl();
    }
}

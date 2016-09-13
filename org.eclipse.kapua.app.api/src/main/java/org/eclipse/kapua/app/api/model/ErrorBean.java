package org.eclipse.kapua.app.api.model;

import javax.ws.rs.core.Response;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "error")
@XmlAccessorType(XmlAccessType.FIELD)
public class ErrorBean 
{
    @XmlElement(name="code")
    private int    code;

    @XmlElement(name="reason")
    private String reason;

    @XmlElement(name="message")
    private String message;

    public ErrorBean() {
    }

    public ErrorBean(Response.Status status, String message) {
        this.code    = status.getStatusCode();
        this.reason  = status.toString();
        this.message = message;
    }
}

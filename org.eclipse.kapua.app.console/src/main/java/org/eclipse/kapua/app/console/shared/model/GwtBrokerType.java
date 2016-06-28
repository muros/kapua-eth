package org.eclipse.kapua.app.console.shared.model;

import java.io.Serializable;

public enum GwtBrokerType implements Serializable
{
    AMQ, MOSQUITTO, CUSTOM;
    
    GwtBrokerType()
    {
    }
}

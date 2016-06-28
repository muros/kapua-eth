package org.eclipse.kapua.app.console.servlet;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.kapua.service.device.registry.DeviceEvent;

public abstract class DeviceEventExporter {
    protected static String[]     s_deviceEventProperties = {
        "Account",
        "Client ID",
        "Received On",
        "Sent On",
        "Event Type",
        "Event Message"
    };

    protected HttpServletResponse m_response;

    protected DeviceEventExporter(HttpServletResponse response) {
        m_response = response;
    }

    public abstract void init(String account,
                              String clientId,
                              Date startDate,
                              Date endDate)
    throws ServletException, IOException;

    public abstract void append(List<DeviceEvent> deviceEvents)
    throws ServletException, IOException;

    public abstract void close()
    throws ServletException, IOException;
}

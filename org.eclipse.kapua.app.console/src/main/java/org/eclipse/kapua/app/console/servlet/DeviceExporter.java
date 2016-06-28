package org.eclipse.kapua.app.console.servlet;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.kapua.service.device.registry.DeviceOld;

public abstract class DeviceExporter {
    protected static String[]     s_deviceProperties = {
        "Account",
        // "Id",
        "Client ID",
        "Status",
        "Connection Status",
        "Created On",
        // "Created By",
        "Last Event On",
        "Last Event Type",
        "Connection IP",
        // "MQTT connection IP",
        "Display Name",
        "Serial Number",
        "IMEI",
        "IMSI",
        "ICCID",
        "Model ID",
        "Bios Version",
        "Firmware Version",
        "OS Version",
        "JVM Version",
        "OSGi Version",
        "ESF/Kura Version",
        "Application Identifiers",
        "Accept Encoding",
        "GPS Longitude",
        "GPS Latitude",
        "Custom Attribute 1",
        "Custom Attribute 2",
        "Custom Attribute 3",
        "Custom Attribute 4",
        "Custom Attribute 5",
        "Certificate Id"
        // "Optlock"
    };

    protected HttpServletResponse m_response;

    protected DeviceExporter(HttpServletResponse response) {
        m_response = response;
    }

    public abstract void init(String account)
    throws ServletException, IOException;

    public abstract void append(List<DeviceOld> messages)
    throws ServletException, IOException;

    public abstract void close()
    throws ServletException, IOException;
}

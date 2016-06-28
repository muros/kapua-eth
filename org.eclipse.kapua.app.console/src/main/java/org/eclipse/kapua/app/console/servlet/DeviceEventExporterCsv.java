/*******************************************************************************
 * Copyright (c) 2011, 2016 Eurotech and/or its affiliates
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Eurotech - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.kapua.app.console.servlet;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.kapua.service.device.registry.DeviceEvent;

import au.com.bytecode.opencsv.CSVWriter;

public class DeviceEventExporterCsv extends DeviceEventExporter
{
    private String     m_account;
    private DateFormat m_dateFormat;
    private CSVWriter  m_writer;

    public DeviceEventExporterCsv(HttpServletResponse response)
    {
        super(response);
    }

    @Override
    public void init(String account, String clientId, Date startDate, Date endDate)
        throws ServletException, IOException
    {
        m_account = account;
        m_dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss.SSS");

        OutputStreamWriter osw = new OutputStreamWriter(m_response.getOutputStream(), Charset.forName("UTF-8"));
        m_writer = new CSVWriter(osw);

        List<String> cols = new ArrayList<String>();
        for (String property : s_deviceEventProperties) {
            cols.add(property);
        }
        m_writer.writeNext(cols.toArray(new String[] {}));
    }

    @Override
    public void append(List<DeviceEvent> deviceEvents)
        throws ServletException, IOException
    {
        for (DeviceEvent deviceEvent : deviceEvents) {

            List<String> cols = new ArrayList<String>();
            cols.add(deviceEvent.getAccountName());
            cols.add(deviceEvent.getClientId());
            cols.add(m_dateFormat.format(deviceEvent.getReceivedOn()));
            if (deviceEvent.getSentOn() != null) {
                cols.add(m_dateFormat.format(deviceEvent.getSentOn()));
            }
            else {
                cols.add("");
            }
            cols.add(deviceEvent.getEventType());
            cols.add(deviceEvent.getEventMessage().replace("\n", " "));
            m_writer.writeNext(cols.toArray(new String[] {}));
        }   
    }

    @Override
    public void close()
        throws ServletException, IOException
    {
        m_response.setContentType("text/csv");
        m_response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + URLEncoder.encode(m_account, "UTF-8") + "_device_events.csv");
        m_response.setHeader("Cache-Control", "no-transform, max-age=0");
        
        m_writer.flush();
        
        m_writer.close();
    }
}

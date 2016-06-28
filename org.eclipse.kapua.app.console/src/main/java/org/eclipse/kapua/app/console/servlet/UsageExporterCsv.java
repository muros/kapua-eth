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

import au.com.bytecode.opencsv.CSVWriter;

import com.eurotech.cloud.commons.model.query.EdcUsageInfo;

public class UsageExporterCsv extends UsageExporter
{

    private String     m_account;
    private DateFormat m_dateFormat;

    private CSVWriter  m_writer;

    public UsageExporterCsv(HttpServletResponse response)
    {
        super(response);
    }

    public void init(String account,
                     boolean byHour,
                     Date startDate,
                     Date endDate)
        throws ServletException, IOException
    {
        m_account = account;

        OutputStreamWriter osw = new OutputStreamWriter(m_response.getOutputStream(), Charset.forName("UTF-8"));
        m_writer = new CSVWriter(osw);

        List<String> cols = new ArrayList<String>();
        cols.add("Timestamp (UTC)");

        m_dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        cols.add("Account");

        if (byHour) {
            cols.add("Hour");
        }

        cols.add("RX byte count");
        m_writer.writeNext(cols.toArray(new String[] {}));
    }

    @Override
    public void append(List<EdcUsageInfo> messages, boolean byHour)
        throws ServletException, IOException
    {
        SimpleDateFormat sdfHour = new SimpleDateFormat("HH");
        for (EdcUsageInfo message : messages) {

            List<String> cols = new ArrayList<String>();
            cols.add(m_dateFormat.format(message.getTimestamp()));
            if (message.getAsset() == null) {
                cols.add(message.getAccountName());
            }
            else {
                if (message.getAsset().trim().equals("")) {
                    cols.add(message.getAccountName());
                }
                else {
                    cols.add(message.getAsset());
                }
            }
            if (byHour) {

                cols.add(sdfHour.format(message.getTimestamp().getTime()));
            }

            Number num = (Number) message.getRxUsage();
            cols.add(num.toString());
            m_writer.writeNext(cols.toArray(new String[] {}));
        }

    }

    @Override
    public void close()
        throws ServletException, IOException
    {
        m_response.setContentType("text/csv; charset=UTF-8");
        m_response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + URLEncoder.encode(m_account, "UTF-8") + "_usage.csv");
        m_response.setHeader("Cache-Control", "no-transform, max-age=0");

        m_writer.flush();

        m_writer.close();
    }
}

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

import com.eurotech.cloud.message.EdcMessage;
import com.eurotech.cloud.message.EdcPayload;
import com.eurotech.cloud.message.EdcPosition;

public class DataExporterCsv extends DataExporter {
    private String              m_account;
    @SuppressWarnings("unused")
    private String              m_topic;
    @SuppressWarnings("unused")
    private String              m_asset;
    private String[]            m_headers;
    private DateFormat          m_dateFormat;
    private CSVWriter           m_writer;

    private static final String POSITION_LONGITUDE = "position_longitude";
    private static final String POSITION_LATITUDE  = "position_latitude";
    private static final String POSITION_ALTITUDE  = "position_altitude";
    private static final String POSITION_PRECISION = "position_precision";
    private static final String POSITION_HEADING   = "position_heading";
    private static final String POSITION_SPEED     = "position_speed";
    private static final String POSITION_TIMESTAMP = "position_timestamp";
    private static final String POSITION_SATELLITE = "position_satellite";
    private static final String POSITION_STATUS    = "position_status";

    public DataExporterCsv(HttpServletResponse response) {
        super(response);
    }

    public void init(String account,
                     String topic,
                     String asset,
                     Date startDate,
                     Date endDate,
                     String[] headers)
    throws ServletException, IOException {

        m_account = account;
        m_topic = topic;
        m_asset = asset;
        m_headers = headers;
        m_dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss.SSS");

        m_response.setHeader("Cache-Control", "no-transform, max-age=0");
        OutputStreamWriter osw = new OutputStreamWriter(m_response.getOutputStream(), Charset.forName("UTF-8"));
        m_writer = new CSVWriter(osw);

        List<String> cols = new ArrayList<String>();
        cols.add("Timestamp (UTC)");
        cols.add("Asset");
        cols.add("Topic");
        for (int i = 0; i < headers.length; i++) {
            cols.add(headers[i]);
        }
        m_writer.writeNext(cols.toArray(new String[] {}));
    }

    @Override
    public void append(List<EdcMessage> messages)
    throws ServletException, IOException {
        for (EdcMessage message : messages) {
            EdcPayload payload = message.getEdcPayload();
            if (payload != null) {

                // !!!! add position metrics
                EdcPosition position = payload.getPosition();

                // avoid empty rows if none of the requested headers has a value
                boolean bContainsHeader = false;
                for (String header : m_headers) {
                    if (payload.getMetric(header) != null) {
                        bContainsHeader = true;
                        break;
                    } else if (header.startsWith("position_") && position != null) {
                        bContainsHeader = true;
                        break;
                    }
                }
                if (bContainsHeader) {
                    List<String> cols = new ArrayList<String>();
                    cols.add(m_dateFormat.format(message.getTimestamp()));
                    cols.add(message.getEdcTopic().getAsset());
                    cols.add(message.getEdcTopic().getSemanticTopic());

                    for (String header : m_headers) {
                        Object value = payload.getMetric(header);
                        if (value == null) {
                            // Check if it is a position metric
                            if (header.startsWith("position_") && position != null) {
                                if (header.equals(POSITION_LONGITUDE)) {
                                    value = position.getLongitude();
                                } else if (header.equals(POSITION_LATITUDE)) {
                                    value = position.getLatitude();
                                } else if (header.equals(POSITION_ALTITUDE)) {
                                    value = position.getAltitude();
                                } else if (header.equals(POSITION_PRECISION)) {
                                    value = position.getPrecision();
                                } else if (header.equals(POSITION_HEADING)) {
                                    value = position.getHeading();
                                } else if (header.equals(POSITION_SPEED)) {
                                    value = position.getSpeed();
                                } else if (header.equals(POSITION_TIMESTAMP)) {
                                    if (position.getTimestamp() != null) {
                                        value = position.getTimestamp().getTime();
                                    }
                                } else if (header.equals(POSITION_SATELLITE)) {
                                    value = position.getSatellites();
                                } else if (header.equals(POSITION_STATUS)) {
                                    value = position.getStatus();
                                }
                            }
                        }
                        if (value != null) {
                            if (Number.class.isAssignableFrom(value.getClass())) {
                                Number num = (Number) value;
                                cols.add(num.toString());
                            } else if (String.class.isAssignableFrom(value.getClass())) {
                                String str = (String) value;
                                cols.add(str.replace("\n", " "));
                            } else if (Boolean.class.isAssignableFrom(value.getClass())) {
                                Boolean bool = (Boolean) value;
                                cols.add(bool.toString());
                            } else {
                                cols.add("");
                            }
                        } else {
                            cols.add("");
                        }
                    }
                    m_writer.writeNext(cols.toArray(new String[] {}));
                }
            }
        }
    }

    @Override
    public void close()
        throws ServletException, IOException
    {
        m_response.setContentType("text/csv");
        m_response.setCharacterEncoding("UTF-8");
        m_response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + URLEncoder.encode(m_account, "UTF-8") + "_data.csv");
        
        m_writer.flush();
        
        m_writer.close();
    }
}

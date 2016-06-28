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
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eurotech.cloud.message.EdcMessage;
import com.eurotech.cloud.message.EdcPayload;
import com.eurotech.cloud.message.EdcPosition;

public class DataExporterExcel extends DataExporter {

    private static final Logger s_logger           = LoggerFactory.getLogger(DataExporterExcel.class);

    private String              m_account;
    private String              m_topic;
    private String              m_asset;
    private String[]            m_headers;

    private Workbook            m_workbook;
    private Sheet               m_sheet;
    private CellStyle           m_dateStyle;
    private int                 m_rowCount;

    private static final String POSITION_LONGITUDE = "position_longitude";
    private static final String POSITION_LATITUDE  = "position_latitude";
    private static final String POSITION_ALTITUDE  = "position_altitude";
    private static final String POSITION_PRECISION = "position_precision";
    private static final String POSITION_HEADING   = "position_heading";
    private static final String POSITION_SPEED     = "position_speed";
    private static final String POSITION_TIMESTAMP = "position_timestamp";
    private static final String POSITION_SATELLITE = "position_satellite";
    private static final String POSITION_STATUS    = "position_status";

    private static final short  MAX_COLS           = 255;
    private static final int    MAX_ROWS           = 65535;
    private static final int    MAX_CHAR           = 32767;

    public DataExporterExcel(HttpServletResponse response) {
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

        String assetName = m_asset != null ? escapeName(m_asset) : "device";
        String sheetName = m_topic != null ? escapeName(m_topic) : assetName;

        // workbook
        m_workbook = new HSSFWorkbook();
        m_sheet = m_workbook.createSheet(sheetName);
        CreationHelper createHelper = m_workbook.getCreationHelper();
        m_dateStyle = m_workbook.createCellStyle();
        m_dateStyle.setDataFormat(createHelper.createDataFormat().getFormat("mm/dd/yyyy hh:mm:ss.0"));

        // headers
        m_rowCount = 0;
        Row row = m_sheet.createRow(m_rowCount++);

        int iColCount = 0;
        m_sheet.setColumnWidth(iColCount, 22 * 256);
        row.createCell(iColCount++).setCellValue("Timestamp (UTC)");

        m_sheet.setColumnWidth(iColCount, 22 * 256);
        row.createCell(iColCount++).setCellValue("Asset");

        m_sheet.setColumnWidth(iColCount, 22 * 256);
        row.createCell(iColCount++).setCellValue("Topic");

        int columns = headers.length;
        if (headers.length >= MAX_COLS) {
            columns = MAX_COLS - 3; // 3 columns already exists
            s_logger.warn("Truncated {} columns from result.", headers.length - columns);
        }

        for (int i = 0; i < columns; i++) {
            m_sheet.setColumnWidth(iColCount, 12 * 256);
            row.createCell(iColCount++).setCellValue(truncate(headers[i]));
        }
    }

    @Override
    public void append(List<EdcMessage> messages)
    throws ServletException, IOException {

        Row row = null;
        Cell cell = null;
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

                    int iColCount = 0;
                    row = m_sheet.createRow(m_rowCount++);
                    cell = row.createCell(iColCount++);
                    cell.setCellStyle(m_dateStyle);
                    cell.setCellValue(message.getTimestamp());
                    row.createCell(iColCount++).setCellValue(truncate(message.getEdcTopic().getAsset()));
                    row.createCell(iColCount++).setCellValue(truncate(message.getEdcTopic().getSemanticTopic()));

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
                                row.createCell(iColCount).setCellValue(num.doubleValue());
                            } else if (String.class.isAssignableFrom(value.getClass())) {
                                String str = (String) value;
                                row.createCell(iColCount).setCellValue(truncate(str));
                            } else if (Boolean.class.isAssignableFrom(value.getClass())) {
                                Boolean bool = (Boolean) value;
                                row.createCell(iColCount).setCellValue(bool);
                            }
                        }
                        iColCount++;

                        if (iColCount >= MAX_COLS) {
                            return;
                        }
                        if (m_rowCount >= MAX_ROWS) {
                            s_logger.warn("Truncated file at {} rows. Max rows limit reached.", MAX_ROWS - 1);
                            return;
                        }
                    }
                }
            }
        }
    }

    @Override
    public void close()
    throws ServletException, IOException {
        // Write the output
        m_response.setContentType("application/vnd.ms-excel");
        m_response.setHeader("Content-Disposition", "attachment; filename=" + m_account + "_data.xls");
        m_response.setHeader("Cache-Control", "no-transform, max-age=0");
        m_workbook.write(m_response.getOutputStream());
    }

    private String escapeName(String name) {
        name = name.replace(":", "-");
        name = name.replace("\\", "-");
        name = name.replace("*", "-");
        name = name.replace("?", "-");
        name = name.replace("/", "-");
        name = name.replace("[", "-");
        name = name.replace("]", "-");

        return name;
    }

    private String truncate(String cellValue) {

        if (cellValue.length() > MAX_CHAR)
            return cellValue.substring(0, MAX_CHAR);
        else
            return cellValue;
    }
}

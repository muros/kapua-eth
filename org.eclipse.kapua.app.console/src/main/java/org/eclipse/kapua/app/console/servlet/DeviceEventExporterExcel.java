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
import org.eclipse.kapua.service.device.registry.DeviceEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeviceEventExporterExcel extends DeviceEventExporter {
    private static final Logger s_logger = LoggerFactory.getLogger(DeviceEventExporterExcel.class);

    private String              m_account;
    private String              m_clientId;

    private Workbook            m_workbook;
    private Sheet               m_sheet;
    private CellStyle           m_dateStyle;
    private short               m_rowCount;

    private static final short  MAX_COLS = 255;
    private static final int    MAX_ROWS = 65535;
    private static final int    MAX_CHAR = 32767;

    public DeviceEventExporterExcel(HttpServletResponse response) {
        super(response);
    }

    @Override
    public void init(String account,
                     String clientId,
                     Date startDate,
                     Date endDate)
    throws ServletException, IOException {
        m_account = account;
        m_clientId = clientId;
        if (m_clientId != null) {
            // replace ":" that is invalid in Excel with "-"
            m_clientId = m_clientId.replace(":", "-");
        }

        // workbook
        m_workbook = new HSSFWorkbook();
        m_sheet = m_workbook.createSheet(m_clientId);
        CreationHelper createHelper = m_workbook.getCreationHelper();
        m_dateStyle = m_workbook.createCellStyle();
        m_dateStyle.setDataFormat(createHelper.createDataFormat().getFormat("mm/dd/yyyy hh:mm:ss.0"));

        // headers
        m_rowCount = 0;
        Row row = m_sheet.createRow(m_rowCount++);

        int columns = s_deviceEventProperties.length;
        if (s_deviceEventProperties.length >= MAX_COLS) {
            columns = MAX_COLS;
            s_logger.warn("Truncated {} columns from result.", s_deviceEventProperties.length - columns);
        }

        int iColCount = 0;
        for (int i = 0; i < columns; i++) {
            m_sheet.setColumnWidth(iColCount, 22 * 256);
            row.createCell(iColCount++).setCellValue(truncate(s_deviceEventProperties[i]));
        }
    }

    @Override
    public void append(List<DeviceEvent> deviceEvents)
    throws ServletException, IOException {
        Row row = null;
        Cell cell = null;
        for (DeviceEvent deviceEvent : deviceEvents) {

            int iColCount = 0;
            row = m_sheet.createRow(m_rowCount++);

            row.createCell(iColCount++).setCellValue(truncate(deviceEvent.getAccountName()));
            row.createCell(iColCount++).setCellValue(truncate(deviceEvent.getClientId()));

            cell = row.createCell(iColCount++);
            cell.setCellStyle(m_dateStyle);
            cell.setCellValue(deviceEvent.getReceivedOn());

            cell = row.createCell(iColCount++);
            cell.setCellStyle(m_dateStyle);
            if (deviceEvent.getSentOn() != null) {
                cell.setCellValue(deviceEvent.getSentOn());
            }

            row.createCell(iColCount++).setCellValue(truncate(deviceEvent.getEventType()));
            row.createCell(iColCount++).setCellValue(truncate(deviceEvent.getEventMessage()));

            if (iColCount >= MAX_COLS) {
                return;
            }
            if (m_rowCount >= MAX_ROWS) {
                s_logger.warn("Truncated file at {} rows. Max rows limit reached.", MAX_ROWS - 1);
                return;
            }
        }
    }

    @Override
    public void close()
    throws ServletException, IOException {
        // Write the output
        m_response.setContentType("application/vnd.ms-excel");
        m_response.setHeader("Content-Disposition", "attachment; filename=" + m_account + "_device_events.xls");
        m_response.setHeader("Cache-Control", "no-transform, max-age=0");
        m_workbook.write(m_response.getOutputStream());
    }

    private String truncate(String cellValue) {
        if (cellValue.length() > MAX_CHAR)
            return cellValue.substring(0, MAX_CHAR);
        else
            return cellValue;

    }
}

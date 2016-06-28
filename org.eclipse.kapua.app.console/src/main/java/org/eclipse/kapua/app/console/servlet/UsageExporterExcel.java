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
import java.text.SimpleDateFormat;
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

import com.eurotech.cloud.commons.model.query.EdcUsageInfo;

public class UsageExporterExcel extends UsageExporter {
    private static final Logger s_logger = LoggerFactory.getLogger(UsageExporterExcel.class);

    private String              m_account;

    private Workbook            m_workbook;
    private Sheet               m_sheet;
    private CellStyle           m_dateStyle;
    private short               m_rowCount;

    private static final int    MAX_ROWS = 65535;
    private static final int    MAX_CHAR = 32767;

    public UsageExporterExcel(HttpServletResponse response) {
        super(response);
    }

    public void init(String account,
                     boolean byHour,
                     Date startDate,
                     Date endDate)
    throws ServletException, IOException {
        m_account = account;

        String sheetName = byHour ? "Usage By Hour" : "Usage By Day";

        // workbook
        m_workbook = new HSSFWorkbook();
        m_sheet = m_workbook.createSheet(sheetName);
        CreationHelper createHelper = m_workbook.getCreationHelper();
        m_dateStyle = m_workbook.createCellStyle();
        m_dateStyle.setDataFormat(createHelper.createDataFormat().getFormat("yyyy/mm/dd"));

        // headers
        m_rowCount = 0;
        Row row = m_sheet.createRow(m_rowCount++);

        int iColCount = 0;
        m_sheet.setColumnWidth(iColCount, 22 * 256);
        row.createCell(iColCount++).setCellValue("Timestamp (UTC)");

        m_sheet.setColumnWidth(iColCount, 22 * 256);
        row.createCell(iColCount++).setCellValue("Account");

        if (byHour) {
            m_sheet.setColumnWidth(iColCount, 22 * 256);
            row.createCell(iColCount++).setCellValue("Hour");
        }

        m_sheet.setColumnWidth(iColCount, 22 * 256);
        row.createCell(iColCount++).setCellValue("Byte count");
    }

    @Override
    public void append(List<EdcUsageInfo> messages, boolean byHour)
    throws ServletException, IOException {
        Row row = null;
        Cell cell = null;
        SimpleDateFormat sdfHour = new SimpleDateFormat("HH");

        for (EdcUsageInfo message : messages) {
            int iColCount = 0;
            row = m_sheet.createRow(m_rowCount++);
            cell = row.createCell(iColCount++);
            cell.setCellStyle(m_dateStyle);
            cell.setCellValue(message.getTimestamp());
            if (message.getAsset() == null) {
                row.createCell(iColCount++).setCellValue(truncate(message.getAccountName()));
            } else {
                if (message.getAsset().trim().equals("")) {
                    row.createCell(iColCount++).setCellValue(truncate(message.getAccountName()));
                } else {
                    row.createCell(iColCount++).setCellValue(truncate(message.getAsset()));
                }
            }

            if (byHour) {

                row.createCell(iColCount++).setCellValue(sdfHour.format(message.getTimestamp().getTime()));
            }

            row.createCell(iColCount++).setCellValue(message.getRxUsage());

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
        m_response.setHeader("Content-Disposition", "attachment; filename=" + m_account + "_usage.xls");
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

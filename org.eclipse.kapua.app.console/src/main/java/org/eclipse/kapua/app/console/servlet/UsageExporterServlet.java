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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.kapua.KapuaEntityNotFoundException;
import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.KapuaIllegalAccessException;
import org.eclipse.kapua.KapuaUnauthenticatedException;
import org.eclipse.kapua.service.config.EdcConfig;
import org.eclipse.kapua.service.locator.ServiceLocator;
import org.eclipse.kapua.service.usage.AccountUsageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eurotech.cloud.commons.model.query.EdcListResult;
import com.eurotech.cloud.commons.model.query.EdcUsageInfo;
import com.eurotech.cloud.commons.model.query.EdcUsageQuery;

public class UsageExporterServlet extends HttpServlet
{
    private static final long serialVersionUID = -5731996767405493116L;
    private static Logger     s_logger         = LoggerFactory.getLogger(UsageExporterServlet.class);

    public void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException
    {
        try {

         // parameter extraction
            String format = request.getParameter("format");
            String account = request.getParameter("account");
            String start = request.getParameter("startDate");
            String end = request.getParameter("endDate");
            String byDayString = request.getParameter("day");

            // data exporter
            UsageExporter usageExporter = null;
            if ("xls".equals(format)) {
                usageExporter = new UsageExporterExcel(response);
            }
            else if ("csv".equals(format)) {
                usageExporter = new UsageExporterCsv(response);
            }
            else {
                throw new IllegalArgumentException("format");
            }

            if (account == null || account.isEmpty()) {
                throw new IllegalArgumentException("account");
            }

            if (start == null || start.isEmpty()) {
                throw new IllegalArgumentException("startDate");
            }

            Date startDate;
            try {
                startDate = new Date(Long.parseLong(start));
            }
            catch (NumberFormatException nfe) {
                throw new IllegalArgumentException("startDate");
            }

            if (end == null || end.isEmpty()) {
                throw new IllegalArgumentException("endDate");
            }

            Date endDate;
            try {
                endDate = new Date(Long.parseLong(end));
            }
            catch (NumberFormatException nfe) {
                throw new IllegalArgumentException("endDate");
            }

            if (byDayString == null || byDayString.isEmpty()) {
                throw new IllegalArgumentException("day");
            }

            boolean byDay;
            try {
                byDay = Boolean.parseBoolean(byDayString);
            }
            catch (NumberFormatException nfe) {
                throw new IllegalArgumentException("day");
            }

            // get the messages and append them to the exporter
            if (byDay) {
                usageExporter.init(account,
                                   false, // FIXME: Inverse logic. byHour=false => byDay
                                   startDate,
                                   endDate);

                findUsageByAccountByDay(account,
                                        startDate,
                                        endDate,
                                        usageExporter);
            }
            else {
                usageExporter.init(account,
                                   true,
                                   startDate,
                                   endDate);

                findUsageByAccount(account,
                                   startDate,
                                   endDate,
                                   usageExporter);
            }

            // Close things up
            usageExporter.close();
        }
        catch (IllegalArgumentException iae) {
            response.sendError(400, "Illegal value for query parameter: " + iae.getMessage());
            return;
        }
        catch (KapuaEntityNotFoundException eenfe) {
            response.sendError(400, eenfe.getMessage());
            return;
        }
        catch (KapuaUnauthenticatedException eiae) {
            response.sendError(401, eiae.getMessage());
            return;
        }
        catch (KapuaIllegalAccessException eiae) {
            response.sendError(403, eiae.getMessage());
            return;
        }
        catch (Exception e) {
            s_logger.error("Error creating usage export", e);
            throw new ServletException(e);
        }
    }

    // Gets usage by account by HOUR
    private void findUsageByAccount(String accountName,
                                    Date startDate,
                                    Date endDate,
                                    UsageExporter usageExporter)
        throws KapuaException, ServletException, IOException
    {
        ServiceLocator locator = ServiceLocator.getInstance();
        AccountUsageService accountUsageService = locator.getAccountUsageService();

        int resultsCount = 0;
        int maxCount = EdcConfig.getInstance().getDataExportMaxCount();
        Object offset = null;

        // paginate through the matching message
        EdcListResult<EdcUsageInfo> results = null;
        EdcUsageQuery query = new EdcUsageQuery()
                                                 .setLimit(250)
                                                 .setDateRange(startDate.getTime(), endDate.getTime());

        query.setTimetype(AccountUsageService.TimeType.HOUR);
        do {

            query.setKeyOffset(offset);
            results = accountUsageService.findUsageByAccount(accountName, query);

            usageExporter.append(results, true); // true => byHour

            offset = results.nextKeyOffset();
            resultsCount += results.size();
        }
        while (offset != null && resultsCount < maxCount);
    }

    private void findUsageByAccountByDay(String accountName,
                                         Date startDate,
                                         Date endDate,
                                         UsageExporter usageExporter)
        throws KapuaException, ServletException, IOException
    {
        ServiceLocator locator = ServiceLocator.getInstance();
        AccountUsageService accountUsageService = locator.getAccountUsageService();

        int resultsCount = 0;
        int maxCount = EdcConfig.getInstance().getDataExportMaxCount();
        Object offset = null;

        // paginate through the matching message
        EdcListResult<EdcUsageInfo> results = null;
        EdcUsageQuery query = new EdcUsageQuery()
                                                 .setLimit(250)
                                                 .setDateRange(startDate.getTime(), endDate.getTime());

        query.setTimetype(AccountUsageService.TimeType.DAY);
        do {
            query.setKeyOffset(offset);
            results = accountUsageService.findUsageByAccount(accountName, query);

            usageExporter.append(results, false); // false => byDay

            offset = results.nextKeyOffset();
            resultsCount += results.size();
        }
        while (offset != null && resultsCount < maxCount);
    }
}

package org.eclipse.kapua.app.console.servlet;

import java.io.IOException;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.kapua.KapuaEntityNotFoundException;
import org.eclipse.kapua.KapuaIllegalAccessException;
import org.eclipse.kapua.KapuaUnauthenticatedException;
import org.eclipse.kapua.service.config.EdcConfig;
import org.eclipse.kapua.service.device.registry.DeviceEvent;
import org.eclipse.kapua.service.device.registry.DeviceEventService;
import org.eclipse.kapua.service.locator.ServiceLocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eurotech.cloud.commons.model.query.EdcDeviceEventQuery;
import com.eurotech.cloud.commons.model.query.EdcListResult;

public class DeviceEventExporterServlet extends HttpServlet
{
    private static final long serialVersionUID = -2533869595709953567L;
    private static Logger     s_logger         = LoggerFactory.getLogger(DataExporterServlet.class);

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException
    {
        String reqPathInfo = request.getPathInfo();
        if (reqPathInfo != null) {
            response.sendError(404);
            return;
        }

        internalDoGet(request, response);
    }
    
    private void internalDoGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException
    {
        try {
            // parameter extraction
            String format = request.getParameter("format");
            String account = request.getParameter("account");
            String clientId = request.getParameter("clientId");
            String start = request.getParameter("startDate");
            String end = request.getParameter("endDate");
            Date startDate = new Date(Long.parseLong(start));
            Date endDate = new Date(Long.parseLong(end));

            // data exporter
            DeviceEventExporter eventExporter = null;
            if ("xls".equals(format)) {
                eventExporter = new DeviceEventExporterExcel(response);
            }
            else if ("csv".equals(format)) {
                eventExporter = new DeviceEventExporterCsv(response);
            }
            if (eventExporter == null) {
                throw new IllegalArgumentException("format");
            }

            if (account == null || account.isEmpty()) {
                throw new IllegalArgumentException("account");
            }
            
            if (clientId == null || clientId.isEmpty()) {
                throw new IllegalArgumentException("clientId");
            }
            
            eventExporter.init(account,
                               clientId,
                               startDate,
                               endDate);

            //
            // get the device events and append them to the exporter
            ServiceLocator locator = ServiceLocator.getInstance();
            DeviceEventService des = locator.getDeviceEventService();

            int resultsCount = 0;
            int maxCount = EdcConfig.getInstance().getDataExportMaxCount();
            Object offset = null;

            // paginate through the matching message
            EdcListResult<DeviceEvent> results = null;
            EdcDeviceEventQuery query = new EdcDeviceEventQuery()
                                                                 .setLimit(250)
                                                                 .setDateRange(startDate.getTime(), endDate.getTime());
            do {
                query.setKeyOffset(offset);
                results = des.findDeviceEvents(account, clientId, query);

                eventExporter.append(results);

                offset = results.nextKeyOffset();
                resultsCount += results.size();
            }
            while (offset != null && resultsCount < maxCount);

            // Close things up
            eventExporter.close();
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
            s_logger.error("Error creating device event export", e);
            throw new ServletException(e);
        }
    }
}

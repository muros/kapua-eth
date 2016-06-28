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
import org.eclipse.kapua.app.console.shared.GwtEdcException;
import org.eclipse.kapua.service.config.EdcConfig;
import org.eclipse.kapua.service.datastore.DataStoreService;
import org.eclipse.kapua.service.locator.ServiceLocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eurotech.cloud.commons.model.query.EdcListResult;
import com.eurotech.cloud.commons.model.query.EdcMessageFetchStyle;
import com.eurotech.cloud.commons.model.query.EdcMessageQuery;
import com.eurotech.cloud.message.EdcMessage;

public class DataExporterServlet extends HttpServlet
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
            String topic = request.getParameter("topic");
            String asset = request.getParameter("asset");
            String[] headers = request.getParameter("headers").split(",");
            String start = request.getParameter("startDate");
            String end = request.getParameter("endDate");
            Date startDate = new Date(Long.parseLong(start));
            Date endDate = new Date(Long.parseLong(end));

            // data exporter
            DataExporter dataExporter = null;
            if ("xls".equals(format)) {
                dataExporter = new DataExporterExcel(response);
            }
            else if ("csv".equals(format)) {
                dataExporter = new DataExporterCsv(response);
            }
            else {
                throw new IllegalArgumentException("format");
            }

            if (account == null || account.isEmpty()) {
                throw new IllegalArgumentException("account");
            }

            dataExporter.init(account,
                              topic,
                              asset,
                              startDate,
                              endDate,
                              headers);

            // get the messages and append them to the exporter
            if (asset != null) {
                findMessagesByAsset(account,
                                    asset,
                                    startDate,
                                    endDate,
                                    dataExporter);
            }
            else if (topic != null) {
                findMessagesByTopic(account,
                                    topic,
                                    startDate,
                                    endDate,
                                    dataExporter);
            }

            // Close things up
            dataExporter.close();
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
            s_logger.error("Error creating data export", e);
            throw new ServletException(e);
        }
    }

    private void findMessagesByTopic(String account,
                                     String topic,
                                     Date startDate,
                                     Date endDate,
                                     DataExporter dataExporter)
        throws KapuaException, ServletException, IOException, GwtEdcException
    {
        // build the appropriate topicQuery: <accountName>/+/<semantic_topic>
        StringBuilder topicQuery = new StringBuilder();
        topicQuery.append(account)
                  .append("/+/")
                  .append(topic);

        ServiceLocator locator = ServiceLocator.getInstance();
        DataStoreService dss = locator.getDataStoreService();

        int resultsCount = 0;
        int maxCount = EdcConfig.getInstance().getDataExportMaxCount();
        Object offset = null;

        // paginate through the matching message
        EdcListResult<EdcMessage> results = null;
        EdcMessageQuery query = new EdcMessageQuery().setLimit(250)
                                                     .setDateRange(startDate.getTime(), endDate.getTime())
                                                     .setFetchStyle(EdcMessageFetchStyle.METADATA_HEADERS);
        do {
            query.setKeyOffset(offset);

            results = dss.findMessagesByTopic(account, topicQuery.toString(), query);

            dataExporter.append(results);

            offset = results.nextKeyOffset();
            resultsCount += results.size();
        }
        while (offset != null && resultsCount < maxCount);
    }

    private void findMessagesByAsset(String account,
                                     String asset,
                                     Date startDate,
                                     Date endDate,
                                     DataExporter dataExporter)
        throws KapuaException, ServletException, IOException, GwtEdcException
    {
        ServiceLocator locator = ServiceLocator.getInstance();
        DataStoreService dss = locator.getDataStoreService();

        int resultsCount = 0;
        int maxCount = EdcConfig.getInstance().getDataExportMaxCount();
        Object offset = null;

        // paginate through the matching message
        EdcListResult<EdcMessage> results = null;
        EdcMessageQuery query = new EdcMessageQuery().setLimit(250)
                                                     .setDateRange(startDate.getTime(), endDate.getTime())
                                                     .setFetchStyle(EdcMessageFetchStyle.METADATA_HEADERS);
        do {
            query.setKeyOffset(offset);

            results = dss.findMessagesByAsset(account, asset, query);

            dataExporter.append(results);

            offset = results.nextKeyOffset();
            resultsCount += results.size();
        }
        while (offset != null && resultsCount < maxCount);
    }
}

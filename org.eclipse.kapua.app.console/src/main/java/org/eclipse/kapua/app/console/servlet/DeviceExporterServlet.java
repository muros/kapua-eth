package org.eclipse.kapua.app.console.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.kapua.KapuaEntityNotFoundException;
import org.eclipse.kapua.KapuaIllegalAccessException;
import org.eclipse.kapua.KapuaUnauthenticatedException;
import org.eclipse.kapua.service.account.AccountServiceOld;
import org.eclipse.kapua.service.config.EdcConfig;
import org.eclipse.kapua.service.device.registry.DeviceOld;
import org.eclipse.kapua.service.device.registry.DeviceConnectionStatus;
import org.eclipse.kapua.service.device.registry.DeviceRegistryServiceOld;
import org.eclipse.kapua.service.device.registry.DeviceStatus;
import org.eclipse.kapua.service.locator.ServiceLocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eurotech.cloud.commons.model.query.AndPredicate;
import com.eurotech.cloud.commons.model.query.AttributeSortCriteria.SortOrder;
import com.eurotech.cloud.commons.model.query.DeviceQueryOld;
import com.eurotech.cloud.commons.model.query.EdcListResult;

public class DeviceExporterServlet extends HttpServlet
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

            // data exporter
            DeviceExporter deviceExporter = null;
            if ("xls".equals(format)) {
                deviceExporter = new DeviceExporterExcel(response);
            }
            else if ("csv".equals(format)) {
                deviceExporter = new DeviceExporterCsv(response);
            }
            else {
                throw new IllegalArgumentException("format");
            }

            if (account == null || account.isEmpty()) {
                throw new IllegalArgumentException("account");
            }

            deviceExporter.init(account);

            //
            // get the devices and append them to the exporter
            ServiceLocator locator = ServiceLocator.getInstance();
            AccountServiceOld acs = locator.getAccountService();
            DeviceRegistryServiceOld drs = locator.getDeviceRegistryService();

            long accountId = acs.getAccountId(account);
            int resultsCount = 0;
            int maxCount = EdcConfig.getInstance().getDataExportMaxCount();
            int offset = 0;

            // paginate through the matching message
            DeviceQueryOld dq = new DeviceQueryOld();
            dq.setLimit(250);

            // Inserting filter parameter if specified
            AndPredicate andPred = new AndPredicate();

            String tag = request.getParameter("tag");
            if (tag != null && !tag.isEmpty()) {
                Long tagId = Long.valueOf(tag);
                andPred = andPred.and(dq.createTagPredicate(tagId));
            }

            String clientId = request.getParameter("clientId");
            if (clientId != null && !clientId.isEmpty()) {
                andPred = andPred.and(dq.createClientIdPredicate(clientId));
            }

            String displayName = request.getParameter("displayName");
            if (displayName != null && !displayName.isEmpty()) {
                andPred = andPred.and(dq.createDisplayNamePredicate(displayName));
            }

            String serialNumber = request.getParameter("serialNumber");
            if (serialNumber != null && !serialNumber.isEmpty()) {
                andPred = andPred.and(dq.createSerialNumberPredicate(serialNumber));
            }

            String deviceStatus = request.getParameter("deviceStatus");
            if (deviceStatus != null && !deviceStatus.isEmpty()) {
                andPred = andPred.and(dq.createDeviceStatusPredicate(DeviceStatus.valueOf(deviceStatus)));
            }

            String deviceConnectionStatus = request.getParameter("deviceConnectionStatus");
            if (deviceConnectionStatus != null && !deviceConnectionStatus.isEmpty()) {
                andPred = andPred.and(dq.createDeviceConnectionStatusPredicate(DeviceConnectionStatus.valueOf(deviceConnectionStatus)));
            }

            String esfVersion = request.getParameter("esfVersion");
            if (esfVersion != null && !esfVersion.isEmpty()) {
                andPred = andPred.and(dq.createEsfVersionPredicate(esfVersion));
            }

            String applicationIdentifiers = request.getParameter("applicationIdentifiers");
            if (applicationIdentifiers != null && !applicationIdentifiers.isEmpty()) {
                andPred = andPred.and(dq.createApplicationIdentifiersPredicate(applicationIdentifiers));
            }

            String imei = request.getParameter("imei");
            if (imei != null && !imei.isEmpty()) {
                andPred = andPred.and(dq.createImeiPredicate(imei));
            }

            String imsi = request.getParameter("imsi");
            if (imsi != null && !imsi.isEmpty()) {
                andPred = andPred.and(dq.createImsiPredicate(imsi));
            }

            String iccid = request.getParameter("iccid");
            if (iccid != null && !iccid.isEmpty()) {
                andPred = andPred.and(dq.createIccidPredicate(iccid));
            }

            String customAttribute1 = request.getParameter("customAttribute1");
            if (customAttribute1 != null && !customAttribute1.isEmpty()) {
                andPred = andPred.and(dq.createCustomAttribute1Predicate(customAttribute1));
            }

            String customAttribute2 = request.getParameter("customAttribute2");
            if (customAttribute2 != null && !customAttribute2.isEmpty()) {
                andPred = andPred.and(dq.createCustomAttribute2Predicate(customAttribute2));
            }

            String sortAttribute = request.getParameter("sortAttribute");
            if (sortAttribute != null && !sortAttribute.isEmpty()) {

                String sortOrder = request.getParameter("sortOrder");
                SortOrder sortOrderEnum;
                if (sortOrder != null && !sortOrder.isEmpty()) {
                    sortOrderEnum = SortOrder.valueOf(sortOrder);
                }
                else {
                    sortOrderEnum = SortOrder.ASCENDING;
                }

                if (sortAttribute.compareTo("CLIENT_ID") == 0) {
                    dq.setSortCriteria(dq.createClientIdSortCriteria(sortOrderEnum));
                }
                else if (sortAttribute.compareTo("DISPLAY_NAME") == 0) {
                    dq.setSortCriteria(dq.createDisplayNameSortCriteria(sortOrderEnum));
                }
                else if (sortAttribute.compareTo("LAST_EVENT_ON") == 0) {
                    dq.setSortCriteria(dq.createLastEventOnCriteria(sortOrderEnum));
                }
            }

            dq.setPredicate(andPred);

            EdcListResult<DeviceOld> results = null;
            do {

                dq.setIndexOffset(offset);
                results = drs.findDevices(accountId, dq);

                deviceExporter.append(results);

                offset += results.size();
                resultsCount += results.size();
            }
            while (results.size() > 0 && resultsCount < maxCount);

            // Close things up
            deviceExporter.close();
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
            s_logger.error("Error creating device export", e);
            throw new ServletException(e);
        }
    }
}

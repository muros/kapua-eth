package org.eclipse.kapua.app.console.servlet;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.eclipse.kapua.KapuaEntityNotFoundException;
import org.eclipse.kapua.KapuaErrorCode;
import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.KapuaIllegalAccessException;
import org.eclipse.kapua.KapuaIllegalArgumentException;
import org.eclipse.kapua.KapuaIllegalNullArgumentException;
import org.eclipse.kapua.KapuaUnauthenticatedException;
import org.eclipse.kapua.app.console.server.KapuaRemoteServiceServlet;
import org.eclipse.kapua.app.console.shared.model.KapuaFormFields;
import org.eclipse.kapua.app.console.shared.model.GwtXSRFToken;
import org.eclipse.kapua.service.device.job.DeviceJobCreator;
import org.eclipse.kapua.service.device.job.DeviceJobService;
import org.eclipse.kapua.service.device.job.DeviceJobType;
import org.eclipse.kapua.service.device.job.internal.SoftwareInstallV2DeviceJobCreator;
import org.eclipse.kapua.service.device.job.internal.SoftwareUninstallV2DeviceJobCreator;
import org.eclipse.kapua.service.job.JobAttachment;
import org.eclipse.kapua.service.job.JobAttachmentCreator;
import org.eclipse.kapua.service.job.JobAttachmentType;
import org.eclipse.kapua.service.locator.ServiceLocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeviceJobServlet extends EdcHttpServlet
{

    private static final long serialVersionUID = -5016170117606322129L;
    private static Logger     s_logger         = LoggerFactory.getLogger(DeviceJobServlet.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException
    {
        String reqPathInfo = req.getPathInfo();
        if (reqPathInfo == null) {
            resp.sendError(404);
            return;
        }

        if (reqPathInfo.equals("/attachment")) {
            doGetAttachment(req, resp);
        }
        else {
            resp.sendError(404);
            return;
        }
    }

    private void doGetAttachment(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException
    {

        try {
            String accountIdString = req.getParameter("accountId");
            String jobIdString = req.getParameter("jobId");
            
            // Parsing parameters
            long accountId;
            if (accountIdString == null || accountIdString.isEmpty()) {
                throw new IllegalArgumentException("accountId");
            }
            
            try {
                accountId = Long.parseLong(req.getParameter("accountId"));
            }
            catch (NumberFormatException e) {
                throw new IllegalArgumentException("accountId");
            }

            
            if (jobIdString == null || jobIdString.isEmpty()) {
                throw new IllegalArgumentException("jobId");
            }
            
            long jobId;
            try {
                jobId = Long.parseLong(req.getParameter("jobId"));
            }
            catch (NumberFormatException e) {
                throw new IllegalArgumentException("jobId");
            }

            // Try find
            ServiceLocator locator = ServiceLocator.getInstance();
            DeviceJobService deviceJobService = locator.getDeviceJobService();
            JobAttachment jobAttachment = deviceJobService.getJobAttachment(accountId, jobId);
            
            if (jobAttachment == null) {
                throw new KapuaEntityNotFoundException(JobAttachment.class, jobId);
            }
            else {
                resp.setHeader("Content-Type", "application/octect-stream");
                resp.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(jobAttachment.getName(), "UTF-8"));
                resp.setHeader("Cache-Control", "no-transform, max-age=0");
                resp.getOutputStream().write(jobAttachment.getBody());
            }

        }
        catch (IllegalArgumentException iae) {
            resp.sendError(400, "Illegal value for query parameter: " + iae.getMessage());
            return;
        }
        catch (KapuaEntityNotFoundException eenfe) {
            resp.sendError(400, eenfe.getMessage());
            return;
        }
        catch (KapuaUnauthenticatedException eiae) {
            resp.sendError(401, eiae.getMessage());
            return;
        }
        catch (KapuaIllegalAccessException eiae) {
            resp.sendError(403, eiae.getMessage());
            return;
        }
        catch (Exception e) {
            s_logger.error("Error creating device export", e);
            throw new ServletException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException
    {
        resp.setContentType("text/html");
        resp.setCharacterEncoding("UTF-8");

        String reqPathInfo = req.getPathInfo();
        if (reqPathInfo == null) {
            resp.sendError(404);
            return;
        }

        // BEGIN XSRF - Servlet dependent code
        KapuaFormFields edcFormFields = getFormFields(req);

        try {
            GwtXSRFToken token = new GwtXSRFToken(edcFormFields.get("xsrfToken"));
            KapuaRemoteServiceServlet.checkXSRFToken(req, token);
        }
        catch (Exception e) {
            throw new ServletException("Security error: please retry this operation correctly.", e);
        }
        // END XSRF security check

        if (reqPathInfo.equals("/create")) {
            doPostCreateDeviceJob(edcFormFields, resp);
        }
        else {
            resp.sendError(404);
            return;
        }
    }

    private void doPostCreateDeviceJob(KapuaFormFields edcFormFields,
                                       HttpServletResponse resp)
        throws ServletException, IOException
    {
        List<FileItem> fileItems = edcFormFields.getFileItems();

        try {

            // deploy-V2
            SoftwareInstallV2DeviceJobCreator djcV2 = null;

            // undeploy-V2
            SoftwareUninstallV2DeviceJobCreator djucV2 = null;

            //
            // Device Job fields retrieval and lots of validation
            //
            String deviceJobDisplayName = edcFormFields.get("displayName");
            if (deviceJobDisplayName == null || deviceJobDisplayName.isEmpty()) {
                throw new KapuaIllegalNullArgumentException("displayName");
            }

            // AccountId
            Long accountId = Long.parseLong(edcFormFields.get("accountId"));
            if (accountId == null || accountId == 0) {
                throw new KapuaIllegalNullArgumentException("accountId");
            }

            //
            // Action
            String action = edcFormFields.get("actionSelector-hidden");
            if (action == null || action.isEmpty()) {
                throw new KapuaIllegalNullArgumentException("actionSelector");
            }

            //
            // Scheduling

            // Determine Timezone
            String timezoneOffsetS = edcFormFields.get("timezoneOffset");
            if (timezoneOffsetS == null || timezoneOffsetS.isEmpty()) {
                throw new KapuaIllegalNullArgumentException("timezoneOffset");
            }
            int timezoneInMinsConsole = -Integer.parseInt(timezoneOffsetS);
            @SuppressWarnings("deprecation")
            int timezoneServlet = -new Date().getTimezoneOffset();

            int timezoneGap = timezoneInMinsConsole - timezoneServlet;

            // Date parsing
            String startDateString = edcFormFields.get("startDate");
            String startTimeString = edcFormFields.get("startTime");
            if (startDateString == null || startTimeString == null) {
                throw new KapuaIllegalNullArgumentException("startDate");
            }

            if (startTimeString.isEmpty()) {
                startTimeString = "0:00";
            }

            Date startDate = null;
            if (!startDateString.isEmpty()) {
                try {
                    Calendar cal = Calendar.getInstance();

                    int day = Integer.parseInt(startDateString.split("/")[1]);
                    int month = Integer.parseInt(startDateString.split("/")[0]) - 1;
                    int year = Integer.parseInt("20"
                                                + startDateString.split("/")[2]);

                    int hour = Integer.parseInt(startTimeString.split(":")[0]);
                    int mins = Integer.parseInt(startTimeString.split(":")[1]);
                    int sec = 0;

                    cal.set(year, month, day, hour, mins, sec);

                    cal.add(Calendar.MINUTE, -timezoneGap);

                    startDate = cal.getTime();
                }
                catch (NumberFormatException nfe) {
                    throw new KapuaException(KapuaErrorCode.INTERNAL_ERROR, nfe,
                                           "Error while parsing startDate");
                }
            }

            Date endDate = null;
            String endDateString = edcFormFields.get("endDate");
            String endTimeString = edcFormFields.get("endTime");
            if (endDateString == null || endTimeString == null) {
                throw new KapuaIllegalNullArgumentException("endTime");
            }

            if (endTimeString.isEmpty()) {
                endTimeString = "23:59";
            }

            if (!endDateString.isEmpty()) {
                try {
                    Calendar cal = Calendar.getInstance();

                    int day = Integer.parseInt(endDateString.split("/")[1]);
                    int month = Integer.parseInt(endDateString.split("/")[0]) - 1;
                    int year = Integer.parseInt("20"
                                                + endDateString.split("/")[2]);

                    int hour = Integer.parseInt(endTimeString.split(":")[0]);
                    int mins = Integer.parseInt(endTimeString.split(":")[1]);
                    int sec = 0;

                    cal.set(year, month, day, hour, mins, sec);

                    cal.add(Calendar.MINUTE, -timezoneGap);

                    endDate = cal.getTime();
                }
                catch (NumberFormatException nfe) {
                    throw new KapuaException(KapuaErrorCode.INTERNAL_ERROR, nfe,
                                           "Error while parsing startDate");
                }
            }

            //
            // If specified both dates check consistency
            if (startDate != null && endDate != null) {
                if (startDate.after(endDate)) {
                    throw new KapuaIllegalArgumentException("DateRange",
                                                          "Start date comes after end date");
                }
            }

            if (endDate != null && endDate.before(new Date())) {
                throw new KapuaIllegalArgumentException("EndDate",
                                                      "End date comes before current time");
            }

            //
            // Checking retry scheduling
            Long maxAttempts = Long.parseLong(edcFormFields
                                                           .get("retryMaxAttempts"));
            if (maxAttempts == null || maxAttempts == 0) {
                throw new KapuaIllegalNullArgumentException("retryMaxAttempts");
            }

            String cronRetryExpression = edcFormFields
                                                      .get("cronRetryExpression");
            int retryEverySeconds = 0;
            if (cronRetryExpression == null || cronRetryExpression.isEmpty()) {
                String retryUnit = edcFormFields.get("retryEveryUnit");
                if (retryUnit == null || retryUnit.isEmpty()) {
                    throw new KapuaIllegalNullArgumentException("retryEveryUnit");
                }

                Integer retryEvery = Integer.parseInt(edcFormFields
                                                                   .get("retryEveryValue"));
                if (retryEvery == null
                    || (retryEvery == 0 && !retryUnit.startsWith("Never"))) {
                    throw new KapuaIllegalNullArgumentException(
                                                              "retryEveryValue.empty");
                }

                //
                // Compose cron expression
                if (!retryUnit.startsWith("Never")) {
                    if (retryUnit.startsWith("Minute"))
                        retryEverySeconds = retryEvery * 60;
                    else if (retryUnit.startsWith("Hour")) {
                        retryEverySeconds = retryEvery * 3600;
                    }
                    else if (retryUnit.startsWith("Day")) {
                        retryEverySeconds = retryEvery * 86400;
                    }
                    else if (retryUnit.startsWith("Month")) {
                        retryEverySeconds = retryEvery * 2592000;
                    }
                }
            }

            // Targets validation
            List<String> clientIdsList = null;
            List<Long> tagIdsList = null;
            boolean selectAllClientIDsFlag = false;

            String clientIDsSelectType = edcFormFields
                                                      .get("clientIDsSelectType");
            String clientIds = edcFormFields.get("clientIds");
            String tagsIds = edcFormFields.get("tagsIds");

            if (clientIDsSelectType == null || clientIDsSelectType.isEmpty()
                || clientIDsSelectType.equals("Specific Targets")) {

                clientIdsList = new ArrayList<String>();
                tagIdsList = new ArrayList<Long>();

                if (clientIds != null && tagsIds != null) {
                    try {
                        String[] clientIdsArray = clientIds.split("#");

                        for (int i = 0; i < clientIdsArray.length; i++) {
                            if (!clientIdsArray[i].isEmpty())
                                clientIdsList.add(clientIdsArray[i]);
                        }
                    }
                    catch (Exception e) {
                        throw new KapuaIllegalArgumentException("clientIds",
                                                              e.getMessage());
                    }

                    try {
                        String[] tagsIdsArray = tagsIds.split(";");

                        for (int i = 0; i < tagsIdsArray.length; i++) {
                            if (!tagsIdsArray[i].isEmpty())
                                tagIdsList.add(Long.parseLong(tagsIdsArray[i]));
                        }
                    }
                    catch (Exception e) {
                        throw new KapuaIllegalArgumentException("tagIds",
                                                              e.getMessage());
                    }

                    if (clientIdsList.size() == 0 && tagIdsList.size() == 0) {
                        throw new KapuaIllegalNullArgumentException(
                                                                  "ClientIds/TagsIds");
                    }
                }
                else {
                    throw new KapuaIllegalNullArgumentException(
                                                              "ClientIds/TagsIds");
                }
            }
            else {
                selectAllClientIDsFlag = true;
            }

            // Attachments
            FileItem fileItem = null;

            if (fileItems.size() > 0) {
                fileItem = fileItems.get(0);
            }

            String jobProperties = edcFormFields.get("jobProperties");
            DeviceJobType djType;
            JobAttachmentCreator jac = null;

            // instantiate the new enum
            DeviceJobType actionJobType = DeviceJobType.valueOf(action);

            if (actionJobType == DeviceJobType.device_config_update) {
                if (fileItem.get().length == 0 || fileItem.getName().isEmpty()) {
                    throw new KapuaIllegalNullArgumentException("attachment");
                }

                djType = DeviceJobType.device_config_update;

                jac = new JobAttachmentCreator();
                jac.setName(fileItem.getName());
                jac.setBody(fileItem.get());
                jac.setContentType(JobAttachmentType.CONFIGURATION);
            }
            else if (actionJobType == DeviceJobType.device_software_install) {
                if (jobProperties == null || jobProperties.isEmpty()) {
                    if (fileItem.get().length == 0
                        || fileItem.getName().isEmpty()) {
                        throw new KapuaIllegalNullArgumentException("attachment");
                    }

                    jac = new JobAttachmentCreator();
                    jac.setName(fileItem.getName());
                    jac.setBody(fileItem.get());
                    jac.setContentType(JobAttachmentType.BUNDLE);
                }

                djType = DeviceJobType.device_software_install;
            }
            else if (actionJobType == DeviceJobType.device_software_uninstall) {
                if (jobProperties == null || jobProperties.isEmpty()) {
                    throw new KapuaIllegalNullArgumentException("jobProperties");
                }

                djType = DeviceJobType.device_software_uninstall;
            }
            else if (actionJobType == DeviceJobType.device_command) {
                if (jobProperties == null || jobProperties.isEmpty()) {
                    throw new KapuaIllegalNullArgumentException("jobProperties");
                }

                if (fileItem.get().length > 0 && !fileItem.getName().isEmpty()) {

                    jac = new JobAttachmentCreator();
                    jac.setName(fileItem.getName());
                    jac.setBody(fileItem.get());
                    jac.setContentType(JobAttachmentType.FILE);
                }

                djType = DeviceJobType.device_command;
            }
            else if (actionJobType == DeviceJobType.device_certificate_update) {

                djType = DeviceJobType.device_certificate_update;
            }
            else if (actionJobType == DeviceJobType.device_certificate_revoke) {

                // jobProperties value contains the id certificate number
                if (jobProperties == null || jobProperties.isEmpty()) {
                    throw new KapuaIllegalNullArgumentException("jobProperties");
                }

                djType = DeviceJobType.device_certificate_revoke;

            }
            else if (actionJobType == DeviceJobType.device_software_install_v2) {

                djType = DeviceJobType.device_software_install_v2;
                djcV2 = new SoftwareInstallV2DeviceJobCreator(accountId, djType);

                // basic parameters
                String dpUri = edcFormFields.get("dp.download.uri");
                String dpName = edcFormFields.get("dp.name");
                String dpVersion = edcFormFields.get("dp.version");

                if ((dpUri == null) || ("".equals(dpUri))) {
                    throw new KapuaIllegalNullArgumentException("dp.download.uri is null or empty: this field is mandatory");
                }
                if ((dpName == null) || ("".equals(dpName))) {
                    throw new KapuaIllegalNullArgumentException("dp.name is null or empty: this field is mandatory");
                }
                if ((dpVersion == null) || ("".equals(dpVersion))) {
                    throw new KapuaIllegalNullArgumentException("dp.version is null or empty: this field is mandatory");
                }

                djcV2.setDpURI(dpUri);
                djcV2.setDpName(dpName);
                djcV2.setDpVersion(dpVersion);

                // V2 properties
                Integer dpDownloadBlockSize = Integer.parseInt(edcFormFields.get("dp.download.block.size"));
                String dpDownloadProtocol = checkVoidString(edcFormFields.get("dp.download.protocol"));
                Integer dpDownloadBlockDelay = Integer.parseInt(edcFormFields.get("dp.download.block.delay"));
                Integer dpDownloadBlockNotify = Integer.parseInt(edcFormFields.get("dp.download.notify.block.size"));
                Integer dpDownloadTimeout = Integer.parseInt(edcFormFields.get("dp.download.timeout"));
                Boolean dpDownloadResume = Boolean.parseBoolean(edcFormFields.get("dp.download.resume"));
                Boolean dpForceDownload = Boolean.parseBoolean(edcFormFields.get("dp.download.force"));

                String dpDownloadHash = checkVoidString(edcFormFields.get("dp.download.hash"));

                String dpDownloadUsername = checkVoidString(edcFormFields.get("dp.download.username"));
                String dpDownloadPassword = checkVoidString(edcFormFields.get("dp.download.password"));

                Boolean dpInstall = Boolean.parseBoolean(edcFormFields.get("dp.install"));
                Boolean dpInstallSystemUpdate = Boolean.parseBoolean(edcFormFields.get("dp.install.system.update"));
                String dpInstallVeryfierURL = checkVoidString(edcFormFields.get("dp.install.verifier.url"));

                Boolean dpReboot = Boolean.parseBoolean(edcFormFields.get("dp.reboot"));
                Integer dpRebootDelay = Integer.parseInt(edcFormFields.get("dp.reboot.delay"));

                Boolean processOnConnect = Boolean.parseBoolean(edcFormFields.get("processOnConnect"));

                // advanced configuration properties
                djcV2.setDpDownloadBlockSize(dpDownloadBlockSize);
                djcV2.setDpDownloadBlockDelay(dpDownloadBlockDelay);
                djcV2.setDpDownloadBlockNotify(dpDownloadBlockNotify);
                djcV2.setDpDownloadTimeout(dpDownloadTimeout);
                djcV2.setDpDownloadResume(dpDownloadResume);
                djcV2.setDpDownloadForce(dpForceDownload);

                if (dpDownloadHash != null &&
                    !dpDownloadHash.isEmpty())
                {
                    dpDownloadHash = "MD5:".concat(dpDownloadHash);
                }
                
                djcV2.setDpDownloadHash(dpDownloadHash);

                djcV2.setDpDownloadUsername(dpDownloadUsername);
                djcV2.setDpDownloadPassword(dpDownloadPassword);
                djcV2.setDpInstall(dpInstall);
                djcV2.setDpInstallSystemUpdate(dpInstallSystemUpdate);
                djcV2.setDpInstallVerifierURI(dpInstallVeryfierURL);
                djcV2.setDpReboot(dpReboot);
                djcV2.setDpRebootDelay(dpRebootDelay);

                djcV2.setDpDownloadProtocol(dpDownloadProtocol);

                djcV2.setProcessOnConnect(processOnConnect);
                djcV2.setSendWakeupSMS(false);

            }
            else if (actionJobType == DeviceJobType.device_software_uninstall_v2) {

                djType = DeviceJobType.device_software_uninstall_v2;

                djucV2 = new SoftwareUninstallV2DeviceJobCreator();
                djucV2.setAccountId(accountId);

                String dpName = checkVoidString(edcFormFields.get("dp.name"));
                if ((dpName == null) || ("".equals(dpName))) {
                    throw new KapuaIllegalNullArgumentException("dp.name is null or empty: this field is mandatory");
                }

                Boolean dpReboot = Boolean.parseBoolean(edcFormFields.get("dp.reboot"));
                Integer dpRebootDelay = Integer.parseInt(edcFormFields.get("dp.reboot.delay"));
                Boolean processOnConnect = Boolean.parseBoolean(edcFormFields.get("processOnConnect"));

                djucV2.setDpName(dpName);
                djucV2.setDpReboot(dpReboot);
                djucV2.setDpRebootDelay(dpRebootDelay);

                djucV2.setProcessOnConnect(processOnConnect);
                djucV2.setSendWakeupSMS(false);

            }
            else {
                throw new KapuaIllegalArgumentException("action",
                                                      "Invalid action selected");
            }

            //
            // Finally all has been validated!
            // Creating a new Device Job
            //

            ServiceLocator locator = ServiceLocator.getInstance();
            DeviceJobService deviceJobService = locator.getDeviceJobService();

            if (djType == DeviceJobType.device_software_install_v2) {

                // standard job properties
                djcV2.setJobDisplayName(deviceJobDisplayName);
                djcV2.setJobProperties(jobProperties);
                djcV2.setJobAttachment(jac);
                djcV2.setSelectAllClientIds(selectAllClientIDsFlag);
                djcV2.setTargetClientIds(clientIdsList);
                djcV2.setTargetDeviceTags(tagIdsList);
                djcV2.setStartOn(startDate);
                djcV2.setEndOn(endDate);
                djcV2.setCronRetryExpression(cronRetryExpression);
                djcV2.setRetryEvery(retryEverySeconds);
                djcV2.setRetryMaxAttempts(maxAttempts.intValue());
                djcV2.setTimezoneInMins(timezoneInMinsConsole);

                deviceJobService.create(djcV2);

            }
            else if (djType == DeviceJobType.device_software_uninstall_v2) {

                // standard job properties
                djucV2.setJobDisplayName(deviceJobDisplayName);
                djucV2.setJobProperties(jobProperties);
                djucV2.setJobAttachment(jac);
                djucV2.setSelectAllClientIds(selectAllClientIDsFlag);
                djucV2.setTargetClientIds(clientIdsList);
                djucV2.setTargetDeviceTags(tagIdsList);
                djucV2.setStartOn(startDate);
                djucV2.setEndOn(endDate);
                djucV2.setCronRetryExpression(cronRetryExpression);
                djucV2.setRetryEvery(retryEverySeconds);
                djucV2.setRetryMaxAttempts(maxAttempts.intValue());
                djucV2.setTimezoneInMins(timezoneInMinsConsole);

                deviceJobService.create(djucV2);

            }
            else {

                DeviceJobCreator djc = new DeviceJobCreator(accountId, djType);

                // standard job properties
                djc.setJobDisplayName(deviceJobDisplayName);
                djc.setJobProperties(jobProperties);
                djc.setJobAttachment(jac);
                djc.setSelectAllClientIds(selectAllClientIDsFlag);
                djc.setTargetClientIds(clientIdsList);
                djc.setTargetDeviceTags(tagIdsList);
                djc.setStartOn(startDate);
                djc.setEndOn(endDate);
                djc.setCronRetryExpression(cronRetryExpression);
                djc.setRetryEvery(retryEverySeconds);
                djc.setRetryMaxAttempts(maxAttempts.intValue());
                djc.setTimezoneInMins(timezoneInMinsConsole);

                deviceJobService.create(djc);
            }

        }
        catch (KapuaException e) {
            s_logger.error("Error while creating device job request", e);
            String respMsg = extractRespMsgFromException(e);
            resp.sendError(400, respMsg);
        }
    }

    private String checkVoidString(String input)
    {
        if (input == null)
            return null;

        // require null instead empty string
        if ("".equals(input))
            return null;

        return input;
    }

    private String extractRespMsgFromException(KapuaException e)
    {
        String respMsg = "";
        String exceptionMsg = e.getMessage();

        if (exceptionMsg == null || exceptionMsg.isEmpty()) {
            respMsg = "InternalError";
        }
        else if (exceptionMsg.contains("DateRange")) {
            respMsg = "DateRange";
        }
        else if (exceptionMsg.contains("EndDate")) {
            respMsg = "EndDate";
        }
        else if (exceptionMsg.contains("timezoneOffset")) {
            respMsg = "timezoneOffset";
        }
        else if (exceptionMsg.contains("ClientIds/TagsIds")) {
            respMsg = "ClientIds/TagsIds";
        }
        else if (exceptionMsg.contains("Device jobs limit exceeded")) {
            respMsg = "DeviceJobLimit";
        }

        return respMsg;
    }

}
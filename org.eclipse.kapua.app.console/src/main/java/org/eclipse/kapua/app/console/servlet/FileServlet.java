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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.poi.util.IOUtils;
//import org.apache.commons.io.IOUtils;
import org.eclipse.kapua.KapuaEntityNotFoundException;
import org.eclipse.kapua.KapuaIllegalAccessException;
import org.eclipse.kapua.KapuaUnauthenticatedException;
import org.eclipse.kapua.app.console.server.KapuaRemoteServiceServlet;
import org.eclipse.kapua.app.console.setting.ConsoleSetting;
import org.eclipse.kapua.app.console.setting.ConsoleSettingKeys;
import org.eclipse.kapua.app.console.shared.model.GwtXSRFToken;
import org.eclipse.kapua.app.console.shared.model.KapuaFormFields;
import org.eclipse.kapua.commons.model.id.KapuaEid;
import org.eclipse.kapua.commons.util.XmlUtil;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.service.device.management.command.DeviceCommandFactory;
import org.eclipse.kapua.service.device.management.command.DeviceCommandInput;
import org.eclipse.kapua.service.device.management.command.DeviceCommandManagementService;
import org.eclipse.kapua.service.device.management.command.DeviceCommandOutput;
import org.eclipse.kapua.service.device.management.configuration.DeviceConfiguration;
import org.eclipse.kapua.service.device.management.configuration.DeviceConfigurationManagementService;
import org.eclipse.kapua.service.device.management.deploy.DeviceDeployManagementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileServlet extends EdcHttpServlet
{
    private static final long serialVersionUID = -5016170117606322129L;
    private static Logger     s_logger         = LoggerFactory.getLogger(FileServlet.class);

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

        if (reqPathInfo.startsWith("/deploy")) {
            doPostDeploy(req.getPathInfo(), edcFormFields, resp);
        }
        else if (reqPathInfo.equals("/command")) {
            doPostCommand(edcFormFields, resp);
        }
        else if (reqPathInfo.equals("/configuration/snapshot")) {
            doPostConfigurationSnapshot(edcFormFields, resp);
        }
        else {
            resp.sendError(404);
            return;
        }
    }

    private void doPostConfigurationSnapshot(KapuaFormFields edcFormFields, HttpServletResponse resp)
        throws ServletException, IOException
    {
        try {
            List<FileItem> fileItems = edcFormFields.getFileItems();
            String scopeIdString = edcFormFields.get("scopeIdString");
            String deviceIdString = edcFormFields.get("deviceIdString");

            if (scopeIdString == null || scopeIdString.isEmpty()) {
                throw new IllegalArgumentException("scopeIdString");
            }

            if (deviceIdString == null || deviceIdString.isEmpty()) {
                throw new IllegalArgumentException("deviceIdString");
            }

            if (fileItems == null || fileItems.size() != 1) {
                throw new IllegalArgumentException("configuration");
            }

            KapuaLocator locator = KapuaLocator.getInstance();
            DeviceConfigurationManagementService deviceService = locator.getService(DeviceConfigurationManagementService.class);

            FileItem fileItem = fileItems.get(0);
            byte[] data = fileItem.get();
            String xmlString = new String(data, "UTF-8");

            DeviceConfiguration config = XmlUtil.unmarshal(xmlString, DeviceConfiguration.class);
            deviceService.put(KapuaEid.parseShortId(scopeIdString),
                              KapuaEid.parseShortId(deviceIdString),
                              config, null);

            // //
            // // Add an additional delay after the configuration update
            // // to give the time to the device to apply the received
            // // configuration
            // EdcConfig edcConfig = EdcConfig.getInstance();
            // int delay = edcConfig.getConsoleDeviceUpdateConfDelay();
            // if (delay > 0) {
            // Thread.sleep(delay);
            // }

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
            s_logger.error("Error posting configuration", e);
            throw new ServletException(e);
        }
    }

    private void doPostDeploy(String reqPathInfo, KapuaFormFields edcFormFields, HttpServletResponse resp)
        throws ServletException, IOException
    {
        try {
            KapuaLocator locator = KapuaLocator.getInstance();
            DeviceDeployManagementService deviceDeployManagementService = locator.getService(DeviceDeployManagementService.class);

            String scopeIdString = edcFormFields.get("scopeIdString");
            String deviceIdString = edcFormFields.get("deviceIdString");

            if (scopeIdString == null || scopeIdString.isEmpty()) {
                throw new IllegalArgumentException("scopeId");
            }

            if (deviceIdString == null || deviceIdString.isEmpty()) {
                throw new IllegalArgumentException("deviceId");
            }

            if (reqPathInfo.endsWith("url")) {

                String packageDownloadUrl = edcFormFields.get("packageUrl");

                if (packageDownloadUrl == null ||
                    packageDownloadUrl.isEmpty() ||
                    !packageDownloadUrl.endsWith(".dp")) {
                    throw new IllegalArgumentException("packageUrl");
                }

                s_logger.info("Installing deployment package at URL: {}", packageDownloadUrl);
                deviceDeployManagementService.install(KapuaEid.parseShortId(scopeIdString),
                                                      KapuaEid.parseShortId(deviceIdString),
                                                      packageDownloadUrl,
                                                      null);
            }
            else if (reqPathInfo.endsWith("upload")) {

                List<FileItem> fileItems = edcFormFields.getFileItems();

                if (fileItems == null || fileItems.size() != 1) {
                    throw new IllegalArgumentException("file");
                }

                FileItem item = fileItems.get(0);
                String filename = item.getName();
                byte[] data = item.get();

                if (filename == null || !filename.endsWith(".dp")) {
                    throw new IllegalArgumentException("packageUrl");
                }

                s_logger.info("Installing deployment package: {}", filename);
                deviceDeployManagementService.install(KapuaEid.parseShortId(scopeIdString),
                                                      KapuaEid.parseShortId(deviceIdString),
                                                      filename,
                                                      data,
                                                      null);
            }
            else {
                resp.sendError(404);
                return;
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
            s_logger.error("Error posting deployment package", e);
            throw new ServletException(e);
        }
    }

    private void doPostCommand(KapuaFormFields edcFormFields, HttpServletResponse resp)
        throws ServletException, IOException
    {
        try {
            List<FileItem> fileItems = edcFormFields.getFileItems();

            final String scopeIdString = edcFormFields.get("scopeIdString");
            final String deviceIdString = edcFormFields.get("deviceIdString");
            final String command = edcFormFields.get("command");
            final String password = edcFormFields.get("password");
            final String timeoutString = edcFormFields.get("timeout");

            if (scopeIdString == null || scopeIdString.isEmpty()) {
                throw new IllegalArgumentException("scopeId");
            }

            if (deviceIdString == null || deviceIdString.isEmpty()) {
                throw new IllegalArgumentException("clientId");
            }

            if (command == null || command.isEmpty()) {
                throw new IllegalArgumentException("command");
            }

            if (fileItems.size() > 1) {
                throw new IllegalArgumentException("file");
            }

            Integer timeout = null;
            if (timeoutString != null && !timeoutString.isEmpty()) {
                try {
                    timeout = Integer.parseInt(timeoutString);
                }
                catch (NumberFormatException nfe) {
                    throw new IllegalArgumentException("timeout");
                }
            }

            KapuaLocator locator = KapuaLocator.getInstance();
            DeviceCommandManagementService deviceService = locator.getService(DeviceCommandManagementService.class);

            // FIXME: set a max size on the MQtt payload
            byte[] data = fileItems.size() == 0 ? null : fileItems.get(0).get();

            DeviceCommandFactory deviceCommandFactory = locator.getFactory(DeviceCommandFactory.class);
            DeviceCommandInput commandInput = deviceCommandFactory.newCommandInput();

            StringTokenizer st = new StringTokenizer(command);
            int count = st.countTokens();

            String cmd = count > 0 ? st.nextToken() : null;
            String[] args = count > 1 ? new String[count - 1] : null;
            int i = 0;
            while (st.hasMoreTokens()) {
                args[i++] = st.nextToken();
            }

            commandInput.setCommand(cmd);
            commandInput.setPassword(password);
            commandInput.setArguments(args);
            commandInput.setTimeout(timeout);
            commandInput.setWorkingDir("/tmp/");
            commandInput.setBody(data);

            DeviceCommandOutput deviceCommandOutput = deviceService.exec(KapuaEid.parseShortId(scopeIdString),
                                                                         KapuaEid.parseShortId(deviceIdString),
                                                                         commandInput, null);
            resp.setContentType("text/plain");
            if (deviceCommandOutput.getStderr() != null &&
                deviceCommandOutput.getStderr().length() > 0) {
                resp.getWriter().println(deviceCommandOutput.getStderr());
            }
            if (deviceCommandOutput.getStdout() != null &&
                deviceCommandOutput.getStdout().length() > 0) {
                resp.getWriter().println(deviceCommandOutput.getStdout());
            }
            if (deviceCommandOutput.getExceptionMessage() != null &&
                deviceCommandOutput.getExceptionMessage().length() > 0) {
                resp.getWriter().println(deviceCommandOutput.getExceptionMessage());
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
            s_logger.error("Error sending command", e);
            throw new ServletException(e);
        }
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException
    {
        String reqPathInfo = request.getPathInfo();

        if (reqPathInfo.startsWith("/icons")) {
            doGetIconResource(request, response);
        }
        else {
            response.sendError(404);
        }
    }

    private void doGetIconResource(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException
    {
        final String id = request.getParameter("id");

        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("id");
        }

        if (!id.matches("[0-9A-Za-z]{1,}")) {
            throw new IllegalArgumentException("id");
        }

        String tmpPath = System.getProperty("java.io.tmpdir");

        StringBuilder filePathSb = new StringBuilder(tmpPath);

        if (!tmpPath.endsWith("/")) {
            filePathSb.append("/");
        }

        ConsoleSetting config = ConsoleSetting.getInstance();

        filePathSb.append(config.getString(ConsoleSettingKeys.DEVICE_CONFIGURATION_ICON_FOLDER))
                  .append("/")
                  .append(id);

        File requestedFile = new File(filePathSb.toString());
        if (!requestedFile.exists()) {
            throw new IllegalArgumentException("id");
        }

        response.setContentType("application/octet-stream");
        response.setCharacterEncoding("UTF-8");
        OutputStream out = response.getOutputStream();
        IOUtils.copy(new FileInputStream(requestedFile), out);
    }
}

class UploadRequest extends ServletFileUpload
{
    private static Logger s_logger = LoggerFactory.getLogger(UploadRequest.class);

    Map<String, String>   formFields;
    List<FileItem>        fileItems;

    public UploadRequest(DiskFileItemFactory diskFileItemFactory)
    {
        super(diskFileItemFactory);
        setSizeMax(ConsoleSetting.getInstance().getLong(ConsoleSettingKeys.FILE_UPLOAD_SIZE_MAX));
        formFields = new HashMap<String, String>();
        fileItems = new ArrayList<FileItem>();
    }

    @SuppressWarnings("unchecked")
    public void parse(HttpServletRequest req)
        throws FileUploadException
    {
        s_logger.debug("upload.getFileSizeMax(): {}", getFileSizeMax());
        s_logger.debug("upload.getSizeMax(): {}", getSizeMax());

        // Parse the request
        List<FileItem> items = null;
        items = parseRequest(req);

        // Process the uploaded items
        Iterator<FileItem> iter = items.iterator();
        while (iter.hasNext()) {
            FileItem item = (FileItem) iter.next();

            if (item.isFormField()) {
                String name = item.getFieldName();
                String value = item.getString();

                s_logger.debug("Form field item name: {}, value: {}", name, value);

                formFields.put(name, value);
            }
            else {
                String fieldName = item.getFieldName();
                String fileName = item.getName();
                String contentType = item.getContentType();
                boolean isInMemory = item.isInMemory();
                long sizeInBytes = item.getSize();

                s_logger.debug("File upload item name: {}, fileName: {}, contentType: {}, isInMemory: {}, size: {}",
                               new Object[] { fieldName, fileName, contentType, isInMemory, sizeInBytes });

                fileItems.add(item);
            }
        }
    }

    public Map<String, String> getFormFields()
    {
        return formFields;
    }

    public List<FileItem> getFileItems()
    {
        return fileItems;
    }
}

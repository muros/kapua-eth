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
import java.io.PrintWriter;
import java.net.URLEncoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.kapua.service.device.management.DeviceManagementService;
import org.eclipse.kapua.service.device.management.configuration.DeviceConfiguration;
import org.eclipse.kapua.service.locator.ServiceLocator;
import org.eclipse.kapua.util.XmlUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeviceSnapshotsServlet extends HttpServlet {
    private static final long serialVersionUID = -2533869595709953567L;

    private static Logger     s_logger         = LoggerFactory.getLogger(DeviceSnapshotsServlet.class);

    public void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        response.setCharacterEncoding("UTF-8");
        PrintWriter writer = response.getWriter();
        try {

            // parameter extraction
            String account = request.getParameter("account");
            String clientId = request.getParameter("clientId");
            String snapshotId = request.getParameter("snapshotId");

            //
            // get the devices and append them to the exporter
            ServiceLocator locator = ServiceLocator.getInstance();
            DeviceManagementService deviceService = locator.getDeviceService();
            DeviceConfiguration conf = deviceService.findDeviceSnapshot(account,
                                       clientId,
                                       snapshotId);

            response.setContentType("application/xml; charset=UTF-8");
            response.setHeader("Content-Disposition", "attachment; "
                               + "filename*=UTF-8''" + URLEncoder.encode(account, "UTF-8") + "_" + URLEncoder.encode(clientId, "UTF-8") + "_" + snapshotId + ".xml; ");
            response.setHeader("Cache-Control", "no-transform, max-age=0");

            XmlUtil.marshal(conf, writer);
        } catch (Exception e) {
            s_logger.error("Error creating Excel export", e);
            throw new ServletException(e);
        } finally {
            if (writer != null)
                writer.close();
        }
    }
}

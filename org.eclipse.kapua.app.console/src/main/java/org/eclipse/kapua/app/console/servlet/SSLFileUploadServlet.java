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

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.eclipse.kapua.app.console.server.KapuaRemoteServiceServlet;
import org.eclipse.kapua.app.console.shared.model.KapuaFormFields;
import org.eclipse.kapua.app.console.shared.model.GwtXSRFToken;
import org.eclipse.kapua.service.config.EdcConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SSLFileUploadServlet extends EdcHttpServlet {
    private static Logger     s_logger         = LoggerFactory.getLogger(SSLFileUploadServlet.class);
    private static final long serialVersionUID = -5016170117606322129L;

    private static int        nInvocations     = 0;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
    throws ServletException, IOException {
        resp.setContentType("text/html");
        resp.setCharacterEncoding("UTF-8");

        String reqPathInfo = req.getPathInfo();
        if (reqPathInfo == null) {
            s_logger.error("Request path info not found");
            throw new ServletException("Request path info not found");
        }

        s_logger.debug("req.getRequestURI(): {}", req.getRequestURI());
        s_logger.debug("req.getRequestURL(): {}", req.getRequestURL());
        s_logger.debug("req.getPathInfo(): {}", req.getPathInfo());

        // BEGIN XSRF - Servlet dependent code
        KapuaFormFields edcFormFields = getFormFields(req);

        try {
            GwtXSRFToken token = new GwtXSRFToken(edcFormFields.get("xsrfToken"));
            KapuaRemoteServiceServlet.checkXSRFToken(req, token);
        } catch (Exception e) {
            s_logger.error("Error during the XSRF token checking on multipart: " + e.getMessage());
            e.printStackTrace();
            throw new ServletException("Security error: please retry this operation correctly.");
        }
        // END XSRF security check

        if (reqPathInfo.startsWith("/certificate")) {
            doPostSSLCertificateInstall(edcFormFields, resp);
        } else {
            s_logger.error("Unknown request path info: " + reqPathInfo);
            throw new ServletException("Unknown request path info: " + reqPathInfo);
        }
    }

    private void doPostSSLCertificateInstall(KapuaFormFields edcFormFields, HttpServletResponse resp)
    throws ServletException, IOException {
        EdcConfig config = EdcConfig.getInstance();
        List<FileItem> fileItems = edcFormFields.getFileItems();

        if (fileItems.size() != 2) {
            s_logger.error("expected 2 file items but found {}", fileItems.size());
            throw new ServletException("Wrong number of file items");
        }

        Iterator<FileItem> iter = fileItems.iterator();
        String newStream = new String();
        while (iter.hasNext()) {
            FileItem item = iter.next();
            if (!item.isFormField()) {
                newStream = item.getString();
            }

            BufferedWriter writer;
            String fileName = null;
            Boolean isCert = newStream.contains("CERTIFICATE");
            Boolean isKey = newStream.contains("KEY");

            if (nInvocations == 0) {
                if (isCert) {
                    nInvocations++;
                    fileName = config.getSSLNewCertificateFilename();
                } else {
                    throw new ServletException("A valid SSL Certificate file must be uploaded.");
                }
            } else if (nInvocations == 1) {
                nInvocations = 0;
                if (isKey) {
                    fileName = config.getSSLNewKeyFilename();
                } else {
                    throw new ServletException("A valid SSL Key file must be uploaded.");
                }
            }

            try {
                writer = new BufferedWriter(new FileWriter(fileName));
                try {
                    writer.write(newStream);
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}

class SSLUploadRequest extends ServletFileUpload {
    private static Logger s_logger = LoggerFactory.getLogger(SSLUploadRequest.class);

    Map<String, String>   formFields;
    List<FileItem>        fileItems;

    public SSLUploadRequest(DiskFileItemFactory diskFileItemFactory) {
        super(diskFileItemFactory);

        setSizeMax(EdcConfig.getInstance().getFileUploadSizeMax());
        formFields = new HashMap<String, String>();
        fileItems = new ArrayList<FileItem>();
    }

    @SuppressWarnings("unchecked")
    public void parse(HttpServletRequest req)
    throws FileUploadException {
        s_logger.debug("upload.getFileSizeMax(): {}", getFileSizeMax());
        s_logger.debug("upload.getSizeMax(): {}", getSizeMax());

        // Parse the request
        List<FileItem> items = null;
        items = parseRequest(req);

        // Process the uploaded items
        Iterator<FileItem> iter = items.iterator();
        while (iter.hasNext()) {
            FileItem item = iter.next();

            if (item.isFormField()) {
                String name = item.getFieldName();
                String value = item.getString();
                s_logger.debug("Form field item name: {}, value: {}", name, value);

                formFields.put(name, value);
            } else {
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

    public List<FileItem> getFileItems() {
        return fileItems;
    }
}
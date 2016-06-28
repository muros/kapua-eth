package org.eclipse.kapua.app.console.servlet;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.eclipse.kapua.KapuaEntityNotFoundException;
import org.eclipse.kapua.KapuaIllegalAccessException;
import org.eclipse.kapua.KapuaUnauthenticatedException;
import org.eclipse.kapua.app.console.client.util.EdcSafeHtmlUtils;
import org.eclipse.kapua.app.console.server.KapuaRemoteServiceServlet;
import org.eclipse.kapua.app.console.shared.model.KapuaFormFields;
import org.eclipse.kapua.app.console.shared.model.GwtXSRFToken;
import org.eclipse.kapua.app.console.shared.model.GwtDevice.GwtDeviceCredentialsTight;
import org.eclipse.kapua.service.account.DeviceCredentialsMode;
import org.eclipse.kapua.service.job.JobAttachmentCreator;
import org.eclipse.kapua.service.job.JobAttachmentType;
import org.eclipse.kapua.service.locator.ServiceLocator;
import org.eclipse.kapua.service.provision.ProvisionRequestCreator;
import org.eclipse.kapua.service.provision.ProvisionRequestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProvisionServlet extends EdcHttpServlet
{
    private static final long serialVersionUID = -5016170117606322129L;
    private static Logger     s_logger         = LoggerFactory.getLogger(ProvisionServlet.class);

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
            doPostCreateProvisionRequest(edcFormFields, resp);
        }
        else {
            resp.sendError(404);
            return;
        }
    }

    private void doPostCreateProvisionRequest(KapuaFormFields edcFormFields, HttpServletResponse resp)
        throws ServletException, IOException
    {
        List<FileItem> fileItems = edcFormFields.getFileItems();

        try {
            //
            // Provision fields
            long accountId = Long.parseLong(edcFormFields.get("accountId"));
            
            String clientId = edcFormFields.get("clientId");
            if (clientId == null ||
                    clientId.isEmpty()) {
                throw new IllegalArgumentException("clientID");
            }
            
//            String clientId = EdcSafeHtmlUtils.htmlUnescape(escapedClientId);
            
            String provisionUsername = edcFormFields.get("usernameField");
            if (provisionUsername == null ||
                    provisionUsername.isEmpty())
            {
                throw new IllegalArgumentException("username");
            }
                
            String provisionPassword = edcFormFields.get("userPassword");
            if (provisionPassword == null ||
                    provisionPassword.isEmpty())
            {
                throw new IllegalArgumentException("password");
            }
            
            // ProvisionSecureURL  
            boolean provisionSecureUrl = true;
            String provisionSecureURLStr = edcFormFields.get("provisionSecureURL");
            if (provisionSecureURLStr != null){            	
            	provisionSecureUrl = Boolean.parseBoolean(provisionSecureURLStr);            	
            }            
            
            // Provision Credential Tight            
            DeviceCredentialsMode provisionedCredentialsTight = null;
            String provCredentialsTightStr=edcFormFields.get("provisionedCredentialsTight");  
            if (provCredentialsTightStr != null &&
                !provCredentialsTightStr.isEmpty()) 
            {
                GwtDeviceCredentialsTight gwtDeviceCredentialsTight = GwtDeviceCredentialsTight.getEnumFromLabel(provCredentialsTightStr);
                provisionedCredentialsTight = DeviceCredentialsMode.valueOf(gwtDeviceCredentialsTight.name());             
            }
            
            // Generate activation key field
            boolean generateActivationKey = false;
            String generateActivationKeyString = edcFormFields.get("requireKey");
            if (generateActivationKeyString != null) {
                generateActivationKey = Boolean.valueOf(generateActivationKeyString);
            }
           
            // Dates
            DateFormat df = new SimpleDateFormat("MM/dd/yy");

            String activatesOnString = edcFormFields.get("activationDateField");
            Date activatesOn = new Date(); // Set to now
            if (activatesOnString != null && !activatesOnString.isEmpty()) {
                try {
                    activatesOn = df.parse(activatesOnString);
                }
                catch (ParseException e) {
                    s_logger.error("Error parsing the file upload request", e);
                    throw new IllegalArgumentException("activationDateField", e);
                }
            }

            String expiresOnString = edcFormFields.get("expirationDateField");
            Date expiresOn = null;
            if (expiresOnString != null && !expiresOnString.isEmpty()) {
                try {
                    expiresOn = df.parse(expiresOnString);
                }
                catch (ParseException e) {
                    s_logger.error("Error parsing the file upload request", e);
                    throw new IllegalArgumentException("expirationDateField", e);
                }
            }

            // Provision Max Attempts - default as 5
            int retryMaxAttemps = 5;
            String retryFields = edcFormFields.get("retryField");
            if (retryFields != null &&
                    !retryFields.isEmpty())
            {
                try {
                    
                    retryMaxAttemps = Integer.parseInt(retryFields);
                }
                catch (NumberFormatException nfe) {
                    throw new IllegalArgumentException("retryField", nfe);
                }
            }
            
            //
            ProvisionRequestCreator provisionRequestCreator = new ProvisionRequestCreator();

            provisionRequestCreator.setAccountId(accountId);
            provisionRequestCreator.setClientId(clientId);
            provisionRequestCreator.setProvisionUserUsername(provisionUsername);
            provisionRequestCreator.setProvisionUserPassword(provisionPassword);
            provisionRequestCreator.setProvisionSecureURL(provisionSecureUrl);
            if (provisionedCredentialsTight != null) {
                provisionRequestCreator.setDeviceCredentialsTight(provisionedCredentialsTight);
            }
            provisionRequestCreator.setGenerateActivationKey(generateActivationKey);
            provisionRequestCreator.setActivatesOn(activatesOn);
            provisionRequestCreator.setExpiresOn(expiresOn);
            provisionRequestCreator.setRetryMaxAttempts(retryMaxAttemps);

            //
            // Provision attachment
                        
            // attachment
            JobAttachmentType attachmentType=null;
            
            String attachmentTypeStr=edcFormFields.get("comboType");
            if (attachmentTypeStr!=null)	{
            	attachmentType = JobAttachmentType.valueOf(edcFormFields.get("comboType"));                        	
            }
            
            List<JobAttachmentCreator> attachments = new ArrayList<JobAttachmentCreator>();
            for (FileItem fileItem : fileItems) {
                if (fileItem.getSize() > 0) {
                    byte[] data = fileItem.get();

                    JobAttachmentCreator jac = new JobAttachmentCreator();
                    jac.setName(fileItem.getName());
                    jac.setContentType(attachmentType);
                    jac.setBody(data);

                    attachments.add(jac);
                }
            }
            provisionRequestCreator.setProvisionAttachments(attachments);

            ProvisionRequestService provisionService = ServiceLocator.getInstance().getProvisionService();
            provisionService.create(provisionRequestCreator);
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
            s_logger.error("Error creating usage export", e);
            String respMsg = extractRespMsgFromException(e);
            throw new ServletException(respMsg);
        }
    }
    
    static private String extractRespMsgFromException(Exception e)
    {
        String respMsg = "";
        String exceptionMsg = e.getMessage();

        if (exceptionMsg == null || exceptionMsg.isEmpty()) {
            respMsg = "InternalError";
        }
        else if (exceptionMsg.contains("Date Range")) {
            respMsg = "DateRange";
        }
        else if (exceptionMsg.contains("Username and/or password")) {
            respMsg = "UsernamePassword";
        }
        else if (exceptionMsg.contains("Error during Persistence Operation")) {
            respMsg = "ClientId";
        }

        return respMsg;
    }
}

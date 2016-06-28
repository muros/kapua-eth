package org.eclipse.kapua.app.console.servlet;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.kapua.KapuaEntityNotFoundException;
import org.eclipse.kapua.KapuaIllegalAccessException;
import org.eclipse.kapua.KapuaUnauthenticatedException;
import org.eclipse.kapua.service.locator.ServiceLocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eurotech.cloud.service.pki.DeviceMgmtPki;
import com.eurotech.cloud.service.pki.PkiDeviceMgmtService;

public class PkiExporterServlet extends HttpServlet
{
    private static final long serialVersionUID = 2522576608526785728L;
    private static Logger     s_logger         = LoggerFactory.getLogger(PkiExporterServlet.class);

    
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
    
    public void internalDoGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException
    {

        OutputStream out = response.getOutputStream();
        try {

            String certificateId = request.getParameter("certificateId");
            if (certificateId == null || certificateId.isEmpty()) {
                throw new IllegalArgumentException("certificateId");
            }

            long crtId;
            try {
                crtId = Long.parseLong(certificateId);
            }
            catch (NumberFormatException nfe) {
                throw new IllegalArgumentException("certificateId");
            }

            //
            // get the requested Pki
            ServiceLocator locator = ServiceLocator.getInstance();
            PkiDeviceMgmtService pkiService = locator.getPkiDeviceMgmtService();

            DeviceMgmtPki pki = (DeviceMgmtPki) pkiService.find(crtId);
            if (pki != null) {
                // expMode
                String expMode = request.getParameter("expMode");

                s_logger.info(" Pki: " + pki.getId() + " expMode: " + expMode);

                if (expMode != null) {
                    if (!expMode.equals("")) {

                        // set the response
                        response.setHeader("Cache-Control", "no-transform, max-age=0");

                        if (expMode.equals("1")) {
                            // public key export
                            // response.setContentType("application/x-x509-user-cert");
                            response.setContentType("application/x-pem-file");

                            response.setHeader("Content-Disposition", "attachment; filename=public_" + pki.getId() + ".crt");

                            // getDecodedCertificate().getPublicKey().getEncoded()

                            out.write(pki.getCertificate().getBytes());
                        }

                        if (expMode.equals("2")) {
                            // private key export
                            response.setContentType("application/x-pem-file");
                            response.setHeader("Content-Disposition", "attachment; filename=private_" + pki.getId() + ".pem");

                            out.write(pki.getPrivateKey().getBytes());
                        }

                        // other, full mode
                    }
                }
            }
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
            s_logger.error("Error creating PKI export", e);
            throw new ServletException(e);
        }
    }
}
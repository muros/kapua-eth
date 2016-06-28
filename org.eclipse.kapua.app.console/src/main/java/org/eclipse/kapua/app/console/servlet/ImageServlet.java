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

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.FileCleanerCleanup;
import org.apache.commons.io.FileCleaningTracker;
import org.eclipse.kapua.KapuaEntityNotFoundException;
import org.eclipse.kapua.KapuaIllegalAccessException;
import org.eclipse.kapua.KapuaUnauthenticatedException;
import org.eclipse.kapua.service.account.AccountOld;
import org.eclipse.kapua.service.account.AccountServiceOld;
import org.eclipse.kapua.service.authorization.AuthorizationServiceOLd;
import org.eclipse.kapua.service.authorization.Permission;
import org.eclipse.kapua.service.authorization.Permission.Action;
import org.eclipse.kapua.service.authorization.Permission.Domain;
import org.eclipse.kapua.service.config.EdcConfig;
import org.eclipse.kapua.service.locator.ServiceLocator;
import org.eclipse.kapua.service.user.UserOld;
import org.eclipse.kapua.service.user.UserServiceOld;
import org.eclipse.kapua.util.SubjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

public class ImageServlet extends HttpServlet
{
    private static final long serialVersionUID = -5016170117606322129L;
    private static Logger     s_logger         = LoggerFactory.getLogger(ImageServlet.class);

    DiskFileItemFactory       m_diskFileItemFactory;
    FileCleaningTracker       m_fileCleaningTracker;

    private static final int  qrCodeSize       = 134;

    @Override
    public void destroy()
    {
        super.destroy();

        s_logger.info("Servlet {} destroyed", getServletName());

        if (m_fileCleaningTracker != null) {
            s_logger.info("Number of temporary files tracked: " + m_fileCleaningTracker.getTrackCount());
        }
    }

    @Override
    public void init()
        throws ServletException
    {
        super.init();

        s_logger.info("Servlet {} initialized", getServletName());

        ServletContext ctx = getServletContext();
        m_fileCleaningTracker = FileCleanerCleanup.getFileCleaningTracker(ctx);

        int sizeThreshold = EdcConfig.getInstance().getFileUploadInMemorySizeThreshold();
        File repository = new File(System.getProperty("java.io.tmpdir"));

        s_logger.info("DiskFileItemFactory.DEFAULT_SIZE_THRESHOLD: {}", DiskFileItemFactory.DEFAULT_SIZE_THRESHOLD);
        s_logger.info("DiskFileItemFactory: using size threshold of: {}", sizeThreshold);

        m_diskFileItemFactory = new DiskFileItemFactory(sizeThreshold, repository);
        m_diskFileItemFactory.setFileCleaningTracker(m_fileCleaningTracker);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException
    {
        resp.setContentType("image/png");

        String reqPathInfo = req.getPathInfo();
        if (reqPathInfo == null) {
            resp.sendError(404);
            return;
        }

        s_logger.debug("req.getRequestURI(): {}", req.getRequestURI());
        s_logger.debug("req.getRequestURL(): {}", req.getRequestURL());
        s_logger.debug("req.getPathInfo(): {}", req.getPathInfo());

        if (reqPathInfo.equals("/2FAQRcode")) {
            doGetQRCode(req, resp);
        }
        else {
            resp.sendError(404);
            return;
        }
    }

    private void doGetQRCode(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException
    {
        try {
            String username = req.getParameter("username");
            if (username == null || username.isEmpty()) {
                throw new IllegalArgumentException("username");
            }

            String accountName = req.getParameter("accountName");
            if (accountName == null || accountName.isEmpty()) {
                throw new IllegalArgumentException("accountName");
            }

            String key = req.getParameter("key");
            if (key == null || key.isEmpty()) {
                throw new IllegalArgumentException("key");
            }

            String timestamp = req.getParameter("timestamp");
            if (timestamp == null || timestamp.isEmpty()) {
                throw new IllegalArgumentException("timestamp");
            }

            //
            ServiceLocator locator = ServiceLocator.getInstance(); 
            AccountServiceOld accountService = locator.getAccountService();
            AccountOld account = accountService.findByName(accountName);
            if (account == null) {
                throw new KapuaEntityNotFoundException(AccountOld.class, accountName);
            }
            
            //return value
            UserOld user = null;
            
            // check if user has self:view permission
    		UserOld currentUser = SubjectUtils.getCurrentUser();
    		if (currentUser == null) {
                throw new KapuaUnauthenticatedException();
            }
    		// check if the user has right to view his profile (self:view permission and the logged user is the same that it's trying to find then do the find)
    		AuthorizationServiceOLd as = locator.getAuthorizationService();
    		UserServiceOld userService = locator.getUserService();
    		Permission userViewPermission = new Permission(Domain.user, Action.view, account.getId());
    		if (!as.hasAccess(userViewPermission)) {
    			Permission selfViewPermission = new Permission(Domain.self, Action.view, account.getId());
    			Long userId = userService.getUserIdFromUsernameTrusted(username);
    			if (userId == null) {
                    throw new KapuaEntityNotFoundException(UserOld.class, username);
                }
    			if (! (currentUser.getAccountId() == account.getId() &&
    				currentUser.getId() == userId && //no needs for .longValue() implicit conversion Long to long done by the jvm 
    				as.hasAccess(selfViewPermission))) {
    				throw new KapuaIllegalAccessException(selfViewPermission.toString());
    			}
    			else {
    				user = userService.find(account.getId(), userId);
    			}
    		}
    		else {
    			user = userService.findByNameWithAccessInfo(account.getId(), username);
    		}
            
            if (user == null) {
                throw new KapuaEntityNotFoundException(UserOld.class, username);
            }
            
            // url to qr_barcode encoding
            StringBuilder sb = new StringBuilder();
            sb.append("otpauth://totp/")
              .append(username)
              .append("@")
              .append(accountName)
              .append("?secret=")
              .append(key);

            BitMatrix bitMatrix = new QRCodeWriter().encode(sb.toString(),
                                                            BarcodeFormat.QR_CODE,
                                                            qrCodeSize,
                                                            qrCodeSize);

            BufferedImage resultImage = buildImage(bitMatrix);
            ImageIO.write(resultImage, "png", resp.getOutputStream());

        } catch (IllegalArgumentException iae) {
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
            s_logger.error("Exception generating two-factor authentication key", e);
            throw new ServletException("Exception generating two-factor authentication key");
        }
    }

    private BufferedImage buildImage(BitMatrix bitMatrix)
    {
        BufferedImage qrCodeImage = MatrixToImageWriter.toBufferedImage(bitMatrix);
        BufferedImage resultImage = new BufferedImage(qrCodeSize,
                                                      qrCodeSize,
                                                      BufferedImage.TYPE_INT_RGB);

        Graphics g = resultImage.getGraphics();
        g.drawImage(qrCodeImage, 0, 0, new Color(232, 232, 232, 255), null);

        return resultImage;
    }
}

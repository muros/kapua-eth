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
package org.eclipse.kapua.app.console.server.util;

import org.eclipse.kapua.service.locator.ServiceLocator;
import org.eclipse.kapua.service.user.UserServiceOld;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class UserUtils {

    public static Logger s_logger = LoggerFactory.getLogger(UserUtils.class);

    public static String formatUser(long userId) {

        // FIXME: Internationalize
        if (userId == 0) {
            return "System User";
        }

        String name = null;
        try {

            ServiceLocator  locator = ServiceLocator.getInstance();
            UserServiceOld userService = locator.getUserService();
            name = userService.getUserDisplayName(userId);
        } catch (Exception e) {
            s_logger.warn("Error loading user display name: "+userId, e);

            // FIXME: Internationalize
            name = "Unknown "+userId;
        }

        return name;
    }
}

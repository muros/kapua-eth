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
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import com.eurotech.cloud.commons.model.query.EdcUsageInfo;

public abstract class UsageExporter {
    protected HttpServletResponse m_response;

    protected UsageExporter(HttpServletResponse response) {
        m_response = response;
    }

    public abstract void init(String account,
                              boolean byHour,
                              Date startDate,
                              Date endDate)
    throws ServletException, IOException;

    public abstract void append(List<EdcUsageInfo> messages, boolean byHour)
    throws ServletException, IOException;

    public abstract void close()
    throws ServletException, IOException;
}

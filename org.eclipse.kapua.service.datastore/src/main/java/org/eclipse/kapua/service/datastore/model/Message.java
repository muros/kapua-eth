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
package org.eclipse.kapua.service.datastore.model;

import java.util.Date;

import org.eclipse.kapua.service.datastore.Storable;

public interface Message extends Storable
{
    public String getUuid();
    public Date getTimestamp();
    public void setTimestamp(Date timestamp);
    public Date getReceivedOn();
    public void setReceivedOn(Date receivedOn);
    public Topic getTopic();
    public void setTopic(Topic topic);
    public String getFullTopic();
    public Payload getPayload();
    public void setPayload(Payload payload);
}

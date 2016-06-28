/*******************************************************************************
 * Copyright (c) 2011, 2016 Eurotech and/or its affiliates
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *      Eurotech - initial API and implementation
 *******************************************************************************/
package org.eclipse.kapua.service.datastore.internal.elasticsearch;

import java.util.Date;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "assetInfo")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class KapuaAssetInfo {
    private String asset;
    private Date timestamp;

    public KapuaAssetInfo() {
    }

    public KapuaAssetInfo(String asset, Date timestamp) {
        super();
        this.asset = asset;
        this.timestamp = timestamp;
    }

    @XmlElement(name = "asset")
    public String getAsset() {
        return asset;
    }

    @XmlElement(name = "lastMessageOn")
    public Date getLastMessageTimestamp() {
        return timestamp;
    }
}
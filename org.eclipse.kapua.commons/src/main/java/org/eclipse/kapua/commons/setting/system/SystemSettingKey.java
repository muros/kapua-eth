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
package org.eclipse.kapua.commons.setting.system;

import org.eclipse.kapua.commons.setting.SettingKey;

public enum SystemSettingKey implements SettingKey
{
    SYS_PROVISION_ACCOUNT_NAME("commons.sys.provision.account.name"),
    SYS_ADMIN_ACCOUNT("commons.sys.admin.account"),

    VERSION("commons.version"),
    BUILD_VERSION("commons.build.version"),
    BUILD_NUMBER("commons.build.number"),

    CHAR_ENCODING("character.encoding"),

    DB_URL("commons.db.url"),
    DB_USERNAME("commons.db.username"),
    DB_PASSWORD("commons.db.password"),
    DB_MAX_SIZE("commons.db.max_size"),
    DB_MIN_SIZE("commons.db.min_size"),
    DB_INCREMENT("commons.db.acquire_increment"),
    DB_TIMEOUT("commons.db.timeout"),
    DB_USESSL("commons.db.ssl"),
    DB_VERIFYSSL("commons.db.sslverify"),
    DB_TRUSTSTORE_URL("commons.db.trust.store.url"),
    DB_TRUSTSTORE_PWD("commons.db.trust.store.pwd"),

    BROKER_PROTOCOL("broker.protocol"),
    BROKER_DNS("broker.dns"),
    BROKER_PORT("broker.port"),

    OSGI_CONTEXT("commons.osgi.context");

    private String key;

    private SystemSettingKey(String key)
    {
        this.key = key;
    }

    public String key()
    {
        return key;
    }
}

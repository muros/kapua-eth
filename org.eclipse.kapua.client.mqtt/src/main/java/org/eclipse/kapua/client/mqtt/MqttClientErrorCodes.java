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
package org.eclipse.kapua.client.mqtt;

import org.eclipse.kapua.KapuaErrorCode;

public enum MqttClientErrorCodes implements KapuaErrorCode
{
    GZIP_DECOMPRESSION_ERROR,
    PROTO_ENCODING_ERROR,

    CLIENT_PUBLISH_ERROR,
    CLIENT_SUBSCRIBE_ERROR,
    CLIENT_UNSUBSCRIBE_ERROR,

    CLIENT_CONNECTION_LOST,
}

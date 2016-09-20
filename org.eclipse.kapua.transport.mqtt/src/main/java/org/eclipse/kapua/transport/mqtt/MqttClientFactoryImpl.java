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
package org.eclipse.kapua.transport.mqtt;

import org.eclipse.kapua.transport.TransportClientFactory;
import org.eclipse.kapua.transport.message.mqtt.MqttMessage;
import org.eclipse.kapua.transport.message.mqtt.MqttPayload;
import org.eclipse.kapua.transport.message.mqtt.MqttTopic;

public class MqttClientFactoryImpl implements TransportClientFactory<MqttTopic, MqttPayload, MqttMessage, MqttMessage, MqttFacade, MqttClientConnectionOptions>
{
    @Override
    public MqttFacade getFacade()
        throws Exception
    {
        return new MqttFacade();
    }

    @Override
    public MqttClientConnectionOptions newConnectOptions()
    {
        return new MqttClientConnectionOptions();
    }

}

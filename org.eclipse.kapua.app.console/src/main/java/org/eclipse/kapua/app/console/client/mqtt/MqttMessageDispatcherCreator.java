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
package org.eclipse.kapua.app.console.client.mqtt;

import org.eclipse.kapua.app.console.shared.model.GwtSession;

public class MqttMessageDispatcherCreator {

    /**
     * Instance of the {@link MqttMessageDispatcher}
     * */
    private static MqttMessageDispatcher mqttMessageDispatcher;

    /**
     * Default constructor<br>
     * Build the instance of {@link MqttMessageDispatcher} if it is null
     */
    public MqttMessageDispatcherCreator(GwtSession currentSession) {
        mqttMessageDispatcher = new MqttMessageDispatcher(currentSession);
    }

    /**
     * Get the instance of the {@link MqttMessageDispatcher}
     * @return
     */
    public MqttMessageDispatcher getMqttMessageDispatcher() {
        return mqttMessageDispatcher;
    }
}

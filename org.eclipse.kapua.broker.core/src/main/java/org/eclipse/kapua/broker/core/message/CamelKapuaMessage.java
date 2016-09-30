/*******************************************************************************
 * Copyright (c) 2011, 2016 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.broker.core.message;

import org.eclipse.kapua.broker.core.plugin.ConnectorDescriptor;
import org.eclipse.kapua.message.KapuaMessage;
import org.eclipse.kapua.model.id.KapuaId;

@SuppressWarnings("rawtypes")
public class CamelKapuaMessage<M extends KapuaMessage> {
	
	private M message;
	private KapuaId connectionId;
	private ConnectorDescriptor connectorDescriptor;
	
	public CamelKapuaMessage(M message, KapuaId connectionId, ConnectorDescriptor connectorDescriptor) {
		this.connectionId = connectionId;
		this.message = message;
		this.connectorDescriptor = connectorDescriptor;
	}

	public M getMessage() {
		return message;
	}

	public void setMessage(M message) {
		this.message = message;
	}

	public KapuaId getConnectionId() {
		return connectionId;
	}

	public void setConnectionId(KapuaId connectionId) {
		this.connectionId = connectionId;
	}

	public ConnectorDescriptor getConnectorDescriptor() {
		return connectorDescriptor;
	}

	public void setConnectorDescriptor(ConnectorDescriptor connectorDescriptor) {
		this.connectorDescriptor = connectorDescriptor;
	}

}

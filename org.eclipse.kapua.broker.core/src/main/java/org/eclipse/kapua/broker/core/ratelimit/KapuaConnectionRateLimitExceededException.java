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
package org.eclipse.kapua.broker.core.ratelimit;

import org.eclipse.kapua.KapuaErrorCode;
import org.eclipse.kapua.KapuaException;

//FIXME-KAPUA: Moved to its used projects or remove
public class KapuaConnectionRateLimitExceededException extends KapuaException {
	
	private static final long serialVersionUID = 6544478368819277901L;
	
	public KapuaConnectionRateLimitExceededException(String clientId, String userName, double rateLimit) {
		super(new KapuaErrorCode() {
			
			@Override
			public String name() {
				return "ConnectionRateLimitExceeded";
			}
		}, null, new Object[]{Double.valueOf(rateLimit), clientId, userName, "connection"});
	}

}
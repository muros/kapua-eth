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
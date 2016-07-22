package org.eclipse.kapua.broker.core.locator;

import org.eclipse.kapua.model.KapuaObjectFactory;
import org.eclipse.kapua.service.KapuaService;

public interface BrokerServiceDirectory {

	public <T extends KapuaService> T lookupService(Class<T> service);

	public <T extends KapuaObjectFactory> T lookupFactory(Class<T> factory);
	
}